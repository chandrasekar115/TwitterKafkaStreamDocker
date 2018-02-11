package com.analyticsproject.commons

object TwitterConstants {
  val CONSUMER_KEY = "VM2HyFbxL0JCCH1uzAiPYnVzI"
  val CONSUMER_SECRET_KEY = "HF9hkkbT3pzUgEfUflb2LoZbPqQx1TFjWOw3AY5QJC3UZYUxSm"
  val ACCESS_TOKEN_KEY = "2430786770-VkVDFx4NUgejMb7JMbRZYKvZmUhOMvFqNIRsOJL"
  val ACCESS_TOKEN_SECRET_KEY = "QGyH29ob3xU7DANkzT8qSiunDQujOWiEEmhR2Syp31Ak3"
  val TOPIC_NAME_PRODUCER = "twitterstreamproducer"
  val TOPIC_NAME_CONSUMER = "twitterstreamconsumer"
  val KAFKA_BROKER = "localhost:9092"
  val HASH_TAGS = Array("Airtel","Aircel","Telenor","Tata Docomo","Vodafone","Jio","Reliance Communications","BSNL","MTNL")
  //val HASH_TAGS = Array("Airtel")
  val ZK_QUORUM = "localhost:2181"
  val GROUP = "TwitterStreamingConsumerGroup"
  val NUM_THREADS = 4
}