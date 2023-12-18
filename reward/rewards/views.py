from django.shortcuts import render
from rest_framework import viewsets

from rewards.models import Reward
from rewards.serializers import RewardSerializer


class RewardViewSet(viewsets.ModelViewSet):
    queryset = Reward.objects.all()
    serializer_class = RewardSerializer