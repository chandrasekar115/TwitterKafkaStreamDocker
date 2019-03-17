FROM cloudera/quickstart
ENV MAVEN_VERSION 3.3.9
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
RUN yum -y install git || true
RUN curl -O http://downloads.lightbend.com/scala/2.11.8/scala-2.11.8.rpm && yum -y install scala-2.11.8.rpm || true
RUN curl -O https://archive.apache.org/dist/kafka/0.11.0.0/kafka_2.11-0.11.0.0.tgz
RUN curl -O https://archive.apache.org/dist/spark/spark-2.4.0/spark-2.4.0-bin-hadoop2.6.tgz
RUN tar -xvf spark-2.4.0-bin-hadoop2.6.tgz && mv spark-2.4.0-bin-hadoop2.6 spark 
RUN tar -xvf kafka_2.11-0.11.0.0.tgz && mv kafka_2.11-0.11.0.0 kafka
RUN yum update -y nss curl libcurl || true
#RUN git clone -b master https://github.com/chandrasekar115/TwitterKafkaStreamDocker.git
ADD docker-quickstart /usr/bin/
RUN chmod 777 -R /usr/bin/docker-quickstart
ADD /TwitterKafkaStreamDocker /
ADD /TwitterKafkaStreamDocker/target /
