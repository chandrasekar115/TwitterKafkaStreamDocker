package com.analyticsproject.kakfastreaming

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext

import com.analyticsproject.commons.TwitterConstants
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import kafka.producer.KeyedMessage
import kafka.producer.Producer
import java.util.Properties
import kafka.producer.ProducerConfig
import twitter4j.JSONArray

object TwitterKafkaConsumer {

  val sparkConf = new SparkConf
  sparkConf.set("spark.sql.crossJoin.enabled", "true")
  sparkConf.setAppName("Analytics Project")
  sparkConf.setMaster("local[6]")
  val sc = new SparkContext(sparkConf)

  def main(args: Array[String]) {
    sc.setLogLevel("WARN")

    // Set the Spark StreamingContext to create a DStream for every 10 seconds
    val ssc = new StreamingContext(sc, Seconds(2))
    ssc.checkpoint("checkpoint")

    // Map each topic to a thread
    val topicMap = TwitterConstants.TOPIC_NAME_PRODUCER.split(",").map((_, TwitterConstants.NUM_THREADS.toInt)).toMap

    // Map value from the kafka message (k, v) pair
    val lines = KafkaUtils.createStream(ssc, TwitterConstants.ZK_QUORUM, TwitterConstants.GROUP, topicMap, StorageLevel.MEMORY_ONLY).map(_._2)

    // Filter hashtags
    var hashTags = lines.flatMap(_.split(" ")).filter(TwitterConstants.HASH_TAGS.contains(_)).map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(2))

    val countsSorted = hashTags.transform(_.sortBy(_._2, ascending = false))

    countsSorted.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val kafkaProperties = new Properties()
        kafkaProperties.put("metadata.broker.list", TwitterConstants.KAFKA_BROKER)
        kafkaProperties.put("serializer.class", "kafka.serializer.StringEncoder")
        val producerConfig = new ProducerConfig(kafkaProperties)
        val producer = new Producer[String, String](producerConfig)
        partition.foreach(record => {
          val data = new JSONArray
          data.put(record._1)
          data.put(record._2)
          val message = new KeyedMessage[String, String](TwitterConstants.TOPIC_NAME_CONSUMER, null, data.toString())
          producer.send(message)
        })
        producer.close()
      })
    })

    ssc.start()
    ssc.awaitTermination()

  }

}