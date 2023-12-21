create table if not exists campaigns
(
    tenant       varchar not null,
    name         varchar not null,
    fulfillments uuid[]  not null,
    rewards      uuid[]  not null,
    id           uuid    not null,
    primary key (id)
);

create table if not exists user_campaigns
(
    tenant       varchar not null,
    campaign_id  uuid    not null,
    user_id      uuid    not null,
    fulfillments uuid[]  not null,
    primary key (campaign_id, user_id)
);