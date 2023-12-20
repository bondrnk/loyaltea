import uuid

from django.db import models


class UserReward(models.Model):
    tenant = models.CharField()
    user_id = models.UUIDField(editable=False)
    points = models.FloatField(default=0.0)


class Reward(models.Model):
    tenant = models.CharField()
    reward_id = models.UUIDField(default=uuid.uuid4, editable=False)
    reward_points = models.FloatField(default=0.0)
