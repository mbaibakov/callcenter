#!/bin/sh

rm -v -f "$CORDA_HOME/network-parameters"
java $JAVA_OPTS -jar "$CORDA_HOME/corda.jar"