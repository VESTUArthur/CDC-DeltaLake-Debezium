import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, from_unixtime, last}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession, functions}

object DeltaStreamSilver {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("test")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR") //retire les logs


    val sbBDF = spark.read
      .format("delta").option("path", "hdfs://localhost:9000/data/subscription_sb/bronze")
      .load()
      .distinct()

    val sbSDF = sbBDF.distinct()
      .groupBy("sb_id")
      .agg(functions.max("read_timestamp").as("read_timestamp"))
      .join(sbBDF, Seq("sb_id", "read_timestamp"))
      .filter(row => row.getAs[String]("op") != "d")
      .select("read_timestamp","sb_id", "sb_email", "sb_password", "sb_type", "sb_price")

    sbSDF.show()
    sbSDF.write
      .format("delta")
      .option("checkpointLocation", "data/subscription_sb/silver" + "/_checkpoints/etl-from-batch")
      .option("path", "hdfs://localhost:9000/data/subscription_sb/silver")
      .option("overwriteSchema", "true")
      .mode("overwrite").save()

  }
}
