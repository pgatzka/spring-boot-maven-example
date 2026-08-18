create table greeting
(
    id         bigint                   not null,
    uuid       uuid                     not null unique,
    version    bigint                   not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null,
    author     varchar(30)              not null,
    message    varchar(200),
    subject    varchar(30)              not null,
    constraint pk_greeting__id primary key (id)
);