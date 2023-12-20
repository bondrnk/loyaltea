from kafka import KafkaProducer

from Loyaltea.settings import KAFKA_BOOTSTRAP_SERVERS

producer = KafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
                         key_serializer=str.encode,
                         value_serializer=str.encode)
