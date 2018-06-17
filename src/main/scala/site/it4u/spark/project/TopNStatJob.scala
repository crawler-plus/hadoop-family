package site.it4u.spark.project

import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * TopN统计Spark作业
  */
object TopNStatJob {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
        .config("spark.sql.sources.partitionColumnTypeInference.enabled", "false")
      .master("local[2]").getOrCreate()
    val accessDF = spark.read.format("parquet").load("hdfs://192.168.0.110/clean/day=20161110")
    // dataframe做一个缓存
    accessDF.cache()
    val day = "20161110"
    StatDAO.deleteData(day)
    videoAccessTopNStatSQL(spark, accessDF)
    cityAccessTopNStat(spark, accessDF)
    videoTrafficsTopNStat(spark, accessDF)
    // dataframe从缓存中去除
    accessDF.unpersist(true)
    spark.stop()
  }

  // 最受欢迎的topN课程(使用dataFrame方式)
  def videoAccessTopNStat(spark: SparkSession, accessDF: DataFrame): Unit ={
    val videoAccessTopNDF = accessDF.filter("cmsType = 'video'").groupBy("cmsId").agg(count("cmsId").as("times"))
      .orderBy(desc("times"))
    videoAccessTopNDF.show(false)
  }

  // 最受欢迎的topN课程(使用sql方式)
  def videoAccessTopNStatSQL(spark: SparkSession, accessDF: DataFrame): Unit ={
    accessDF.createOrReplaceTempView("access_logs")
    val videoAccessTopNDF = spark.sql("select cmsId, count(1) as times from access_logs where cmsType = 'video' group by cmsId" +
      " order by times desc")
    videoAccessTopNDF.show(false)
    try {
      // 将统计结果写入到MYSQL中
      videoAccessTopNDF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayVideoAccessStat]
        partitionOfRecords.foreach(info => {
          val day = "20161110"
          val cmsId = info.getAs[Long]("cmsId")
          val times = info.getAs[Long]("times")
          list.append(DayVideoAccessStat(day, cmsId, times))
        })
        StatDAO.insertDayVideoAccessTopN(list)
      })
    }catch {
      case e: Exception => e.printStackTrace()
    }
  }

  //
  def cityAccessTopNStat(spark: SparkSession, accessDF: DataFrame): Unit ={
    val cityAccessTopNDF = accessDF.filter("cmsType = 'video'")
      .groupBy("city", "cmsId").agg(count("cmsId").as("times"))
    // window函数
    val top3DF = cityAccessTopNDF.select(cityAccessTopNDF("city"),
      cityAccessTopNDF("cmsId"),
      cityAccessTopNDF("times"),
      row_number().over(Window.partitionBy(cityAccessTopNDF("city")).orderBy(cityAccessTopNDF("times").desc)).as("times_rank"))
      .filter("times_rank <= 3") // 统计每一个地市最受欢迎的top3
    try {
      // 将统计结果写入到MYSQL中
      top3DF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayCityVideoAccessStat]
        partitionOfRecords.foreach(info => {
          val day = "20161110"
          val cmsId = info.getAs[Long]("cmsId")
          val city = info.getAs[String]("city")
          val times = info.getAs[Long]("times")
          val timesRank = info.getAs[Int]("times_rank")
          list.append(DayCityVideoAccessStat(day, cmsId, city, times, timesRank))
        })
        StatDAO.insertDayCityVideoAccessTopN(list)
      })
    }catch {
      case e: Exception => e.printStackTrace()
    }
  }

  def videoTrafficsTopNStat(spark: SparkSession, accessDF: DataFrame): Unit ={
    val trafficAccessTopNDF = accessDF.filter("cmsType = 'video'")
      .groupBy("cmsId").agg(sum("traffic").as("traffics"))
      .orderBy(desc("traffics"))
    trafficAccessTopNDF.show(false)
    try {
      // 将统计结果写入到MYSQL中
      trafficAccessTopNDF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayVideoTrafficsStat]
        partitionOfRecords.foreach(info => {
          val day = "20161110"
          val cmsId = info.getAs[Long]("cmsId")
          val traffics = info.getAs[Long]("traffics")
          list.append(DayVideoTrafficsStat(day, cmsId, traffics))
        })
        StatDAO.insertDayVideoTrafficsTopN(list)
      })
    }catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
