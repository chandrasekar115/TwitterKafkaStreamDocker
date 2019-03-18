## Prerequisite Softwares

- Java 1.7
- Maven 3.3.9
- Git 2.7.4
- Scala 2.10
- Kafka 0.11.1
- Spark 1.6.0

- Cloud Virtual Machine OS: Ubuntu

## Steps to setup the project

1. **Build Docker image**
	- > cd /docker_files/docker-analytics/DockerSetup/TwitterKafkaStreamDocker
	- > sudo docker build -t docker-analytics-setup .

2. **Run Docker image**
	- > sudo docker run --hostname=quickstart.cloudera --privileged=true -t -i **--docker_image_id--** /usr/bin/docker-quickstart
	
	**Example command**
	- > sudo docker run --hostname=quickstart.cloudera --privileged=true -t -i **933a400d96ac** /usr/bin/docker-quickstart
	
	- **Command to find docker_image_id :**
	- > sudo docker images

3. **Kafka Producer - Tweets**
	- > bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic twitterstreamproducer --from-beginning
	
4. **Kafka Consumer - Aggregated Tweets**
	- > bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic twitterstreamconsumer --from-beginning
	
5. **Steps to stop and remove docker images**
	- > sudo docker stop $(sudo docker ps -aq)
	- > sudo docker rm $(sudo docker ps -aq)
	- > sudo docker rmi -f $(sudo docker images -a -q)
	
