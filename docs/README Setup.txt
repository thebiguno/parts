To install Postgres for Parts on a Debian system (as root):

	(General Postgres setup; do this once for all DBs you run)
	aptitude install postgresql postgresql-contrib			#the contrib package is needed for lo extension
	su postgres
	psql
	ALTER ROLE postgres WITH ENCRYPTED PASSWORD 'password';
	Ctrl-D
	exit (you should be root again now)
	
	(Parts-specific setup)
	psql -h localhost -U postgres
	CREATE USER parts WITH PASSWORD 'password';
	CREATE DATABASE parts ENCODING 'UTF8';
	GRANT ALL PRIVILEGES ON DATABASE parts to parts;
	\c parts
	CREATE EXTENSION LO;
	Ctrl-D
