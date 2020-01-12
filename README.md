Simple User Directory Service
=============================

Simple user directory service written in Java.

### Setting Up ###
To run test cases, a `suds-test.properties` must be added to the root directory defining the PostgreSQL database to use like so:
```
suds.pg.host=postgres-instance-ip-address:port
suds.pg.database=database-name
suds.pg.user=postgres-user-name
suds.pg.pass=postgres-password
```
This file should not by checked in to git, and is in the `.gitignore`.

### How to Build ###
This project is built using Maven by typing the command `mvn install`.