from dataclasses import dataclass
from uuid import UUID

from dataclasses_json import LetterCase, DataClassJsonMixin, config


@dataclass
class FulfillmentEvent(DataClassJsonMixin):
    dataclass_json_config = config(letter_case=LetterCase.CAMEL)["dataclasses_json"]
    tenant: str
    user_id: UUID
    fulfillment_id: UUID


@dataclass
class BuyEvent(DataClassJsonMixin):
    dataclass_json_config = config(letter_case=LetterCase.CAMEL)["dataclasses_json"]
    tenant: str
    user_id: UUID
    product: str
    amount: float
