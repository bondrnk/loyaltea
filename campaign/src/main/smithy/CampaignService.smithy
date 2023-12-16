namespace smithy4s.campaign

use alloy#simpleRestJson

@simpleRestJson
service CampaignService {
    version: "0.0.1",
    operations: [Create]
}

@http(method: "POST", uri: "/{name}", code: 200)
operation Create {
    input: CreateCampaignRequest,
    output: Campaign
}

structure CreateCampaignRequest {
    @httpLabel
    @required
    name: String,
}

structure Campaign {
    @required
    id: String
    @required
    name: String
}