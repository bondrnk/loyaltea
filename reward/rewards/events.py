# class User:
#     def __init__(self, tenant: str, rewardId: str, userId: str) -> None:
#         self.tenant = tenant
#         self.rewardId = rewardId
#         self.userId = userId
from dataclasses import dataclass
from uuid import UUID

from dataclasses_json import dataclass_json


@dataclass_json
@dataclass
class RewardEvent(object):
    tenant: str
    reward_id: UUID
    user_id: UUID


@dataclass_json
@dataclass
class RewardNotifyEvent(object):
    tenant: str
    reward_id: UUID
    user_id: UUID
    points: float

