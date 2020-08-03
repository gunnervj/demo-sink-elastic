#!/bin/bash

sleep 150
export CLASSPATH=/var/lib/kafka/connectors/share/java/demo-sink-elastic/*
# connect-standalone /var/lib/kafka/connectors/etc/demo-sink-elastic/worker.properties /var/lib/kafka/connectors/etc/demo-sink-elastic/MySinkConnector.properties