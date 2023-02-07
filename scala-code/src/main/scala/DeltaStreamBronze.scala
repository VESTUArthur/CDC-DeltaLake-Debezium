import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, current_timestamp, from_json, from_unixtime, lit, regexp_replace}

object DeltaStreamBronze {
  //variable d'environement hadoop
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("test")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR") //retire les logs


    spark.conf.set("spark.sql.streaming.checkpointLocation", "src/main/resources/checkpoint")

    import org.apache.spark.sql.types._

    //define JSON schema
    val sbValueSchema = StructType(Array(
      StructField("sb_id", IntegerType, false),
      StructField("sb_email", StringType, true),
      StructField("sb_password", StringType, true),
      StructField("sb_type", StringType, true),
      StructField("sb_price", IntegerType, true)
    ))

    val sourceSchema = StructType(Array(
      StructField("version", StringType, false),
      StructField("connector", StringType, false),
      StructField("name", StringType, false),
      StructField("ts_ms", LongType, false),
      StructField("snapshot", StringType, true),
      StructField("db", StringType, false),
      StructField("sequence", StringType, true),
      StructField("table", StringType, true),
      StructField("server_id", LongType, false),
      StructField("gtid", StringType, true),
      StructField("file", StringType, false),
      StructField("pos", LongType, false),
      StructField("row", IntegerType, false),
      StructField("thread", LongType, true),
      StructField("query", StringType, true)
    ))

    val transactionSchema = StructType(Array(
      StructField("id", StringType, false),
      StructField("total_order", LongType, false),
      StructField("data_collection_order", LongType, false)
    ))

    val payloadSchema = StructType(Array(
      StructField("before", sbValueSchema, true),
      StructField("after", sbValueSchema, true),
      StructField("source", sourceSchema, false),
      StructField("op", StringType, false),
      StructField("ts_ms", LongType, true),
      StructField("transaction", transactionSchema, true)
    ))
    val rootSchema = StructType(Array(
      StructField("schema",StringType , true),
      StructField("payload",payloadSchema , true),
    ))

    //Get data
    val inputDF = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "kafka:9092")
      .option("enable.auto.commit", false)
      .option("startingOffsets", "earliest")
      .option("subscribe", "dbserver1.ping.subscriber_sb")
      .load()

    //create DF
    val extractedDF = inputDF.selectExpr("CAST(value AS STRING)")
      .select(from_json(col("value"), rootSchema).as("df"))

    val afterDF = extractedDF.select(
      col("df.payload.source.ts_ms"),
      col("df.payload.op"),
      col("df.payload.after.sb_id"),
      col("df.payload.after.sb_email"),
      col("df.payload.after.sb_password"),
      col("df.payload.after.sb_type"),
      col("df.payload.after.sb_price")
    )
      .where("df.payload.after is not null")

    val beforeDF = extractedDF.select(
      col("df.payload.source.ts_ms"),
      col("df.payload.op"),
      col("df.payload.before.sb_id"),
      col("df.payload.before.sb_email"),
      col("df.payload.before.sb_password"),
      col("df.payload.before.sb_type"),
      col("df.payload.before.sb_price")
    )
      .where("df.payload.before is not null and df.payload.op != 'u'")

    val sbDF = afterDF.unionAll(beforeDF)
      .withColumn("write_timestamp", current_timestamp().cast("timestamp"))
      .withColumnRenamed("ts_ms", "read_timestamp")
      .withColumn("read_timestamp", from_unixtime(col("read_timestamp") / 1000).cast("timestamp"))
      .withColumn("delta_timestamp", (col("write_timestamp") - col("read_timestamp")).cast(StringType))
      .withColumn("delta_timestamp", regexp_replace(col("delta_timestamp"), "(\\d+)\\.(\\d+) seconds", "$1.$2").cast("double"))
      .withColumn("delta_ms", (col("delta_timestamp") * 1000).cast("long"))



    val sbWrite = sbDF.writeStream.format("console")
      .start()


    sbDF.writeStream
      .format("delta")
      .option("checkpointLocation", "/data/subscription_sb/bronze" + "/_checkpoints/etl-from-batch")
      .option("path", "hdfs://localhost:9000/data/subscription_sb/bronze")
      .start().awaitTermination()
    sbWrite.awaitTermination()


    //write the part to store the data using hudi
    spark.conf.set("spark.sql.streaming.checkpointLocation", "src/main/resources/checkpoint")



  }
}

