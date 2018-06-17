package site.it4u.spark

import org.apache.spark.sql.SparkSession

/**
  * dataframe中的其他操作
  */
object DataFrameCase {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(this.getClass.getSimpleName)
      .master("local[2]").getOrCreate()
    // RDD => DataFrame
    val rdd = spark.sparkContext.textFile("student.data")
    // 注意：需要导入隐式转换
    import spark.implicits._
    val studentDF = rdd.map(_.split("\\|")).map(line => Student(line(0).toInt, line(1), line(2), line(3)))
      .toDF()
    // show默认只显示20条
    studentDF.show
    // 返回前30条数据，不截取
    studentDF.show(30, false)
    // 返回前10行数据
    studentDF.take(10)
    // 返回第一行数据
    studentDF.first()
    studentDF.head()
    studentDF.select("name", "email").show(30, false)
    studentDF.filter("name=''").show()
    studentDF.filter("name='' OR name='NULL'").show()
    studentDF.filter("SUBSTR(name,0,1)='m'").show()
    studentDF.sort(studentDF.col("name")).show()
    studentDF.sort(studentDF.col("name").desc).show()
    studentDF.sort(studentDF.col("name").desc, studentDF.col("id").desc).show()
    studentDF.select(studentDF("name").as("student_name")).show()
    val studentDF2 = rdd.map(_.split("\\|")).map(line => Student(line(0).toInt, line(1), line(2), line(3)))
      .toDF()
    studentDF.join(studentDF2, studentDF.col("id") === studentDF2.col("id")).show()
    spark.stop()
  }

  case class Student(id:Int, name:String, phone:String, email:String)
}

