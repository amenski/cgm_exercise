--currency types
INSERT INTO CONSTANT_ENUM(ENUM_NAME, ENUM_CODE, ENUM_TYPE, ENUM_LABEL, ENUM_DESC, DISABLED) VALUES('EURO','1000','CURRENCY_TYPE', 'Euro', 'Currrency in EU states', false);
INSERT INTO CONSTANT_ENUM(ENUM_NAME, ENUM_CODE, ENUM_TYPE, ENUM_LABEL, ENUM_DESC, DISABLED) VALUES('US DOLLAR','1001','CURRENCY_TYPE', 'Dollar', 'US dollar', false);
INSERT INTO CONSTANT_ENUM(ENUM_NAME, ENUM_CODE, ENUM_TYPE, ENUM_LABEL, ENUM_DESC, DISABLED) VALUES('CANADA DOLLAR','1002','CURRENCY_TYPE', 'Dollar', 'Canadian dollar', false);

-- international codes
insert into international_phone_codes (id, country, currency, area_code) values('1', 'ITA', 1000, '+39');
insert into international_phone_codes (id, country, currency, area_code) values('2', 'USA', 1001, '+1');
insert into international_phone_codes (id, country, currency, area_code) values('3', 'CAN', 1002, '+1');
insert into international_phone_codes (id, country, currency, area_code) values('4', 'ETH', 1003, '+251');