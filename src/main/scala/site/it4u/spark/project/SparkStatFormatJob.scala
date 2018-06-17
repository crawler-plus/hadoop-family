package site.it4u.spark.project

import org.apache.spark.sql.SparkSession

/**
  * 第一步清洗：抽取出需要的指定列的数据
  */
object SparkStatFormatJob {

  def main(args: Array[String]): Unit = {
    var spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()
    val access = spark.sparkContext.textFile("access_10000.log")
    access.map(line => {
      val splits = line.split(" ")
      // 访问ip
      val ip = splits(0)
      // 访问时间
      val time = splits(3) + " " + splits(4)
      // 访问url
      val url = splits(11).replaceAll("\"", "")
      // 流量
      val traffic = splits(9)
      DateUtils.parse(time) + "\t" + url + "\t" + traffic + "\t" + ip
    }).filter(_.matches(".*http://www.imooc.com/(code|video){1,1}.*")).saveAsTextFile("hdfs://192.168.0.110:8020/output/") // 写入hdfs中
    spark.stop()
  }
}
