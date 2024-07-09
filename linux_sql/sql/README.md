# Introduction

# SQL Quries

###### Table Setup (DDL)
This section of the documentation provides the SQL Data Definition Language (DDL) statements used to set up the necessary tables for the club database. These tables are crucial for managing members, bookings, and facilities efficiently.

### Preliminary Setup

Before creating the tables, ensure you are connected to the appropriate database:
`\c host_agent;`

##### Creation of Tables

The following subsections describe the creation of each table, including their fields and the relationships between them.

##### 1\. Host Info Table

Stores essential information about each host in the system.

```sql
CREATE TABLE IF NOT EXISTS PUBLIC.host_info (
id SERIAL NOT NULL,
hostname VARCHAR NOT NULL,
cpu_number INT2 NOT NULL,
cpu_architecture VARCHAR NOT NULL,
cpu_model VARCHAR NOT NULL,
cpu_mhz FLOAT8 NOT NULL,
l2_cache INT4 NOT NULL,
"timestamp" TIMESTAMP NULL,
total_mem INT4 NULL,
CONSTRAINT host_info_pk PRIMARY KEY (id),
CONSTRAINT host_info_un UNIQUE (hostname)
);
```

##### 2\. Host Usage Table

Tracks resource usage metrics for each host periodically.

```sql
CREATE TABLE IF NOT EXISTS PUBLIC.host_usage (
"timestamp" TIMESTAMP NOT NULL,
host_id INT NOT NULL,
memory_free INT4 NOT NULL,
cpu_idle INT2 NOT NULL,
cpu_kernel INT2 NOT NULL,
disk_io INT4 NOT NULL,
disk_available INT4 NOT NULL,
CONSTRAINT host_usage_host_info_fk FOREIGN KEY (host_id) REFERENCES host_info(id)
);
```

##### 3\. Members Table

Contains detailed records about each member of the club.

```sql
CREATE TABLE IF NOT EXISTS cd.members (
memid SERIAL PRIMARY KEY,
surname VARCHAR(200) NOT NULL,
firstname VARCHAR(200) NOT NULL,
address VARCHAR(300) NOT NULL,
zipcode INTEGER NOT NULL,
telephone VARCHAR(20) NOT NULL,
recommendedby INTEGER REFERENCES cd.members(memid),
joindate TIMESTAMP NOT NULL
);
```

##### 4\. Bookings Table

Records all bookings made for the club's facilities.

```sql
CREATE TABLE IF NOT EXISTS cd.bookings (
bookid SERIAL PRIMARY KEY,
facid INTEGER NOT NULL REFERENCES cd.facilities(facid),
memid INTEGER NOT NULL REFERENCES cd.members(memid),
starttime TIMESTAMP NOT NULL,
slots INTEGER NOT NULL
);
```

##### 5\. Facilities Table

Details each facility available at the club, including costs and maintenance information.

```sql
CREATE TABLE IF NOT EXISTS cd.facilities (
facid SERIAL PRIMARY KEY,
name VARCHAR(100) NOT NULL,
membercost NUMERIC NOT NULL,
guestcost NUMERIC NOT NULL,
initialoutlay NUMERIC NOT NULL,
monthlymaintenance NUMERIC NOT NULL
);
```

###### Question 1: Show all members

```sql
SELECT *
FROM cd.members
```

###### Questions 2: Lorem ipsum...

```sql
SELECT blah blah 
```