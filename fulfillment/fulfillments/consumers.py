import logging
import threading
from typing import Callable

from kafka import KafkaConsumer

from fulfillments.events import BuyEvent
from fulfillments.services.BuyFulfillmentService import BuyFulfillmentService


class ForkedKafkaConsumer():
    def __init__(self, name: str, consumer: KafkaConsumer, process: Callable[[any], any]):
        self.consumer = consumer
        self.name = name
        self.logger = logging.getLogger("consumers." + name)
        self.process = process

    def run(self):
        self.logger.info(self.name + " Consumer started")

        for message in self.consumer:
            try:
                self.process(message)
            except Exception as err:
                self.logger.error(f"Error consuming message {message=} {err=}")

    def start(self):
        thread = threading.Thread(name="kafka-consumers", target=self.run)
        thread.daemon = True
        thread.start()


class BuyFulfillmentConsumer():
    def __init__(self, service: BuyFulfillmentService):
        self.service = service
        consumer = KafkaConsumer('buy', group_id='buy-fulfillment-group', bootstrap_servers=['localhost:9092'])
        self.consumer = ForkedKafkaConsumer("buy-fulfillment", consumer, self.process)

    def process(self, message):
        buy_event = BuyEvent.from_json(message.value)
        self.service.processBuyEvent(buy_event)

    def start(self):
        self.consumer.start()
