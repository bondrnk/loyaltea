package loyaltea
package producers

import config.*
import model.RewardEvent
import producers.Producers.deriveJsonSerde

import io.circe.*
import io.circe.syntax.*
import zio.*
import zio.kafka.*
import zio.kafka.producer.*
import zio.kafka.serde.*

final class Producers(kafkaCfg: KafkaCfg) {
    def produceReward(event: RewardEvent): Task[Unit] =
      Producer.produce("reward", event.key, event, Serde.string, deriveJsonSerde[RewardEvent])
        .provide(kafkaCfg.toProducerLayer)
        .unit
}

object Producers {
  def deriveJsonSerde[A: Codec]: Serializer[Any, A] =
    Serde.string.contramapM((a: A) => ZIO.succeed(a.asJson.noSpaces))
}


