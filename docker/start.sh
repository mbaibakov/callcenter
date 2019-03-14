#!/bin/bash
#
# Exit on first error, print all commands.
set -e

echo -e "\nStopping the previous network (if any)"
docker-compose -f docker-compose.yml down

# Create and Start the Docker containers for the network
docker-compose -f docker-compose.yml up -d
sleep 15
echo -e "\nNetwork started!!\n"