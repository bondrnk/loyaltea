from django.shortcuts import render
from rest_framework import viewsets

from rewards.models import Reward, UserReward
from rewards.serializers import RewardSerializer, UserRewardSerializer


class RewardViewSet(viewsets.ModelViewSet):
    queryset = Reward.objects.all()
    serializer_class = RewardSerializer


class UserRewardViewSet(viewsets.ModelViewSet):
    queryset = UserReward.objects.all()
    serializer_class = UserRewardSerializer