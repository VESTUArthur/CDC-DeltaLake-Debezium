# PING67
 
 Cloud agnostic implementation of Change Data Capture (CDC)
 
 
 
![Schema_PING67 (4)](https://user-images.githubusercontent.com/50460721/217348892-67333e50-63a7-4488-bad0-cb9b3096913d.jpg)

## About:

This repository is an exemple of implementation of a cloud agnostic Change Data Capture (CDC) data pipeline, a scalable and reliable solution for real-time data replication. The pipeline is designed to be infrastructure agnostic and operates seamlessly across different cloud platforms.

The pipeline use Apache Kafka and its CDC connector Debezium to capture and transport data changes in real-time. It offers a flexible and scalable solution for capturing data changes and allows for easy integration with other data processing systems such as spark that we use in this implementation.

The CDC data pipeline consists of several components, each with a specific role in the data replication process. The components include:

- Apache Kafka: A distributed streaming platform used for data replication.
- Debezium: A CDC connector for Apache Kafka that captures and transports data changes.
- Database, here Mysql: A source database containing the data to be replicated.
- Spark: A data processing system used for transforming and analyzing the data.

Docker Compose is used to run each component as a separate container and manage their interactions with each other. The pipeline is designed to be infrastructure agnostic, allowing it to operate seamlessly across different cloud platforms.

## Prequisites

- Make sure to have Docker installed on your instance/PC.
- Git clone this repo, or download the ZIP and unzip it in your target repository, where you will launch the application.

## Installation and Configuration
### 1. Place yourself in the good repository

<details>
  <summary>Go to this repository :</summary>
  
![image_test](https://github.com/VESTUArthur/PING67/blob/main/images/capture-1.png)

</details>


### 2. Setup docker compose 

```
docker compose up
```
**If you launch it for the first time, it will take some time.**

<details>
  <summary>After few minutes, and if the download is ok, you will see : </summary>
  
![image_lancementComposUp](https://github.com/VESTUArthur/PING67/blob/main/images/capture-composeUp.png)

</details>


<details>
  <summary>And when the command line displays this :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-composeUp2.png)

</details>

You must wait few seconds and head toward the next step.

### 3. Open a new terminal and start Spark Thrift Server 

```
docker exec code-spark-1 bash home/start-server.sh
```

<details>
  <summary>Results :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-startServerThrift.png)

</details>




### 4. Setup superset 

```
docker exec code-superset-1 bash /setup-superset.sh
```


<details>
  <summary>It will take some time and will result by :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-setupSuperset.png)

</details>

**TIPS** : If you relaunch the application, you don't need to re-enter this command.




### 5. Connect to database on superset webpage

```
http://localhost:8085/superset/welcome/
```

**TIPS**: If you test our application on cloud, you just have to replace localhost by the VM's IP (Be careful with the firewall)

### 6. Add our Dataset to the dataviz application, Superset 

<details>
  <summary>First :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-chooseDataset.png)

</details>

<br>

<details>
  <summary>Then choose "Apache Spark SQL" :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-chooseApacheSql.png)

</details>


Finally enter the below URI :
```
hive://hive@spark:10000/
```
<details>
  <summary>Show the image</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-enterHiveURI.png)

</details>


**Superset will maybe display a fatal error, you can ignore it**


### 7. Go to SQL Lab

<details>
  <summary>Show the image</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-OpenSqlLab.png)

</details>


### 8. Query the dataset 

In SQL Lab you can query the delta table using:
```
SELECT * FROM delta.`hdfs://localhost:9000/data/subscription_sb/bronze`;
```
<details>
  <summary>Show the image</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-QueryDataSet.png)

</details>

### 9. Get actual Mysql table state 

```
SELECT sbBDF.sb_id, sbBDF.sb_email, sbBDF.sb_password, sbBDF.sb_type, sbBDF.sb_price
FROM (
  SELECT sb_id, MAX(read_timestamp) as read_timestamp
  FROM (
    SELECT DISTINCT sb_id, read_timestamp
    FROM delta.`hdfs://localhost:9000/data/subscription_sb/bronze`
  )
  GROUP BY sb_id
) sbSDF
JOIN delta.`hdfs://localhost:9000/data/subscription_sb/bronze` sbBDF
ON sbSDF.sb_id = sbBDF.sb_id AND sbSDF.read_timestamp = sbBDF.read_timestamp
WHERE sbBDF.op != 'd'
ORDER BY sbBDF.sb_id
```

## Utils

### 1. Enter Kafka container terminal to verify kafka topic:

```
docker exec code-kafka-1 kafka-console-consumer.sh --topic dbserver1.ping.subscriber_sb --from-beginning --bootstrap-server kafka:9092
```

### 2. Verify schema registry

```
curl -X GET http://localhost:8081/schemas
```

### 3. Create a chart 

<details>
  <summary>First save the result (save dataset) :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-createChart1.png)

</details>

<details>
  <summary>Then create the chart wanted :</summary>
  
![image](https://github.com/VESTUArthur/PING67/blob/main/images/capture-utilsCreateChart2.png)

</details>
