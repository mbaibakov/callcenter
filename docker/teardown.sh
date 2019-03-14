#!/bin/bash
#
# Exit on first error, print all commands.
set -ev

# Shut down the Docker containers for the system tests.
docker-compose -f docker-compose.yml kill && docker-compose -f docker-compose.yml down
if [ "$(docker ps -aq)" ]; then
	docker rm -f $(docker ps -aq)
fi

#remove volumes
if [ "$(docker volume ls -qf dangling=true)" ]; then
    docker volume rm $(docker volume ls -qf dangling=true)
fi
# Your system is now clean
