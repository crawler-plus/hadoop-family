package site.it4u.spark

import org.apache.spark.sql.SparkSession

/**
  * parquet文件操作
  */
object ParquetApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()
    val userDF = spark.read.format("parquet").load("users.parquet")
    userDF.printSchema()
    userDF.show()
    userDF.select("name", "favorite_color").write.format("json").save("jsonout") // 保存为json文件
    spark.stop()
  }
}
