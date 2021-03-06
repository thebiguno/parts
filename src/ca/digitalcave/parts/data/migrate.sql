--liquibase formatted sql

--changeset wj:create_account
create table account (
	account_id smallint primary key,
	identifier varchar(255) not null unique,
	email varchar(255) not null unique,
	secret varchar(255),
	activation_key varchar(36) unique,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table account;

--changeset wj:insert_admin
insert into account values (0, 'admin', 'admin@example.com', 'password', null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--rollback delete from account where account_id = 0;

--changeset wj:create_category
create table category (
	category_id smallint primary key,
	parent_id smallint references category (category_id) on delete restrict,
	account_id integer not null references account (account_id) on delete cascade,
	name varchar(255) not null,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table category;

--changeset wj:create_part
create table part (
	part_id smallint primary key,
	category_id smallint not null references category (category_id) on delete restrict,
	available smallint not null default 0, -- quantity on hand
	minimum smallint not null default 0, -- quantity desired
	part_no varchar(255) not null,
	description varchar(255),
	notes varchar(2048),
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table part;

--changeset wj:create_attribute
create table attribute (
	attribute_id int primary key,
	part_id smallint not null references part (part_id) on delete cascade, 
	name varchar(255) not null, 
	val varchar(255) not null, 
	href varchar(1024), 
	mime_type varchar(255),
	data oid,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table attribute;

--changeset wj:create_attribute_trigger
create trigger t_attribute before update or delete on attribute
	for each row execute procedure lo_manage(data);
