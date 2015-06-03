package jp.modal.soul.KeikyuTimeTable.model


import java.io.{FileOutputStream, IOException}

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import android.util.Log
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.util.{Loan, LogTag}

import scala.util.{Failure, Try}

/**
 * Created by imae on 2015/05/11.
 */
object DBHelper {
  final val DB_VERSION = 4
}

case class DBHelper(context:Context) extends SQLiteOpenHelper(context, context.getResources.getString(R.string.db_name), null, DBHelper.DB_VERSION) with LogTag {
  private[this] final val DB_FULL_PATH = s"${context.getResources.getString(R.string.db_path)}${context.getResources.getString(R.string.db_name)}"
  private[this] final val DB_ASSET = context.getResources.getString(R.string.db_name_asset)
  private[this] var database:SQLiteDatabase = null

  override def onCreate(s:SQLiteDatabase) = {}

  override def close = synchronized {
    if(database != null) database.close()
    super.close()
  }

  def needMigration(current:Int) = current == DBHelper.DB_VERSION

  /**
   * DBファイルの存在チェック
   * @return
   */
  def existDatabase = {
    var exist = false
    for(db <- openedDatabase())(exist = true)
    exist
  }

  def createEmptyDatabase:Unit = {
    if(!existDatabase) {
      Log.e(TAG, "EMPTY!!")
      for(db <- Loan(getReadableDatabase)) {
        Try(copyDatabaseFromAssets).recover{
          case e:IOException => Log.e(TAG, s"Error copying atabase. ${e.getMessage}")
        }
      }
    }
  }

  def migration:Option[Int] = {
    (for{
      db <- openedDatabase(writable = true)
      version <- Try{copyDatabaseFromAssets;DBHelper.DB_VERSION}.recoverWith{
        case e:IOException => Log.e(TAG, s"Error copying atabase. ${e.getMessage}"); Failure(e)
      }
    } yield ( version )).toOption
  }

  @throws(classOf[IOException])
  private[this] def copyDatabaseFromAssets: Unit = {
    for {
      in <- Loan(context.getAssets.open(DB_ASSET))
      out <- Loan(new FileOutputStream(DB_FULL_PATH))
    } {
      val buf = new Array[Byte](1024)
      var size = 0
      while({size = in.read(buf); size != -1} ) {
        out.write(buf, 0, size)
      }
      out.flush()
    }
  }

  /**
   * データベースを返す
   * @param writable
   * @return
   */
  def openedDatabase(writable:Boolean = false) = {
    Try {
      database = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, if(writable)SQLiteDatabase.OPEN_READWRITE else SQLiteDatabase.OPEN_READONLY)
      database
    }.recoverWith{
      case e:Exception =>
        Log.e(TAG, s"DB open error. ${e.getMessage}")
        Failure(e)
    }
  }

  override def onUpgrade(p1: SQLiteDatabase, p2: Int, p3: Int): Unit = {}
}
