## Prerequisite Softwares

- Java 1.8
- Maven 3.3.9
- Git 2.7.4
- Scala 2.11.8
- Kafka 0.11.1
- Spark 2.2.1
- npm
- Node.js
- screen

- Cloud Virtual Machine OS: Ubuntu

## Steps to setup the project

1. **Clone git project**
	- > divyateja15@analyticsvm:~$ `git clone https://github.com/divyateja15/TwitterKafkaStream.git`
	- **If already done do git pull**
	- > divyateja15@analyticsvm:~$ `git pull`

2. **Maven Install**
	- **Navigate to Project Folder**
	- > divyateja15@analyticsvm:~$ `cd TwitterKafkaStream`
	- > divyateja15@analyticsvm:~/TwitterKafkaStream$ `mvn clean install`
	
	**Hint: Screen is a terminal multiplexer, which allows a user to access multiple separate terminal sessions inside a single terminal window or remote terminal session (such as when using SSH).**
	- **Example Commands for Screen :**
	- `screen -S newscreenname`
	- `screen -r reopenexisitingscreen`
	- `screen -list (List of Screen)`
	- `ctrl+a+d` - to detach from current screen
	- `screen -d` screenname (Forceful Detach)
	
3. **Start Zookeeper**
	- **Navigate to Kafka Installed Folder**
	- > divyateja15@analyticsvm:~/TwitterKafkaStream$ `cd /usr/local/kafka`
	- **Creata a Screen for zookeeper**
	- > divyateja15@analyticsvm:/usr/local/kafka$ `screen -S zookeeper`
	- **Start Zookeeper Server**
	- > divyateja15@analyticsvm:/usr/local/kafka$ `bin/zookeeper-server-start.sh config/zookeeper.properties`
	- **Detach Screen using ctrl+a+d keys**
	
	**Hint: screen -list and check for screen is detached** 
	
3. **Start Kafka Server**
	- **Navigate to Kafka Installed Folder**
	- > divyateja15@analyticsvm:~/TwitterKafkaStream$ `cd /usr/local/kafka`
	- **Creata a Screen for KafkaServer**
	- > divyateja15@analyticsvm:/usr/local/kafka$ `screen -S kafkaserver`
	- **Start Kafka Server**
	- > divyateja15@analyticsvm:/usr/local/kafka$ `bin/kafka-server-start.sh config/server.properties`
	- **Detach Screen using ctrl+a+d keys**

4. **Create Kafka Topics**
	- **Create Kafka Producer Topic**
	- > divyateja15@analyticsvm:~$ `kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitterstreamproducer`
	- **Create Kafka Consumer Topic**
	- > divyateja15@analyticsvm:~$ `kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic twitterstreamconsumer`
	
5. **View list of Kafka Topics**
	- >divyateja15@analyticsvm:~$ `kafka-topics.sh --list --zookeeper localhost:2181`

6. **Start Kafka Producer to Genereate Tweets**
	- **Create a Screen for Kafka Producer Topic**
	- > divyateja15@analyticsvm:~$ `screen -S kafkaproducer`
	- **Navigate to Project Target Folder**
	- > divyateja15@analyticsvm:~$ `cd TwitterKafkaStream/target/`
	- **Start Kafka Producer Topic**
	- > divyateja15@analyticsvm:~/TwitterKafkaStream/target$ `scala -cp twitterkafkastream-0.0.1-SNAPSHOT.jar:lib/*:. com.analyticsproject.kakfastreaming.TwitterKafkaProducer`
	- **Detach Screen using ctrl+a+d keys**
	
7. **Start Kafka Consumer using Spark Streaming to aggregate Tweets**
	- **Create a Screen for Kafka Consumer Topic**
	- > divyateja15@analyticsvm:~$ `screen -S kafkaconsumer`
	- **Navigate to Project Target Folder**
	- > divyateja15@analyticsvm:~$ `cd TwitterKafkaStream/target/`
	- **Start Kafka Consumer Topic using Spark Submit**
	- > divyateja15@analyticsvm:~/TwitterKafkaStream/target$ `spark-submit --class com.analyticsproject.kakfastreaming.TwitterKafkaConsumer --master local[4] --jars $(echo ./lib/*.jar | tr ' ' ',') twitterkafkastream-0.0.1-SNAPSHOT.jar`
	- **Detach Screen using ctrl+a+d keys**
	
	**NOTE-- First time execution for users**
	- **To Create SSH Keys**
	- > divyateja15@analyticsvm:~$ `ssh-keygen -t rsa -P ""`
	- > divyateja15@analyticsvm:~$ `cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys`
	- > divyateja15@analyticsvm:~$ `ssh-copy-id -i /home/divya15teja/.ssh/id_rsa.pub divya15teja@localhost`
	
8. **Start Hadoop Daemons**
	- **To Start Hadoop Daemons**
	- > divyateja15@analyticsvm:~$ `start-dfs.sh`
	- **To Start Yarn Daemons**
	- > divyateja15@analyticsvm:~$ `start-yarn.sh`
	- **To Start Job History Server**
	- > divyateja15@analyticsvm:~$ `mr-jobhistory-daemon.sh start historyserver`
	- **NOTE --- Stop the daemons only it is requires**
	- **To Stop Hadoop Daemons**
	- > divyateja15@analyticsvm:~$ `stop-dfs.sh`
	- **To Stop Yarn Daemons**
	- > divyateja15@analyticsvm:~$ `stop-yarn.sh`
	- **To Stop Job History Server**
	- > divyateja15@analyticsvm:~$ `mr-jobhistory-daemon.sh stop historyserver`	
	
9. **Start Console View**
	- **Create a Screen for Console**
	- > divyateja15@analyticsvm:~$ `screen -S console`
	- **Navigate to Project Node Folder**
	- > divyateja15@analyticsvm:~/TwitterKafkaStream/node$ `node index.js`
	- **Detach Screen using ctrl+a+d keys**
	
