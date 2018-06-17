package site.it4u.spark.project

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * 使用spark完成数据清洗操作
  */
object SpartStatCleanJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .config("spark.sql.parquet.compression.codec", "gzip") // 更改parquet文件默认压缩格式
      .master("local[2]").getOrCreate()
    val accessRDD = spark.sparkContext.textFile("hdfs://192.168.0.110:8020/output/part-00000")
    // RDD => DF
    val accessDF = spark.createDataFrame(accessRDD.map(x => AccessConvertUtil.parseLog(x)),
      AccessConvertUtil.struct)
    accessDF.printSchema()
    accessDF.show(false)
    // 写入到文件系统,按照日期进行分区, coalesce指定为1只有一个输出
    accessDF.coalesce(1).write.format("parquet").
      mode(SaveMode.Overwrite).partitionBy("day")
      .save("hdfs://192.168.0.110:8020/clean2/")
    spark.stop()
  }
}
