#!/bin/bash

echo 'Cassandra!'
services cassandra start
cqlsh -f src/main/resources/config/cql/create-keyspace.cql
cqlsh -f src/main/resources/config/cql/changelog/00000000000000_create-tables.cql
cqlsh -f src/main/resources/config/cql/changelog/00000000000001_insert_default_users.cql
echo 'MongoDB!'
services mongodb start
echo 'Postgres!'
services pgservice start
psql -U postgres <<EOF
create role "jhipster-fou" login;
create database "jhipster-fou";
\q
EOF
echo 'MariaDB-SQL!'
services mysql start
mysql -u root <<EOF
SET SESSION sql_mode = 'ANSI';
create database if not exists "jhipster-fou";
\q
EOF
