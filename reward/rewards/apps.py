import os
import threading

from django.apps import AppConfig

from rewards import consumers


class RewardsConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'rewards'

    def ready(self):
        if not threading.current_thread().isDaemon() and os.environ.get('RUN_MAIN'):
            thread = threading.Thread(name="kafka-consumers", target=consumers.run)
            thread.daemon = True
            thread.start()

            # producer.send('reward-notify', {'tenant': 'meiwei', 'user': 'bondrns' })
            # producer.flush()
