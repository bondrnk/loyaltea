import uuid

from django.db import models


class Fulfillment(models.Model):
    tenant = models.CharField()
    fulfillment_id = models.UUIDField(default=uuid.uuid4, editable=False)
    name = models.CharField()

    class Meta:
        abstract = True


class BuyFulfillment(Fulfillment):
    product = models.CharField(blank=True)
    amount = models.FloatField()


class TagFulfillment(Fulfillment):
    tag = models.CharField()
