package site.it4u.spark

import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

object HiveContextApp {

  def main(args: Array[String]): Unit = {
    // 创建相应的Context
    val sparkConf = new SparkConf()
    // 在测试或生产中，AppName和master是通过脚本指定
    //    sparkConf.setAppName("HiveContextApp").setMaster("local[2]")
    val sc = new SparkContext(sparkConf)
    val hiveContext = new HiveContext(sc)
    // 相关的处理
    hiveContext.table("emp").show()
    // 关闭资源
    sc.stop()
  }
}
