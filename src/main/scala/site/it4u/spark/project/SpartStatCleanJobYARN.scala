package site.it4u.spark.project

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * 使用spark完成数据清洗操作(运行在yarn之上）
  */
object SpartStatCleanJobYARN {
  def main(args: Array[String]): Unit = {
    if(args.length != 2) {
      println("Usage: SpartStatCleanJobYARN <inputPath><outputPath>")
      System.exit(1)
    }
    val Array(inputPath, outputPath) = args
    val spark = SparkSession.builder().getOrCreate()
    val accessRDD = spark.sparkContext.textFile(inputPath)
    // RDD => DF
    val accessDF = spark.createDataFrame(accessRDD.map(x => AccessConvertUtil.parseLog(x)),
      AccessConvertUtil.struct)
    accessDF.printSchema()
    accessDF.show(false)
    // 写入到文件系统,按照日期进行分区, coalesce指定为1只有一个输出
    accessDF.coalesce(1).write.format("parquet").
      mode(SaveMode.Overwrite).partitionBy("day")
      .save(outputPath)
    spark.stop()
  }
}
