namespace smithy4s.campaign

use alloy#UUID
use alloy#simpleRestJson
use smithy4s.meta#packedInputs

@packedInputs
@simpleRestJson
service CampaignService {
    version: "0.0.1",
    operations: [Create, List]
}

@http(method: "POST", uri: "/campaign", code: 200)
operation Create {
    input: CreateCampaignRequest,
    output: CampaignDTO
}

@http(method: "GET", uri: "/campaign", code: 200)
operation List {
    output: CampaignListResponse
}

structure CreateCampaignRequest {
    @required
    tenant: String
    @required
    name: String
    @required
    fulfillments: Fulfillments
    @required
    rewards: Rewards
}

list Fulfillments {
    member: UUID
}

list Rewards {
    member: UUID
}

structure CampaignDTO {
    @required
    id: UUID
    @required
    tenant: String
    @required
    name: String
    @required
    fulfillments: Fulfillments
    @required
    rewards: Rewards
}

structure CampaignListResponse {
    @required
    campaigns: CampaignList
}

list CampaignList {
    member: CampaignDTO
}