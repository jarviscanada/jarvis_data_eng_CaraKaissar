#!/bin/bash

# Collect hardware specifications
hostname=$(hostname -f)
lscpu_out=$(lscpu)
cpu_number=$(echo "$lscpu_out" | grep "^CPU(s):" | awk '{print $2}')
cpu_architecture=$(echo "$lscpu_out" | grep "Architecture:" | awk '{print $2}')
cpu_model=$(echo "$lscpu_out" | grep "Model name:" | awk -F ': ' '{print $2}')
cpu_mhz=$(echo "$lscpu_out" | grep "CPU MHz:" | awk '{print $3}')
l2_cache=$(echo "$lscpu_out" | grep "L2 cache:" | awk '{print $3}' | sed 's/K//')
total_mem=$(vmstat --unit M | tail -1 | awk '{print $4}')
timestamp=$(date -u '+%Y-%m-%d %H:%M:%S')

# Print hardware specifications
echo "id=1"
echo "hostname=$hostname"
echo "cpu_number=$cpu_number"
echo "cpu_architecture=$cpu_architecture"
echo "cpu_model=$cpu_model"
echo "cpu_mhz=$cpu_mhz"
echo "l2_cache=$l2_cache"
echo "total_mem=$total_mem"
echo "timestamp=$timestamp"
