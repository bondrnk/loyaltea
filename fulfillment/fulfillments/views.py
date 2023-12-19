from django.shortcuts import render
from rest_framework import viewsets

from fulfillments.models import Fulfillment
from fulfillments.serializers import FulfillmentSerializer


class FulfillmentViewSet(viewsets.ModelViewSet):
    queryset = Fulfillment.objects.all()
    serializer_class = FulfillmentSerializer