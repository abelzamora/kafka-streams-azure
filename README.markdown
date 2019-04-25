# Kafka-Streams-Azure

A Kafka Streams based microservice application which will ingest and transform data from `TextLinesTopic` to `UppercasedTextLinesTopic`

![Azure Cloud](docs/images/azure_cloud.png)

It is a Scala project which uses [Simple Build Test](https://github.com/harrah/xsbt/wiki) (SBT) to easily compile and package the project

## Compile and Package the software

1. Launch sbt: `sbt`
2. Compile the software `>compile`
3. Assembly the software with all the dependencies `>assembly`

## Prepare the infrastructure in Azure Cloud
In this example we need a Kafka cluster and a container instance.

### Kafka Cluster
Due to economics reasons (the Virtual Machine is cheaper than any Kafka cluster) I am going to use a Virtual machine with Debian and install Kafka and Zookeeper.

The Kafka cluster can be create in two different ways:

- Using the Azure web interface:
 - Select the option from the right menu: "Create a resource"
 - Then select the virtual machine a fill all the required files

- From a template:
 - You must first should be logged in azure using the azure client: `az login`
 - Then you should create a resource: ```az group create --name kafka-streams-test --location "West Europe"```
 - Once the resource is created, using the templates, you can create the ```az group deployment create --name CreateVm-credativ.Debian-9-backports-20190429115753 --resource-group kafka-streams-test --template-file template.json --parameters @parameters.json```

#### Deploying Kafka a create the Kafka topics
Once the cluster is ready, it is necessary install Kafka and Zookeeper. In this project we are only use one machine because the goal of the project is learn how to build and deploy a container of Kafka Stream in Azure Cloud.

To install Kafka and Zookeeper I just followed this manual [Install Kafka on Ubuntu](https://linuxhint.com/install-apache-kafka-ubuntu/)

Once Kafka is ready, it is necessary to create the two Kafka topics we are going to use in this example:

```$bash
sudo kafka-topics.sh --create --bootstrap-server localhost:9092 --topic TextLinesTopic --partitions 1 --replication-factor 1
sudo kafka-topics.sh --create --bootstrap-server localhost:9092 --topic UppercasedTextLinesTopic --partitions 1 --replication-factor 1
```

Check that the two topics have been created:

```$bash
sudo /opt/Kafka/kafka_2.11-2.2.0/bin/kafka-topics.sh --zookeeper localhost:2181 --list
```

Start the producer

```$bash
sudo /opt/Kafka/kafka_2.11-2.2.0/bin/kafka-console-producer.sh --broker-list kafka-streams-test.westeurope.cloudapp.azure.com:9092 --topic TextLinesTopic
```

Start the consumer

```$bash
sudo /opt/Kafka/kafka_2.11-2.2.0/bin/kafka-console-consumer.sh --bootstrap-server kafka-streams-test.westeurope.cloudapp.azure.com:9092 --topic UppercasedTextLinesTopic --from-beginning
```
### Docker componentes
At this point the software and the infrastructure is ready, the only remaining thing is create a container with the software and deploy it :)

The components involved in this phase are the ones showed below:
![Azure Cloud](docs/images/docker_components.png)

The docker configuration is defined in `docker-compose.yml`:

```
version: "3.7"
services:
  kafka-stream-ingestor:
    build:
      context: .
      dockerfile: dockerfile
    image: abelzamora/kafka-stream-ingestor:1.0.0-SNAPSHOT
```  
The `dockerfile` referenced in the `docker-compose.yml` contains the deployment instructions:

```
FROM openjdk:8
COPY target/scala-2.12/kafka-streams-azure-assembly-1.0.0-SNAPSHOT.jar /usr/src/app/
COPY src/main/resources/init_container.sh /opt
WORKDIR /usr/src/app
RUN chmod 755 /opt/init_container.sh
CMD ["/opt/init_container.sh"]
```

The `init_container.sh` contains the run instructions:

```
java -jar kafka-streams-azure-assembly-1.0.0-SNAPSHOT.jar
```

- Build the project: `docker-compose -f docker-compose.yml build`
- Push the project: `docker-compose -f docker-compose.yml push`
- Deploy the project: 

 ```$bash
 az container create --resource-group kafka-streams-test \
--image abelzamora/kafka-stream-ingestor:1.0.0-SNAPSHOT \
--name kafka-stream-ingestor \
--vnet kafka-streams-test-vnet \
--subnet subredcontenedores
 ```
