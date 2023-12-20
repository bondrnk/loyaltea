import os
import threading

from django.apps import AppConfig


class FulfillmentsConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'fulfillments'

    def ready(self):
        if not threading.current_thread().daemon and os.environ.get('RUN_MAIN'):
            from fulfillments.consumers import BuyFulfillmentConsumer
            from fulfillments.producers import producer
            from fulfillments.services.BuyFulfillmentService import BuyFulfillmentService

            buyFulfillmentService = BuyFulfillmentService(producer)
            buyFulfillmentConsumer = BuyFulfillmentConsumer(buyFulfillmentService)
            buyFulfillmentConsumer.start()
