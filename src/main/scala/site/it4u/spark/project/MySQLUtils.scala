package site.it4u.spark.project

import java.sql.{Connection, DriverManager, PreparedStatement}

/**
  * mysql操作工具类
  */
object MySQLUtils {

  def getConnection(): Connection =  {
    DriverManager.getConnection("jdbc:mysql://192.168.0.110:3306/hadoop_family_project?user=root&password=root")
  }

  /**
    * 释放资源
    * @param connection
    * @param pstmt
    */
  def release(connection:Connection, pstmt:PreparedStatement): Unit = {
    try {
      if(pstmt != null) {
        pstmt.close()
      }
    }catch {
      case e: Exception => e.printStackTrace()
    }finally {
      if(connection != null) {
        connection.close()
      }
    }
  }

  def main(args: Array[String]): Unit = {
    println(getConnection())
  }
}
