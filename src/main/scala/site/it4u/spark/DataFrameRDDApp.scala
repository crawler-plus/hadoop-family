package site.it4u.spark

import org.apache.spark.sql.SparkSession

/**
  * dataframe和RDD的互操作
  * */
object DataFrameRDDApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
        .master("local[2]").getOrCreate()
    // RDD => DataFrame
    val rdd = spark.sparkContext.textFile("infos.txt")
    // 注意：需要导入隐式转换
    import spark.implicits._
    val infoDF = rdd.map(_.split(",")).map(line => Info(line(0).toInt, line(1), line(2).toInt))
      .toDF()
    infoDF.show()
    infoDF.filter(infoDF.col("age") > 30).show()
    // 注册成一个临时表
    infoDF.createOrReplaceTempView("infos")
    spark.sql("select * from infos where age > 30").show()
    spark.stop()
  }

  case class Info(id:Int, name:String, age:Int)
}

