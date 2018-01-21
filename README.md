Real Time Streaming using Kafka and Spark Streaming with Twitter as a stream source

Navigate to Kafka folder : cd /usr/local/kafka

Start Zookeeper : bin/zookeeper-server-start.sh config/zookeeper.properties

Start Kafka Server : bin/kafka-server-start.sh config/server.properties

Create Kafka Producer Topic : kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitterstreamproducer

Create Kafka Consumer Topic : kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitterstreamconsumer

View list of Kafka Topic : kafka-topics.sh --list --zookeeper localhost:2181

Navigate to Project target folder

Start Kafka Producer : scala -cp twitterkafkastream-0.0.1-SNAPSHOT.jar:lib/*:. com.analyticsproject.kakfastreaming.TwitterKafkaProducer

Unset Hadoop Config : unset HADOOP_CONF_DIR

Start Kafka Consumer using Spark : spark-submit --class com.analyticsproject.kakfastreaming.TwitterKafkaConsumer --master local[4] --jars $(echo ./lib/*.jar | tr ' ' ',') twitterkafkastream-0.0.1-SNAPSHOT.jar
