## Introduction

The Linux Cluster Monitoring Agent is a tool that allows users to monitor nodes connected in a Linux cluster by tracking each node in real time. This project helps in gathering hardware and usage information of each node, storing it in a PostgreSQL database, and providing valuable insights for cluster management. The key technologies used in this project include Bash scripting, Docker, PostgreSQL, and Crontab.

## Quick Start

#### Start a PostgreSQL instance using psql_docker.sh
```bash
./linux_sql/scripts/psql_docker.sh start db_password
```
#### Initialize the database and tables
```bash
psql -h psql_host -U psql_user -w -f linux_sql/sql/ddl.sql
```
#### Insert node hardware specifications into the host_info table
```bash
./linux_sql/scripts/host_info.sh psql_host psql_port db_name psql_user psql_password
```
#### Insert a snapshot of the node's resource usage into the host_usage table
```bash
./linux_sql/scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password
```
#### Set up a crontab job to run host_usage.sh periodically
```bash
crontab -e
```
#### Add the following line to run every minute
```bash
* * * * * bash [path]/host_usage.sh psql_host psql_port db_name psql_user psql_password > /tmp/host_usage.log
```
## Implementation

## Architecture

The architecture of the Linux Cluster Administration (LCA) monitoring system consists of the following components:

- **Linux Host**: Represented as a server icon labeled `Node 1`.
- **Monitoring Agent**: A single monitoring agent icon connected to the node to indicate that the agent is installed on the server to collect hardware specification data and resource usage data.
- **PostgreSQL Database**: Represented as a database icon labeled `PostgreSQL DB`.

![Architecture Diagram](assets/architecture_diagram.png)

### Scripts

- **psql_docker.sh**: Starts and stops the PostgreSQL Docker container.

    ```bash
    # Usage
    ./scripts/psql_docker.sh start
    ./scripts/psql_docker.sh stop
    ```

- **host_info.sh**: Gathers hardware specifications and inserts them into the `host_info` table.

    ```bash
    # Usage
    ./scripts/host_info.sh psql_host psql_port db_name psql_user psql_password
    ```

- **host_usage.sh**: Collects resource usage data and inserts it into the `host_usage` table.

    ```bash
    # Usage
    ./scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password
    ```

- **crontab**: Sets up a cron job to run `host_usage.sh` periodically.

    ```bash
    # Usage
    crontab -e
    # Add the following line
    * * * * * bash [path]/scripts/host_usage.sh psql_host psql_port db_name psql_user psql_password > /tmp/host_usage.log
    ```

- **queries.sql**: Contains SQL queries to analyze and manage the cluster's data.

### Database Modeling

#### `host_info` Table

| Column           | Type      | Description                            |
|------------------|-----------|----------------------------------------|
| id               | SERIAL    | Unique ID for each node                |
| hostname         | VARCHAR   | Fully qualified hostname               |
| cpu_number       | INT2      | Number of CPUs                         |
| cpu_architecture | VARCHAR   | CPU architecture                       |
| cpu_model        | VARCHAR   | CPU model                              |
| cpu_mhz          | FLOAT8    | CPU clock speed in MHz                 |
| l2_cache         | INT4      | L2 cache size in KB                    |
| total_mem        | INT4      | Total memory in KB                     |
| timestamp        | TIMESTAMP | UTC timestamp of data collection       |

#### `host_usage` Table

| Column          | Type      | Description                            |
|-----------------|-----------|----------------------------------------|
| timestamp       | TIMESTAMP | UTC timestamp of data collection       |
| host_id         | SERIAL    | ID of the host from `host_info` table  |
| memory_free     | INT4      | Free memory in MB                      |
| cpu_idle        | INT2      | CPU idle time in percentage            |
| cpu_kernel      | INT2      | CPU kernel time in percentage          |
| disk_io         | INT4      | Number of disk I/O operations          |
| disk_available  | INT4      | Available disk space in MB             |

## Test

To test the bash scripts and DDL, run the scripts in a test environment and verify the data in the PostgreSQL tables using `SELECT` queries. The expected results are accurate hardware specifications and resource usage data inserted into the respective tables.

## Deployment

The application is deployed using GitHub, crontab, and Docker. Ensure the PostgreSQL container is running, and the bash scripts are set up correctly in the cron jobs for continuous data collection.

## Improvements

1. Handle hardware updates and changes by periodically verifying the `host_info` data and updating it if necessary.
2. Create a monitoring script that will perform all the steps in the `usage` section to make it easier to run the cluster monitoring agent.
