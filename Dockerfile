FROM confluentinc/cp-kafka-connect-base:5.5.1

WORKDIR /demo-sink-elastic

ADD ./target/demo-sink-elastic-1.0-package/etc/demo-sink-elastic/ etc
ADD ./target/demo-sink-elastic-1.0-package/share/java/demo-sink-elastic/ share

ENV CLASSPATH /demo-sink-elastic/share/*

VOLUME /demo-sink-elastic/offsets

ENTRYPOINT ["connect-standalone", "./etc/worker.properties", "./etc/MySinkConnector.properties"]