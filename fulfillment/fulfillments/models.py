from django.db import models

class FulfillmentTypes(models.TextChoices):
    WAGER = "wager"
    TAG = "tag"


class Fulfillment(models.Model):
    name = models.CharField()
    tenant = models.CharField()
    campaign = models.UUIDField()
    type = models.CharField(choices=FulfillmentTypes)