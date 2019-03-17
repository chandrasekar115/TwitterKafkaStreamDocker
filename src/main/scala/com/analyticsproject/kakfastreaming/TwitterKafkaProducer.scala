package com.analyticsproject.kakfastreaming

import java.util.Properties

import com.analyticsproject.commons.TwitterStream._

import kafka.producer.KeyedMessage
import kafka.producer.ProducerConfig
import twitter4j.Status
import twitter4j.json.DataObjectFactory
import com.analyticsproject.commons.TwitterConstants
import com.analyticsproject.commons.TwitterStream
import kafka.producer.Producer

object TwitterKafkaProducer {

  val kafkaProducer = {
    val kafkaProperties = new Properties()
    kafkaProperties.put("metadata.broker.list", TwitterConstants.KAFKA_BROKER)
    kafkaProperties.put("serializer.class", "kafka.serializer.StringEncoder")
    val producerConfig = new ProducerConfig(kafkaProperties)
    new Producer[String, String](producerConfig)
  }

  // Remove special characters inside of statuses that screw up Hive's scanner.
  def formatStatus(s: Status): String = {
    def safeValue(a: Any) = Option(a)
      .map(_.toString)
      .map(_.replace("\t", ""))
      .map(_.replace("\"", ""))
      .map(_.replace("\'", ""))
      .map(_.replace("\n", ""))
      .map(_.replaceAll("[\\p{C}]", "")) // Control characters
      .getOrElse("")
    hivefields.map { case (f, name, hiveType) => f(s) }
      .map(f => safeValue(f))
      .mkString("'", "'\t'" , "'")
  }

  val dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0")
  val year = new java.text.SimpleDateFormat("yyyy")
  val month = new java.text.SimpleDateFormat("MM")
  val day = new java.text.SimpleDateFormat("dd")
  val hour = new java.text.SimpleDateFormat("HH")
  val minute = new java.text.SimpleDateFormat("mm")
  val second = new java.text.SimpleDateFormat("ss")

  val hivefields: Seq[(Status => Any, String, String)] = Seq(
    (s => s.getId, "id", "BIGINT"),
    (s => s.getUser.getId, "user_id", "INT"),
    (s => s.getUser.getName, "user_name", "STRING"),
    (s => s.getUser.getScreenName, "user_screen_name", "STRING"),
    (s => s.getUser.getLang, "user_language", "STRING"),
    (s => s.getUser.getLocation, "user_location", "STRING"),
    (s => s.getUser.getTimeZone, "user_timezone", "STRING"),
    (s => s.getUser.getFollowersCount, "user_followers", "BIGINT"),
    (s => s.getUser.getFavouritesCount, "user_favorites", "BIGINT"),
    (s => s.getRetweetCount, "retweet_count", "INT"),
    (s => s.getInReplyToStatusId, "reply_status_id", "BIGINT"),
    (s => s.getInReplyToUserId, "reply_user_id", "BIGINT"),
    (s => s.getSource, "source", "STRING"),
    (s => s.getText, "text", "STRING"),
    (s => Option(s.getGeoLocation).map(_.getLatitude()).getOrElse(""), "latitude", "FLOAT"),
    (s => Option(s.getGeoLocation).map(_.getLongitude()).getOrElse(""), "longitude", "FLOAT"),
    (s => dateFormat.format(s.getUser.getCreatedAt), "user_created_at", "TIMESTAMP"),
    (s => dateFormat.format(s.getCreatedAt), "created_at", "TIMESTAMP"),
    (s => year.format(s.getCreatedAt), "created_at_year", "INT"),
    (s => month.format(s.getCreatedAt), "created_at_month", "INT"),
    (s => day.format(s.getCreatedAt), "created_at_day", "INT"),
    (s => hour.format(s.getCreatedAt), "created_at_hour", "INT"),
    (s => minute.format(s.getCreatedAt), "created_at_minute", "INT"),
    (s => second.format(s.getCreatedAt), "created_at_second", "INT"))

  // Hive Schema
  val hiveSchema = hivefields.map { case (f, name, hiveType) => "%s %s".format(name, hiveType) }.mkString("(", ", ", ")")
  println("Hive Table schema is: %s".format(hiveSchema))

  private def sendToKafka(status: Status) {
    val msg = new KeyedMessage[String, String](TwitterConstants.TOPIC_NAME_PRODUCER, formatStatus(status).toString())
    kafkaProducer.send(msg)
  }

  def main(args: Array[String]) {
    val twitterStream = TwitterStream.getStream
    twitterStream.addListener(new OnTweetPosted(status => sendToKafka(status)))
    twitterStream.filter(TwitterConstants.HASH_TAGS: _*)
  }

}