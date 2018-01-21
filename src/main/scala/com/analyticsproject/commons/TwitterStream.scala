package com.analyticsproject.commons

import twitter4j.{ StallWarning, Status, StatusDeletionNotice, StatusListener, TwitterStreamFactory }
import twitter4j.conf.{ Configuration, ConfigurationBuilder }
import org.apache.kafka.clients.producer.ProducerRecord
import twitter4j.TwitterObjectFactory

object TwitterStream {

  private val getTwitterConf: Configuration = {
    val twitterConf = new ConfigurationBuilder()
      .setJSONStoreEnabled(true)
      .setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY)
      .setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET_KEY)
      .setOAuthAccessToken(TwitterConstants.ACCESS_TOKEN_KEY)
      .setOAuthAccessTokenSecret(TwitterConstants.ACCESS_TOKEN_SECRET_KEY)
      .setIncludeEntitiesEnabled(true)
      .build()
    twitterConf
  }

  def getStream = new TwitterStreamFactory(getTwitterConf).getInstance()

  class OnTweetPosted(cb: Status => Unit) extends StatusListener {
    override def onStatus(status: Status): Unit = cb(status)
    override def onException(ex: Exception): Unit = throw ex
    // no-op for the following events
    override def onStallWarning(warning: StallWarning): Unit = {}
    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}
    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}
    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}
  }

  def toProducerRecord(topic: String, tweet: Status) = {
    new ProducerRecord[String, String](
      topic,
      tweet.getUser.getScreenName,
      TwitterObjectFactory.getRawJSON(tweet))
  }
}