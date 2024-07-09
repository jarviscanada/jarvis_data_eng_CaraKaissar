-- Switch to the host_agent database
\c host_agent;

-- Create host_info table if it doesn't exist
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

-- Create host_usage table if it doesn't exist
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

-- Create cd.members table
CREATE TABLE IF NOT EXISTS cd.members (
                                          memid SERIAL PRIMARY KEY,
                                          surname VARCHAR(200) NOT NULL,
    firstname VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    zipcode INTEGER NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    recommendedby INTEGER,
    joindate TIMESTAMP NOT NULL,
    FOREIGN KEY (recommendedby) REFERENCES cd.members(memid)
    );

-- Create cd.bookings table
CREATE TABLE IF NOT EXISTS cd.bookings (
                                           bookid SERIAL PRIMARY KEY,
                                           facid INTEGER NOT NULL,
                                           memid INTEGER NOT NULL,
                                           starttime TIMESTAMP NOT NULL,
                                           slots INTEGER NOT NULL,
                                           FOREIGN KEY (facid) REFERENCES cd.facilities(facid),
    FOREIGN KEY (memid) REFERENCES cd.members(memid)
    );

-- Create cd.facilities table
CREATE TABLE IF NOT EXISTS cd.facilities (
                                             facid SERIAL PRIMARY KEY,
                                             name VARCHAR(100) NOT NULL,
    membercost NUMERIC NOT NULL,
    guestcost NUMERIC NOT NULL,
    initialoutlay NUMERIC NOT NULL,
    monthlymaintenance NUMERIC NOT NULL
    );