create table if not exists campaigns
(
    tenant       varchar not null,
    name         varchar not null,
    fulfillments uuid[]  not null,
    rewards      uuid[]  not null,
    id           uuid    not null,
    primary key (id)
);

create table if not exists "user-campaigns"
(
    tenant       varchar not null,
    campaignId   uuid    not null,
    userId       uuid    not null,
    fulfillments uuid[]  not null,
    primary key (campaignId, userId)
);