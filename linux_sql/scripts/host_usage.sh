#!/bin/bash

# Collect resource usage data
memory_free=$(vmstat --unit M | tail -1 | awk '{print $4}')
cpu_idle=$(vmstat | tail -1 | awk '{print $15}')
cpu_kernel=$(vmstat | tail -1 | awk '{print $14}')
disk_io=$(vmstat -d | tail -1 | awk '{print $10}')
disk_available=$(df -BM / | tail -1 | awk '{print $4}' | sed 's/M//')
timestamp=$(date -u '+%Y-%m-%d %H:%M:%S')

# Print resource usage data
echo "timestamp=$timestamp"
echo "host_id=1"
echo "memory_free=$memory_free"
echo "cpu_idle=$cpu_idle"
echo "cpu_kernel=$cpu_kernel"
echo "disk_io=$disk_io"
echo "disk_available=$disk_available"
