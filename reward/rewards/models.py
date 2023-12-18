from django.db import models

class Reward(models.Model):
    points = models.FloatField()
    tenant = models.CharField()
    user = models.CharField()