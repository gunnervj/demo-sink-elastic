#!/usr/bin/env bash
clear
echo "Initializing a clean build"
mvn clean install
echo "Exporting Mount Paths"
export MOUNT_DIR="$(pwd)/target/demo-sink-elastic-1.0-package"
export KAFKA_DATA_DIR="~/Docker/Mounts/Kafka/Data"
export ZOOKEEPER_DATA_DIR="~/Docker/Mounts/Zookeeper/Data"
export ZOOKEEPER_LOG_DIR="~/Docker/Mounts/Zookeeper/DataLog"
export ELASTIC_SEARCH_DATA_DIR="~/Docker/Mounts/ElasticSearch/DataLog"
export DEMO_SINK_ELASTIC_OFFSET="~/Docker/Mounts/Kafka/demo-sink-connector/offsets"
echo "$PWD"
echo "Copying standalone properties to mount folder"
echo "starting docker prune - forceful"
#docker system prune --all -f
docker container prune -f
echo "Removing"
echo "-----------------------------------------"
docker images -a | grep "demo-sink"
echo "-----------------------------------------"
docker images -a | grep "demo-sink" | awk '{print $3}' | xargs docker rmi
echo "Removed elastic-sink-images"
echo "Rebuilding image for demo-sink-elastic"
docker build . -t kafka-connectors/demo-sink-elastic:latest
echo "Rebuilding comple image for demo-sink-elastic"
echo "Initializing docker compose....."
docker-compose up


