from rest_framework import serializers

from rewards.models import Reward, UserReward


class RewardSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Reward
        fields = ['tenant', 'reward_id', 'reward_points']

class UserRewardSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = UserReward
        fields = ['tenant', 'user_id', 'points']
