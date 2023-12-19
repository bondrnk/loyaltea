import os
import threading

from django.apps import AppConfig

from fulfillments import consumers

class FulfillmentsConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'fulfillments'

    def ready(self):
        if not threading.current_thread().isDaemon() and os.environ.get('RUN_MAIN'):
            thread = threading.Thread(name="kafka-consumers", target=consumers.run)
            thread.daemon = True
            thread.start()

            # producer.send('fulfillment-notify', {'tenant': 'meiwei', 'user': 'bondrns' })
            # producer.flush()
