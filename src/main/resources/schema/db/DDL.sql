set client_encoding to 'utf8';

-- create schema 
create database docomo_example_api;

-- create sequence
create sequence if not exists payment_transaction_id_seq increment by 50 start with 1 cache 1000 no cycle;

--create table
create table payment_transaction (
"id" integer not null,
"phone_number" varchar(20) not null,
"product_id" varchar(200) not null,
"transaction_id" varchar(36) not null, 
"amount" double precision not null,
"transaction_begin" timestamp not null,
"transaction_end" timestamp not null
);

--constraints
alter table payment_transaction add constraint "pk_payment_transaction_id" primary key ("id");
alter table payment_transaction add constraint "unique_tx_id" unique ("transaction_id");

create index "payment_trasaction_idx" on payment_transaction ("phone_number", "product_id");
