#!/usr/bin/env bash

echo Waiting for Nodes...;
while ! nc -z notary 10002 && ! nc -z agent 10004;
do
  sleep 45;
done;
echo All nodes are ready! Starting Api.

java $JAVA_OPTS -jar /app/api.jar