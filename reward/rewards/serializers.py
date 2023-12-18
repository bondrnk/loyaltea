from rest_framework import serializers

from rewards.models import Reward


class RewardSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Reward
        fields = ['points', 'tenant', 'user']
