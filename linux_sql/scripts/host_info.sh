#!/bin/bash

# Assign CLI arguments to variables
psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

if [ "$#" -ne 5 ]; then
    echo "Illegal number of parameters"
    exit 1
fi

# Parse host hardware specifications using bash cmds
hostname=$(hostname -f)
lscpu_out=$(lscpu)
cpu_number=$(echo "$lscpu_out" | grep "^CPU(s):" | awk '{print $2}')
cpu_architecture=$(echo "$lscpu_out" | grep "Architecture:" | awk '{print $2}')
cpu_model=$(echo "$lscpu_out" | grep "Model name:" | awk -F ': ' '{print $2}' | xargs)
cpu_mhz=$(echo "$lscpu_out" | grep "CPU MHz:" | awk -F ': ' '{print $2}' | xargs)

# Alternative method for CPU MHz if not found in lscpu output
if [ -z "$cpu_mhz" ]; then
    cpu_mhz=$(cat /proc/cpuinfo | grep "cpu MHz" | head -1 | awk '{print $4}')
fi

# Verify cpu_mhz after alternative method
if [ -z "$cpu_mhz" ]; then
    echo "cpu_mhz is still not set after using /proc/cpuinfo"
else
    echo "cpu_mhz is set to $cpu_mhz"
fi

l2_cache=$(echo "$lscpu_out" | grep "L2 cache:" | awk '{print $3}' | sed 's/K//')
total_mem=$(vmstat --unit M | tail -1 | awk '{print $4}')
timestamp=$(date -u '+%Y-%m-%d %H:%M:%S')

# Debugging: Print variable values
echo "hostname=$hostname"
echo "cpu_number=$cpu_number"
echo "cpu_architecture=$cpu_architecture"
echo "cpu_model=$cpu_model"
echo "cpu_mhz=$cpu_mhz"
echo "l2_cache=$l2_cache"
echo "total_mem=$total_mem"
echo "timestamp=$timestamp"



# Ensure all variables are properly assigned
if [[ -z "$cpu_number" ]]; then echo "cpu_number is not set"; fi
if [[ -z "$cpu_architecture" ]]; then echo "cpu_architecture is not set"; fi
if [[ -z "$cpu_model" ]]; then echo "cpu_model is not set"; fi
if [[ -z "$cpu_mhz" ]]; then echo "cpu_mhz is not set"; fi
if [[ -z "$l2_cache" ]]; then echo "l2_cache is not set"; fi
if [[ -z "$total_mem" ]]; then echo "total_mem is not set"; fi

# Ensure all variables are properly assigned
if [[ -z "$cpu_number" || -z "$cpu_architecture" || -z "$cpu_model" || -z "$cpu_mhz" || -z "$l2_cache" || -z "$total_mem" ]]; then
    echo "Error: One or more variables are not set."
    exit 1
fi

# Construct the INSERT statement with conflict handling
insert_stmt="INSERT INTO host_info (hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, timestamp)
             VALUES ('$hostname', $cpu_number, '$cpu_architecture', '$cpu_model', $cpu_mhz, $l2_cache, $total_mem, '$timestamp')
             ON CONFLICT (hostname) DO UPDATE SET
             cpu_number = EXCLUDED.cpu_number,
             cpu_architecture = EXCLUDED.cpu_architecture,
             cpu_model = EXCLUDED.cpu_model,
             cpu_mhz = EXCLUDED.cpu_mhz,
             l2_cache = EXCLUDED.l2_cache,
             total_mem = EXCLUDED.total_mem,
             timestamp = EXCLUDED.timestamp;"

# Execute the INSERT statement through the psql CLI tool
export PGPASSWORD=$psql_password
psql -h $psql_host -p $psql_port -d $db_name -U $psql_user -c "$insert_stmt"

# Exit with the status of the last command
exit $?