package site.it4u.spark

import org.apache.spark.sql.SparkSession

object DataFrameApp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()
    //  将json文件加载成一个dataframe
    val peopleDF = spark.read.format("json").load("people.json")
    // 输出dataframe对应的schema信息
    peopleDF.printSchema
    // 输出数据集前20条记录
    peopleDF.show
    // 查询某一列所有数据
    peopleDF.select("name").show
    // 查询某几列所有的数据并对列进行计算
    peopleDF.select(peopleDF.col("name"), (peopleDF.col("age") + 10).as("age2")).show
    // 根据某一列的值进行过滤
    peopleDF.filter(peopleDF.col("age") >10).show
    // 根据某一列进行分组，然后进行聚合操作
    peopleDF.groupBy("age").count().show
    spark.stop
  }
}
