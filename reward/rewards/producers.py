from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers=['localhost:9092'],
                         key_serializer=str.encode,
                         value_serializer=str.encode)
