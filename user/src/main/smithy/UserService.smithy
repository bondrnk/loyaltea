namespace smithy4s.user

use alloy#UUID
use alloy#simpleRestJson
use smithy4s.meta#packedInputs

@simpleRestJson
@packedInputs
service UserService {
    version: "0.0.1",
    operations: [Create, List]
}

@http(method: "POST", uri: "/user/", code: 200)
operation Create {
    input: CreateUserRequest,
    output: UserDTO
}

@http(method: "GET", uri: "/user", code: 200)
operation List {
    output: UserListResponse
}

structure CreateUserRequest {
    @required
    fullname: String
    @required
    username: String
}



structure UserDTO {
    @required
    id: UUID
    @required
    fullname: String
    @required
    username: String
}

structure UserListResponse {
    @required
    users: UserList
}

list UserList {
    member: UserDTO
}