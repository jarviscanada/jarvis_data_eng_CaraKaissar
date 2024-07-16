#!/bin/sh

# Capture CLI arguments
cmd=$1
db_username=$2
db_password=$3

# Print debug information
echo "Command: $cmd"
echo "DB Username: $db_username"
echo "DB Password: $db_password"

# Start docker
# Make sure you understand the double pipe operator
echo "Checking Docker status..."
sudo systemctl status docker || sudo systemctl start docker
echo "Docker status checked."

# Check container status (try the following cmds on terminal)
echo "Checking container status..."
docker container inspect jrvs-psql > /dev/null 2>&1
container_status=$?
echo "Container status: $container_status"

# Use switch case to handle create|stop|start options
case $cmd in
  create)
    # Check if the container is already created
    if [ $container_status -eq 0 ]; then
      echo 'Container already exists'
      exit 1
    fi

    # Check # of CLI arguments
    if [ $# -ne 3 ]; then
      echo 'Create requires username and password'
      exit 1
    fi

    # Create container
    echo "Creating Docker volume..."
    docker volume create jrvs-psql-vol
    echo "Docker volume created."

    # Start the container
    echo "Starting Docker container..."
    docker run --name jrvs-psql -e POSTGRES_USER=$db_username -e POSTGRES_PASSWORD=$db_password -d -v jrvs-psql-vol:/var/lib/postgresql/data -p 5432:5432 postgres
    echo "Docker container started."

    # Make sure you understand what's `$?`
    exit $?
    ;;

  start|stop)
    # Check instance status; exit 1 if container has not been created
    if [ $container_status -ne 0 ]; then
      echo 'Container has not been created'
      exit 1
    fi

    # Start or stop the container
    echo "${cmd^}ing Docker container..."
    docker container $cmd jrvs-psql
    echo "Docker container ${cmd}ed."
    exit $?
    ;;

  *)
    echo 'Illegal command'
    echo 'Commands: start|stop|create'
    exit 1
    ;;
esac
