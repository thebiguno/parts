--liquibase formatted sql

--changeset wj:create_account
create table account (
    account_id smallint primary key,
    name varchar(255) not null unique
    email varchar(255) not null,
    credentials varchar(255) not null,
    created_at timestamp not null,
    modified_at timestamp not null
);
--rollback drop table account;

--changeset wj:insert_admin
insert into account values (0, 'Administrator', 'admin@example.com', 'password', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--rollback delete from account where account_id = 0;

--changset wj:create_component
create table component (
	component_id smallint primary key,
	account_id integer not null references account (account_id),
	available smallint not null default 0, -- quantity on hand
	minimum smallint not null default 0, -- quantity desired
	category varchar(255) not null,
	family varchar(255) not null,
	part_no varchar(255) not null,
	description varchar(255),
	notes varchar(255)
);
--rollback drop table component;

--changeset wj:create_property
create table attribute (
	attribute_id int primary key,
	component_id smallint not null references component (component_id), 
	name varchar(255) not null, 
	val varchar(255) not null, 
	href varchar(255), 
	sort smallint default 0
);
--rollback drop table property;

--changeset wj:create_attachment
create table attachment (
	attachment_id int primary_key,
	component_id smallint not null,
	name varchar(255) not null,
	mime_type varchar(255) not null
);
--rollback drop table attachment;
 
