package loyaltea
package repo

import error.*
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.util.chaining.scalaUtilChainingOps

import io.circe
import io.circe.Codec
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import os.Path
import zio.*

sealed trait DummyRepo[I, O] {
  ZIO
  def list: Task[List[O]]
  def create(id: I, entity: O): Task[O]
  def read(id: I): Task[Option[O]]
  def update(id: I, change: O => O): Task[O]
  def delete(id: I): Task[Unit]
}

object DummyRepo {
  type PersistTo = Option[Path]

  final case class Json[I, O](
      key: O => I,
      persistTo: PersistTo = None,
  )(implicit C: Codec[O], CT: ClassTag[O])
      extends DummyRepo[I, O] {
    private def readJsonStore: Map[I, O] =
      persistTo
        .filter(os.exists(_))
        .map(os.read(_))
        .flatMap(
          parse(_)
            .flatMap(_.asJson.as[List[circe.Json]])
            .map(_.flatMap(_.as[O].tap(_.left.foreach(error => println(s"$CT:$error"))).toOption))
            .toOption
        )
        .map(_.map(o => key(o) -> o).toMap)
        .getOrElse(Map.empty)

    persistTo
      .map(Seq(_))
      .foreach(
        os.watch.watch(_,
                       _ => {
                         Unsafe.unsafe { implicit unsafe =>
                           Runtime.default.unsafe.run(store.set(readJsonStore))
                         }
                       },
        )
      )

    private val writeLock: Semaphore  = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(Semaphore.make(1)).getOrThrow()
    }
    private val store: Ref[Map[I, O]] = Unsafe.unsafe { implicit unsafe =>
      Runtime.default.unsafe.run(Ref.make(readJsonStore)).getOrThrow()
    }
    private val persist: UIO[Unit]    = persistTo.fold(ZIO.unit) { location =>
      store.get
        .map(_.values.asJson.deepDropNullValues.spaces2)
        .map(os.write.over(location, _, null, createFolders = true))
    }

    def modifyStore[A](modify: Ref[Map[I, O]] => Task[A]): Task[A] =
      writeLock.withPermit(modify(store) <* persist)

    override def list: Task[List[O]] =
      store.get.map(_.values.toList)

    override def read(id: I): Task[Option[O]] =
      store.get.map(_.get(id))

    override def create(id: I, entity: O): Task[O] =
      modifyStore(_.update(_ + (id -> entity)).as(entity))

    override def delete(id: I): Task[Unit] =
      modifyStore(_.update(_.removed(id)))

    override def update(id: I, change: O => O): Task[O] =
      modifyStore(_.modify { map =>
        val mutableMap = map.to(mutable.Map)
        val result     = mutableMap.updateWith(id)(_.map(change))
        (result, mutableMap.toMap)
      }).someOrFail(error"$CT.update.failed")

    def update(entity: O): Task[O] = update(key(entity), _ => entity)
    def create(entity: O): Task[O] = create(key(entity), entity)
  }
}
