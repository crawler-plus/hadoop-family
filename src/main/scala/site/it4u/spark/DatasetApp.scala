package site.it4u.spark

import org.apache.spark.sql.SparkSession

/**
  * dataset操作
  */
object DatasetApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()
      // 需要导入隐式转换
    import spark.implicits._
    // 解析csv文件
    val df = spark.read.option("header", "true").option("inferSchema", "true")
      .csv("sales.csv")
    df.show()
    val ds = df.as[Sales]
    ds.map(line => line.itemId).show()
    spark.stop()
}

  case class Sales(tId:Int,cusId:Int,itemId:Int,paid:Double)

}
