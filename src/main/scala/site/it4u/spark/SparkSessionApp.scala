package site.it4u.spark

import org.apache.spark.sql.SparkSession

/**
  * spark session使用
  */
object SparkSessionApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
        .master("local[2]").getOrCreate()
    val people = spark.read.json("people.json")
    people.show()
    spark.stop()
  }
}
