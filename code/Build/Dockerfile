FROM ubuntu 
COPY utils /home

ENV SPARK_HOME=/opt/spark
ENV PATH=$PATH:$SPARK_HOME/bin:$SPARK_HOME/sbin
ENV PYSPARK_PYTHON=/usr/bin/python3
ENV HADOOP_HOME=/opt/hadoop
ENV HADOOP_INSTALL=$HADOOP_HOME
ENV HADOOP_MAPRED_HOME=$HADOOP_HOME
ENV HADOOP_COMMON_HOME=$HADOOP_HOME
ENV HADOOP_HDFS_HOME=$HADOOP_HOME
ENV YARN_HOME=$HADOOP_HOME
ENV HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
ENV JAVA_HOME=/usr

RUN apt-get update &&\
apt-get install openjdk-8-jdk wget vim curl -y &&\
java -version; javac -version

RUN wget https://archive.apache.org/dist/spark/spark-3.1.3/spark-3.1.3-bin-hadoop2.7.tgz &&\
tar xvf spark-3.1.3-bin-hadoop2.7.tgz &&\
mv spark-3.1.3-bin-hadoop2.7 /opt/spark

RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz &&\
tar xvf hadoop-3.3.4.tar.gz &&\
mv hadoop-3.3.4 opt/hadoop 

RUN cd opt/hadoop/etc/hadoop &&\
sed -i '$d' core-site.xml &&\
echo " \n\
<property> \n\
<name>hadoop.tmp.dir</name> \n\
<value>/opt/hadoop/tmpdata</value> \n\
</property> \n\
<property> \n\
<name>fs.default.name</name> \n\
<value>hdfs://localhost:9000</value> \n\
</property> \n\
</configuration> \n\
" >> core-site.xml &&\
sed -i '$d' hdfs-site.xml &&\
echo " \n\
<property> \n\
<name>dfs.data.dir</name> \n\
<value>/opt/hadoop/dfsdata/namenode</value> \n\
</property> \n\
<property> \n\
<name>dfs.data.dir</name> \n\
<value>/opt/hadoop/dfsdata/datanode</value> \n\
</property> \n\
<property>\n\
<name>dfs.replication</name>\n\
<value>1</value>\n\
</property>\n\
</configuration>\n\
">> hdfs-site.xml &&\
sed -i '$d' mapred-site.xml &&\
echo " \n\
<property>\n\
<name>mapreduce.framework.name</name>\n\
<value>yarn</value>\n\
</property>\n\
</configuration>\n\
">> mapred-site.xml &&\
sed -i '$d' yarn-site.xml &&\
echo " \n\
<property>\n\
<name>yarn.nodemanager.aux-services</name> \n\
<value>mapreduce_shuffle</value>\n\
</property>\n\
<property>\n\
<name>yarn.nodemanager.auxservices.mapreduce.shuffle.class</name>\n\
<value>org.apache.hadoop.mapred.ShuffleHandler</value>\n\
</property>\n\
<property>\n\
<name>yarn.resourcemanager.hostname</name>\n\
<value>localhost</value>\n\
</property>\n\
<property>\n\
<name>yarn.acl.enable</name>\n\
<value>0</value>\n\
</property>\n\
<property>\n\
<name>yarn.nodemanager.env-whitelist</name>\n\
<value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PERPEND_DISTCACHE,HADOOP_YARN_HOME,HADOOP_MAPRED_HOME</value>\n\
</property>\n\
</configuration>\n\
">> yarn-site.xml &&\
cd ../../sbin &&\
hdfs namenode -format &&\
cd /



ENTRYPOINT ./opt/spark/sbin/start-master.sh &&\
hdfs --daemon start namenode &&\
hdfs --daemon start datanode &&\
yarn --daemon start resourcemanager &&\
yarn --daemon start nodemanager &&\
mapred --daemon start historyserver &&\
jps &&\
sleep 30 &&\
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" connect:8083/connectors/ --data '{"name": "ping-connector","config": {"connector.class": "io.debezium.connector.mysql.MySqlConnector","tasks.max": "1","database.hostname": "db","database.port": "3306","database.user": "root","database.password": "root","database.server.id": "1","topic.prefix": "dbserver1","database.include.list": "ping","database.allowPublicKeyRetrieval":"true","schema.history.internal.kafka.bootstrap.servers": "kafka:9092","schema.history.internal.kafka.topic": "schema-changes.ping"}}' &&\
bash home/deltaStreamBronze.sh
