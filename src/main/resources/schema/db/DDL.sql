set client_encoding to 'utf8';

-- create schema 
create database docomo_example_api;

-- enums table
create table constant_enum (
"enum_code" integer not null, 
"enum_name" varchar(50)  default '' not null, 
"enum_type" varchar(50)  default '' not null,
"enum_label" varchar(50)  default '' not null, 
"enum_desc" varchar(250)  default '' not null, 
"disabled" boolean default false
);
alter table constant_enum add constraint "PK_constant_enum_code" primary key ("enum_code");


--payment_transaction
create sequence if not exists payment_transaction_id_seq increment by 50 start with 1 cache 1000 no cycle;
create table payment_transaction (
"id" integer not null,
"phone_number" varchar(20) not null,
"msisdn" varchar(20),
"product_id" varchar(200) not null,
"transaction_id" varchar(36) not null, 
"order_id" varchar(20), -- returned by the operators
"amount" double precision not null,
"currency" integer not null,
"kind" varchar(50) not null,
"refund_reason" varchar(4000),
"failure_message" varchar(4000),
"status" varchar(20),
"created_at" timestamp not null,
"updated_at" timestamp not null
);
alter table payment_transaction add constraint "fk_pay_transaction_const_enum_1" foreign key("currency") references constant_enum("enum_code");
alter table payment_transaction add constraint "pk_payment_transaction_id" primary key ("id");
alter table payment_transaction add constraint "unique_tx_id" unique ("transaction_id");
create index "payment_trasaction_idx" on payment_transaction ("phone_number", "product_id", "order_id");


-- operators table
create sequence if not exists international_phone_codes_id_seq;
create table international_phone_codes (
"id" integer not null, 
"country" varchar(50) not null,
"currency" integer not null, 
"area_code" varchar(250)  not null
);
alter table international_phone_codes add constraint "PK_international_phone_codes_id" primary key ("id");
alter table international_phone_codes add constraint "unique_area_code" unique ("area_code");
alter table international_phone_codes add constraint "FK_int_phone_codes_to_constant_enum" foreign key("currency") references constant_enum("enum_code");

