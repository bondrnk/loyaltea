namespace smithy4s.campaign

use alloy#UUID
use alloy#simpleRestJson

@simpleRestJson
service CampaignService {
    version: "0.0.1",
    operations: [Create, List]
}

@http(method: "POST", uri: "/campaign/{name}", code: 200)
operation Create {
    input: CreateCampaignRequest,
    output: CampaignDTO
}

@http(method: "GET", uri: "/campaign", code: 200)
operation List {
    output: CampaignListResponse
}

structure CreateCampaignRequest {
    @httpLabel
    @required
    name: String,
    @required
    fulfillments: Fulfillments
}

list Fulfillments {
    member: UUID
}


structure CampaignDTO {
    @required
    id: UUID
    @required
    name: String
    @required
    fulfillments: Fulfillments
}

structure CampaignListResponse {
    @required
    campaigns: CampaignList
}

list CampaignList {
    member: CampaignDTO
}