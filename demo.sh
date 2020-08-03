#!/usr/bin/env bash
echo "Initializing a clean build"
# mvn clean install
echo "Exporting Mount Paths"
export MOUNT_DIR="$(pwd)/target/demo-sink-elastic-1.0-package"
export KAFKA_DATA_DIR="~/Docker/Mounts/Kafka/Data"
export ZOOKEEPER_DATA_DIR="~/Docker/Mounts/Zookeeper/Data"
export ZOOKEEPER_LOG_DIR="~/Docker/Mounts/Zookeeper/DataLog"
export ELASTIC_SEARCH_DATA_DIR="~/Docker/Mounts/ElasticSearch/DataLog"
echo "$PWD"
echo "Copying standalone properties to mount folder"
cp connector-start.sh "${MOUNT_DIR}"
docker-compose up


