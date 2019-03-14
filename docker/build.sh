#!/bin/bash
# Exit on first error, print all commands.
set -e

echo 'Build api'
cd .. && ./gradlew clean build && cd -

echo 'Build docker images'
docker-compose -f docker-compose-build.yml build
sleep 15
echo 'Building completed!'

