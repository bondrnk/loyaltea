from django.shortcuts import render
from rest_framework import viewsets

from fulfillments.models import BuyFulfillment, TagFulfillment
from fulfillments.serializers import TagFulfillmentSerializer, BuyFulfillmentSerializer


class BuyFulfillmentViewSet(viewsets.ModelViewSet):
    queryset = BuyFulfillment.objects.all()
    serializer_class = BuyFulfillmentSerializer

class TagFulfillmentViewSet(viewsets.ModelViewSet):
    queryset = TagFulfillment.objects.all()
    serializer_class = TagFulfillmentSerializer