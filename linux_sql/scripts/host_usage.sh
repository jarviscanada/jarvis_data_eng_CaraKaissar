#!/bin/bash

# Assign CLI arguments to variables
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

# Print debug information
echo "PSQL Host: $psql_host"
echo "PSQL Port: $psql_port"
echo "Database Name: $db_name"
echo "PSQL User: $psql_user"
echo "PSQL Password: $psql_password"

# Ensure the correct number of arguments are provided
if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

# Save machine statistics in MB and current machine hostname to variables
echo "Collecting machine statistics..."
vmstat_mb=$(vmstat --unit M)
hostname=$(hostname -f)

# Print debug information
echo "VMStat Output: $vmstat_mb"
echo "Hostname: $hostname"

# Retrieve hardware specification variables
memory_free=$(echo "$vmstat_mb" | awk '{print $4}' | tail -n1 | xargs)
cpu_idle=$(vmstat | tail -1 | awk '{print $15}')
cpu_kernel=$(vmstat | tail -1 | awk '{print $14}')
disk_io=$(vmstat -d | tail -1 | awk '{print $10}')
disk_available=$(df -BM / | tail -1 | awk '{print $4}' | sed 's/M//')
timestamp=$(date -u '+%Y-%m-%d %H:%M:%S')

# Print debug information
echo "Memory Free: $memory_free"
echo "CPU Idle: $cpu_idle"
echo "CPU Kernel: $cpu_kernel"
echo "Disk IO: $disk_io"
echo "Disk Available: $disk_available"
echo "Timestamp: $timestamp"

# Subquery to find the matching id in host_info table
host_id="(SELECT id FROM host_info WHERE hostname='$hostname')"

# Print debug information
echo "Host ID Query: $host_id"

# Construct the INSERT statement from specification variables
insert_stmt="INSERT INTO host_usage (timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
             VALUES ('$timestamp', $host_id, $memory_free, $cpu_idle, $cpu_kernel, $disk_io, $disk_available);"

# Print debug information
echo "Insert Statement: $insert_stmt"

# Execute the INSERT statement through the psql CLI tool
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"

# Print the status of the last command
status=$?
echo "PSQL Command Exit Status: $status"

# Exit with the status of the last command
exit $status