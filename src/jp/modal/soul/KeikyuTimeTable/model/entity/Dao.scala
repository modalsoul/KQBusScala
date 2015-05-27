package jp.modal.soul.KeikyuTimeTable.model.entity

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import jp.modal.soul.KeikyuTimeTable.model.DBHelper
import jp.modal.soul.KeikyuTimeTable.util.LogTag

import scala.util.{Failure, Success}

/**
 * Created by imae on 2015/05/11.
 */
trait Dao extends LogTag {

  final val INSERT_FAILED = -1
  final val UPDATE_FAILED = 0

  val context:Context
  val tableName:String
  val COLUMNS:Array[String]

  val dbHelper:DBHelper = DBHelper(context)

  final val ASC = (column:String) => s"$column asc"
  final val DESC = (column:String) => s"$column desc"
  final val SELECTION = (column:String) => s"$column = ?"
  final val SELECTION_OR = (column:String) => (num:Int) => (for(i <- 0 until num) yield s"$column = ? ") .mkString("or ")
  final def SELECTION_AND(columns:Seq[String]) = columns.map(column => s"$column = ?").mkString(" and ")
  final val SELECTION_ARG = (arg:Any) => Array(arg.toString)
  final def SELECTION_ARGS(args:Seq[Any]) = args.map(_.toString).toArray

  final val COLUMN_CREATE_DATE = "create_date"
  final val COLUMN_UPDATE_DATE = "update_date"

  private[this] def database(writable:Boolean = false):SQLiteDatabase = {
    var db: SQLiteDatabase = null
    while(db == null) {
      dbHelper.openedDatabase(writable) match {
        case Success(opened) =>
          db = opened
        case Failure(e) =>
          try {
            Thread.sleep(100)
          } catch {
            case e: Exception => Log.e(TAG, s"Sleep failed. ${e.getMessage}")
          }
      }
    }
    db
  }

  def readableDatabase = database()

  def writableDatabase = database(writable = true)

}
