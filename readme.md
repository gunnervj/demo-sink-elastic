# A Demo Project for Kafka Connect Sink Connector

## Introduction

As wisely said, why re-invent wheels? Kafka connect helps us with something like that.

When using Apache the source of the data could be a kafka-producer who pushes data into the kafka. The data could be from a database, application, cache etc. 

Let's take an example of loading data to kafka topic from a database. This could be a broiler plate code. Many of us might have already wrote something like this. Why not re-use?

Kafka Connect help us with this scenario. They are ready to use components which. can help us import or export data between kafka and external systems.

- A source connector help us with loading data into kafka from various sources
- A Sink connector help us to ship data out of kafka, say into elastic search or any other data sinks.


We already have many connectors which fit most use cases. Some are opensource which some are supported by Confluent or its partners. 

Check out : https://docs.confluent.io/current/connect/index.html

For available connectors: https://www.confluent.io/hub/?_ga=2.61129070.312483398.1596463018-44691259.1595303352

But, when the avialable plugins does not fit our reqirement, we might sometime need to write our own connector. This project is to demonstrate such a requirement. We transform source data from 
one format into another, perform some validations and then load into elastic search.

The project has room for improvement and is for a standalone, demo purpose.

Set Up

- Checkout the project.
- For linux/mac users, run the demo.sh

**Pre-requistes**

- Have docker intalled in the running machine. As we are going to use containers.

