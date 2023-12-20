# Generated by Django 5.0 on 2023-12-20 12:39

import uuid
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Reward',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('tenant', models.CharField()),
                ('reward_id', models.UUIDField(default=uuid.uuid4, editable=False)),
                ('reward_points', models.FloatField(default=0.0)),
            ],
        ),
        migrations.CreateModel(
            name='UserReward',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('tenant', models.CharField()),
                ('user_id', models.UUIDField(editable=False)),
                ('points', models.FloatField(default=0.0)),
            ],
        ),
    ]
