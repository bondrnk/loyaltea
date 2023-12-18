import json

from kafka import KafkaConsumer


def run():
    consumer = KafkaConsumer('reward',
                             group_id='reward-group',
                             bootstrap_servers=['localhost:9092'])

    print("Reward Consumer started")

    for message in consumer:
        # message value and key are raw bytes -- decode if necessary!
        # e.g., for unicode: `message.value.decode('utf-8')`
        print("%s:%d:%d: key=%s value=%s" % (message.topic, message.partition,
                                             message.offset, message.key,
                                             message.value))