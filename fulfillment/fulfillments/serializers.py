from rest_framework import serializers

from fulfillments.models import Fulfillment


class FulfillmentSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Fulfillment
        fields = ['name', 'tenant', 'campaign', 'type']
