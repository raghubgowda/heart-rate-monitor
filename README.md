# Heart Rate Data Processor

## Built With

* 	[KafkaStream](https://kafka.apache.org/documentation/streams/) - Kafka Streams is a client library for building applications and microservices, where the input and output data are stored in Kafka clusters.
* 	[Jetty](https://www.eclipse.org/jetty/) - Eclipse Jetty provides a Web server and javax.servlet container, plus support for HTTP/2, WebSocket, OSGi, JMX, JNDI, JAAS and many other integrations.
* 	[Maven](https://maven.apache.org/) - A software project management and comprehension tool.
* 	[JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java™ Platform, Standard Edition Development Kit. 


## External Tools Used

* [Postman](https://www.getpostman.com/) - API Development Environment (Testing Docmentation)

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 4](https://maven.apache.org)
- [Kafka](https://kafka.apache.org)

##Pre-requisite:

#### 1. Setup Kafka  
- Download Kafka binaries from [Kafka Org](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.5.0/kafka_2.12-2.5.0.tgz)
- Unzip the zip file to a directory {e.g: kafka}
- Open Command Prompt and Change directory (cd) to 'kafka' folder 
- Run the below scripts

##### Run Zookeeper
```shell
bin/zookeeper-server-start.sh config/zookeeper.properties
```
##### Run Kafka
```shell
bin/kafka-server-start.sh config/server.properties
```
##### Create a topic
```shell
bin/kafka-topics.sh -create --topic heart-rate --zookeeper localhost:2181 --partitions 1 --replication-factor 1
```

This will create a topic with name 'heart-rate'

## Running the application locally
- Download the zip or clone the git repository.
- Unzip the zip file

#### 1. Build and generate the jar!  
- Open Command Prompt and Change directory (cd) to 'iot-data-processor' folder containing pom.xml
```shell
mvn clean package
```
#### 2. Build and start the Producer!  
```shell
./start-producer.sh
```
#### 3. Start the Consumer!  
```shell
./start-producer.sh 7071
```

Change the port argument if needed.


###Query time!

Now you can use the HTTP endpoint exposed by the service to check the 
metrics reading from the port provided [7071]

The rest-endpoints requires authentication token and one can be obtained by calling the below endpoint.
```
curl --location --request GET 'http://localhost:7071/state/authenticate?user=admin'
```

Once the token obtained, use it to access the other end-points

####Message count by range for a device:
```
curl --location --request GET 'http://localhost:7071/state/windowed/msg-cnt-rng/Device1/1593507358000/1593507365000' \
--header 'Authorization: Bearer YWRtaW4=' \
```

####Message Count for all devices:
```
curl --location --request GET 'http://localhost:7071/state/keyvalues/msg-cnt/all' \
--header 'Authorization: Bearer YWRtaW4=' \
```
####Message count for a specific device:
```
curl --location --request GET 'http://localhost:7071/state/keyvalue/msg-cnt/Device4' \
--header 'Authorization: Bearer YWRtaW4=' \
```
####Max heart rate for all devices:
```
curl --location --request GET 'http://localhost:7071/state/keyvalues/max/all' \
--header 'Authorization: Bearer YWRtaW4=' \
```
####Max heart rate for a specific device:
```
curl --location --request GET 'http://localhost:7071/state/keyvalue/max/Device5' \
--header 'Authorization: Bearer YWRtaW4=' \
```
####Minimum heart rate for all devices:
```
curl --location --request GET 'http://localhost:7071/state/keyvalues/min/all' \
--header 'Authorization: Bearer YWRtaW4=' \
```
####Minimum heart rate for a specific device:
```
curl --location --request GET 'http://localhost:7071/state/keyvalue/min/Device3' \
--header 'Authorization: Bearer YWRtaW4=' \
```

####Get store info:
```
curl --location --request GET 'http://localhost:7071/state/instances' \
--header 'Authorization: Bearer YWRtaW4=' \
```

## Documentation

* [Postman Collection](https://www.getpostman.com/collections/e187ebe5bd07aa15c28b)
