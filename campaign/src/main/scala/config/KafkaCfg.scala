package loyaltea
package config

import zio.ZLayer
import zio.kafka.consumer.*
import zio.kafka.producer.*

case class KafkaCfg(
    host: String,
    port: Int,
) {
  val bootstrapServers: List[String] = List(s"$host:$port")
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
