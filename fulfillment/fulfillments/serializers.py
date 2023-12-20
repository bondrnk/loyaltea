from rest_framework import serializers

from fulfillments.models import TagFulfillment, BuyFulfillment


class TagFulfillmentSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = TagFulfillment
        fields = '__all__'


class BuyFulfillmentSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = BuyFulfillment
        fields = '__all__'
