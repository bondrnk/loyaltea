from django.contrib import admin

from .models import BuyFulfillment, TagFulfillment

admin.site.register(BuyFulfillment)
admin.site.register(TagFulfillment)