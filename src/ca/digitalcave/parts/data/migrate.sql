--liquibase formatted sql

--changeset wj:create_account
create table account (
	account_id smallint primary key,
	identifier varchar(255) not null unique,
	email varchar(255) not null,
	secret varchar(255) not null,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table account;

--changeset wj:insert_admin
insert into account values (0, 'Administrator', 'admin@example.com', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--rollback delete from account where account_id = 0;

--changeset wj:create_category
create table category (
	category_id smallint primary key,
	account_id integer not null references account (account_id) on delete cascade,
	name varchar(255) not null unique,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table category;

--changeset wj:create_family
create table family (
	family_id smallint primary key,
	category_id smallint not null references category (category_id) on delete restrict,
	name varchar(255) not null unique,
	created_at timestamp not null,
	modified_at timestamp not null
);
--rollback drop table family;

--changeset wj:create_part
create table part (
	part_id smallint primary key,
	family_id smallint not null references family (family_id) on delete restrict,
	available smallint not null default 0, -- quantity on hand
	minimum smallint not null default 0, -- quantity desired
	part_no varchar(255) not null,
	description varchar(255),
	notes varchar(255)
);
--rollback drop table part;

--changeset wj:create_atttribute
create table attribute (
	attribute_id int primary key,
	part_id smallint not null references part (part_id) on delete cascade, 
	name varchar(255) not null, 
	val varchar(255) not null, 
	href varchar(255), 
	sort smallint default 0
);
--rollback drop table property;

--changeset wj:create_attachment
create table attachment (
	attachment_id int primary key,
	part_id smallint not null references part (part_id) on delete cascade,
	name varchar(255) not null,
	mime_type varchar(255) not null default 'application/octet-stream',
	modified_at timestamp not null,
	data oid
);
--rollback drop table attachment;

--changeset wj:create_attachment_trigger
create trigger t_attachment before update or delete on attachment
	for each row execute procedure lo_manage(data);
