import org.apache.spark.sql.functions.{col, from_unixtime}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.apache.spark.sql.functions.{col, regexp_replace}
import org.apache.spark.sql.types.StringType
object DeltaReadBronze {
  //variable d'environement hadoop
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("test")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR") //retire les logs

    spark.conf.set("spark.sql.streaming.checkpointLocation", "src/main/resources/checkpoint")
    val sbBDF = spark.read
      .format("delta").option("path", "hdfs://localhost:9000/data/subscription_sb/bronze")
      .load()

    sbBDF
      .show(20)
    sbBDF
      .describe().show()
  }
}


