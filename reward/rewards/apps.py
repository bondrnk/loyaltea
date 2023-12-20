import os
import threading

from django.apps import AppConfig


class RewardsConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'rewards'

    def ready(self):
        if not threading.current_thread().daemon and os.environ.get('RUN_MAIN'):
            from rewards import consumers

            thread = threading.Thread(name="kafka-consumers", target=consumers.run)
            thread.daemon = True
            thread.start()
