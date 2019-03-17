package com.analyticsproject.kakfastreaming

import java.util.Date
import java.util.Properties

import org.apache.hadoop.io.compress.DefaultCodec
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils

import com.analyticsproject.commons.TwitterConstants

import kafka.producer.KeyedMessage
import kafka.producer.Producer
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

    // Set the Spark StreamingContext to create a DStream for every 30 seconds
    val ssc = new StreamingContext(sc, Seconds(30))
    ssc.checkpoint("checkpoint")

    // Map each topic to a thread
    val topicMap = TwitterConstants.TOPIC_NAME_PRODUCER.split(",").map((_, TwitterConstants.NUM_THREADS.toInt)).toMap

    // Map value from the kafka message (k, v) pair
    val lines = KafkaUtils.createStream(ssc, TwitterConstants.ZK_QUORUM, TwitterConstants.GROUP, topicMap, StorageLevel.MEMORY_ONLY).map(_._2)

    // Filter hashtags
    var hashTags = lines.flatMap(_.split(" ")).filter(TwitterConstants.HASH_TAGS.contains(_)).map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(30))

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

    // Stream checkpointing Local Directory
    val cpDir = sys.env.getOrElse("CHKPOINT_DIR", "/tmp").toString

    // HDFS directory to store twitter tweets
    val opDir = sys.env.getOrElse("OP_DIR", "hdfs://quickstart.cloudera:8020/hive_warehouse/tweets/")

    // Every Seconds - Ouput Batches size
    val opBatchInterval = sys.env.get("OP_BATCH_INTERVAL").map(_.toInt).getOrElse(60)

    // no of output files for each batch interval.
    val opFiles = sys.env.get("OP_FILES").map(_.toInt).getOrElse(1)

    // print for user to know settings
    Seq(
      ("CHKPOINT_DIR" -> cpDir),
      ("OP_DIR" -> opDir),
      ("OP_FILES" -> opFiles),
      ("OP_BATCH_INTERVAL" -> opBatchInterval)).foreach {
        case (k, v) => println("%s: %s".format(k, v))
      }

    opBatchInterval match {
      case 60   =>
      case 3600 =>
      case _ => throw new Exception(
        "due to Hive partitioning restrictions batch interval output can only be 60 or 3600.")
    }

    // Enable meta-data cleaning in Spark (so this can run forever)
    System.setProperty("spark.cleaner.ttl", (opBatchInterval * 5).toString)
    System.setProperty("spark.cleaner.delay", (opBatchInterval * 5).toString)

    // Hive partitions folder date format
    val dateFormat = opBatchInterval match {
      case 60   => new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm")
      case 3600 => new java.text.SimpleDateFormat("yyyy-MM-dd-HH")
    }

    // larger batches group
    val statuses = lines.window(Seconds(opBatchInterval), Seconds(opBatchInterval))

    // Coalesce fixed number of files for each batch
    val coalesced = statuses.transform(rdd => rdd.coalesce(opFiles))

    // save as output in hdfs directory
    coalesced.foreachRDD((rdd, time) => {
      val opPartitionFolder = "tweet_created_at=" + dateFormat.format(new Date(time.milliseconds))
      print("GGRD**" + rdd.toString())
      rdd.saveAsTextFile("" + "%s/%s".format(opDir, opPartitionFolder))
    })

    ssc.start()
    ssc.awaitTermination()

  }

}
