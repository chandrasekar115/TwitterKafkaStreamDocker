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

  private def sendToKafka(status: Status) {
    val msg = new KeyedMessage[String, String](TwitterConstants.TOPIC_NAME_PRODUCER, DataObjectFactory.getRawJSON(status))
    kafkaProducer.send(msg)
  }

  def main(args: Array[String]) {
    val twitterStream = TwitterStream.getStream
    twitterStream.addListener(new OnTweetPosted(status => sendToKafka(status)))
    twitterStream.filter(TwitterConstants.HASH_TAGS: _*)
  }

}