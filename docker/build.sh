#!/bin/bash
# Exit on first error, print all commands.
set -e

echo 'Build and publish cordapp'
cd .. && ./gradlew -b cordapp/build.gradle clean build publish && cd -

echo 'Build api and deploy nodes'
cd .. && ./gradlew clean build cordapp:deployNodes && cd -

echo 'Build docker images'
docker-compose -f docker-compose-build.yml build
sleep 15
echo 'Building completed!'