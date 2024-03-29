from dataclasses import dataclass
from uuid import UUID

from dataclasses_json import LetterCase, DataClassJsonMixin, config


@dataclass
class RewardEvent(DataClassJsonMixin):
    dataclass_json_config = config(letter_case=LetterCase.CAMEL)["dataclasses_json"]
    tenant: str
    reward_id: UUID
    user_id: UUID


@dataclass
class RewardNotifyEvent(DataClassJsonMixin):
    dataclass_json_config = config(letter_case=LetterCase.CAMEL)["dataclasses_json"]
    tenant: str
    reward_id: UUID
    user_id: UUID
    points: float
