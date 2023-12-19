package loyaltea
package consumers

import config.KafkaCfg

import io.circe.Codec
import io.circe.parser.parse
import io.circe.syntax.EncoderOps
import consumers.Consumers.*

import io.circe.generic.semiauto
import loyaltea.model.{FulfillmentEvent, RewardEvent}
import zio.*
import zio.kafka.consumer.*
import zio.kafka.serde.*
import zio.stream.ZStream

import java.util.UUID

final class Consumers(config: KafkaCfg, campaignApi: CampaignApi) {
  def processFulfillment(event: FulfillmentEvent): Task[Unit] =
    ZIO.logInfo(s"Processing $event") *> campaignApi.processFulfillment(event)

  val fulfillmentConsumer: ZStream[Consumer, Nothing, Unit] =
    Consumer
      .plainStream(Subscription.topics("fulfillment"), Serde.uuid.asTry, deriveJsonSerde[FulfillmentEvent].asTry)
      .tap(_.value.map(processFulfillment).getOrElse(ZIO.unit))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .catchAll(error =>
        ZStream.logError(s"Fulfillment consumer failed with $error") *> fulfillmentConsumer
      )
      .drain


  def startAll: Task[Unit] =
    fulfillmentConsumer.provideLayer(config.toConsumerLayer("fulfillment-group"))
      .runDrain

  def startAllFork: Task[Unit] = startAll.fork.unit
}

object Consumers {

  def deriveJsonSerde[A: Codec]: Deserializer[Any, A] =
    Serde.string
      .mapM(string => ZIO.fromEither(parse(string).flatMap(_.as[A].toTry.toEither)).mapError(new IllegalArgumentException(_)))
}
