import org.apache.spark.sql.functions.{col, from_unixtime}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

object DeltaReadSilver {
  //variable d'environement hadoop
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("test")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR") //retire les logs

    spark.conf.set("spark.sql.streaming.checkpointLocation", "src/main/resources/checkpoint")
    val sbBDF = spark.read
      .format("delta").option("path", "hdfs://localhost:9000/data/subscription_sb/silver")
      .load().show()

  }
}
