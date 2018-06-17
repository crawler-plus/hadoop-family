package site.it4u.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext

object SQLContextApp {

  def main(args: Array[String]): Unit = {
    var path = args(0)
    // 创建相应的Context
    val sparkConf = new SparkConf()
    // 在测试或生产中，AppName和master是通过脚本指定
//    sparkConf.setAppName("SQLContextApp").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    // 相关的处理
    val people = sqlContext.read.format("json").load(path)
    people.printSchema()
    people.show()
    // 关闭资源
    sc.stop()
  }
}
