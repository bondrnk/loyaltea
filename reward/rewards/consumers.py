import logging

from kafka import KafkaConsumer

from rewards.events import RewardEvent, RewardNotifyEvent
from rewards.models import Reward, UserReward
from rewards.producers import producer


def run():
    logger = logging.getLogger("consumers.reward")
    consumer = KafkaConsumer('reward',
                             group_id='reward-group',
                             bootstrap_servers=['localhost:9092'])

    logger.info("Reward Consumer started")

    for message in consumer:
        try:
            event = RewardEvent.from_json(message.value)
            reward_id = event.reward_id
            user_id = event.user_id
            reward = Reward.objects.get(reward_id=reward_id, tenant=event.tenant)
            (user_reward, created) = UserReward.objects.get_or_create(user_id=user_id, tenant=event.tenant)
            user_reward.points += reward.reward_points
            user_reward.save()
            logger.info(f"Added reward for {event=}")

            reward_notify_event = RewardNotifyEvent(tenant=event.tenant, user_id=user_id, reward_id=reward_id,
                                                    points=reward.reward_points)

            producer.send('reward-notify', reward_notify_event.to_json(), f"{reward_id=}/{user_id=}")
            producer.flush()
            logger.info(f"Sent reward notification {reward_notify_event=}")
        except Exception as err:
            logger.error(f"Error consuming reward event {message=} {err=}")
