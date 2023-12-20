from django.contrib import admin

from .models import Reward, UserReward

admin.site.register(Reward)
admin.site.register(UserReward)