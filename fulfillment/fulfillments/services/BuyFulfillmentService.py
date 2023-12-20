import logging

from kafka import KafkaProducer

from fulfillments.events import BuyEvent, FulfillmentEvent
from fulfillments.models import BuyFulfillment
from fulfillments.producers import producer


class BuyFulfillmentService():
    def __init__(self, producer: KafkaProducer):
        self.producer = producer
        self.logger = logging.getLogger("services.buy-fulfillment")

    def processBuyEvent(self, buy_event: BuyEvent):
        try:
            user_id = buy_event.user_id
            tenant = buy_event.tenant
            tenant_fulfillments = BuyFulfillment.objects.filter(tenant=tenant)

            for fulfillment in tenant_fulfillments:
                if (buy_event.amount >= fulfillment.amount and
                        (fulfillment.product is None or fulfillment.product == buy_event.product)):

                    fulfillment_id = fulfillment.fulfillment_id
                    fulfillment_event = FulfillmentEvent(tenant=tenant, user_id=user_id, fulfillment_id=fulfillment_id)
                    producer.send('fulfillment', fulfillment_event.to_json(), f"{fulfillment_id=}/{user_id=}")
                    producer.flush()
                    self.logger.info(f"Sent fulfillment {fulfillment_event=}")

        except Exception as err:
            self.logger.error(f"Error processing {buy_event=} {err=}")