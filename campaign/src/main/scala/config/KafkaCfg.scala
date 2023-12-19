package loyaltea
package config

import zio.ZLayer
import zio.kafka.consumer.*
import zio.kafka.producer.*

case class KafkaCfg(
    bootstrapServers: List[String]
) {
  def toConsumerLayer(groupId: String) = ZLayer.scoped(
    Consumer.make(
      ConsumerSettings(bootstrapServers).withGroupId(groupId)
    )
  )
  
  def toProducerLayer = ZLayer.scoped(
    Producer.make(
      ProducerSettings(bootstrapServers)
    )
  )
}
