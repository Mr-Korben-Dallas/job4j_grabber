create table posts
(
    id          bigserial
        constraint posts_pk
            primary key,
    name        text      not null,
    description text      not null,
    link        text      not null,
    created_at  timestamp not null
);

create unique index posts_link_uindex
    on posts (link);
