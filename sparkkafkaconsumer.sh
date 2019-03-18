spark-submit --class com.analyticsproject.kakfastreaming.TwitterKafkaConsumer --master local[4] --jars $(echo /target/lib/*.jar | tr ' ' ',') /target/twitterkafkastreamdocker-0.0.1-SNAPSHOT.jar
