package jp.modal.soul.KeikyuTimeTable.migration

import android.content.Context
import android.util.Log
import jp.modal.soul.KeikyuTimeTable.model.DBHelper
import jp.modal.soul.KeikyuTimeTable.util.LogTag

/**
 * Created by imae on 2015/05/11.
 */
case class AppMigration(context:Context) extends LogTag {
  private[this] final val UN_BOOTED = 0 /* 未起動 */
  private[this] final val BOOTED = 1 /* 起動 */

  val dbHelper = DBHelper(context)

  def check(): Unit ={
    get("BOOT_STATE")("INIT_STATE") match {
      case BOOTED =>
        if(dbHelper.needMigration(get("DB_VERSION")("DB_STATE"))) {
          dbHelper.migration.foreach(version => put("DB_VERSION", version)("DB_STATE"))
        }
      case _ =>
        dbHelper.createEmptyDatabase
        put("BOOT_STATE", BOOTED)("INIT_STATE")
    }
  }

  /**
   * 共有プリファレンスの指定キーに値をセットする
   * @param key
   * @param value
   * @param preferenceName
   * @return
   */
  private[this] def put(key:String, value:Int)(preferenceName:String):Unit = {
    val sp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    sp.edit().putInt(key, value).commit()
  }

  /**
   * 共有プリファレンスの指定キー値を返す
   * @param key
   * @param preferenceName
   * @return
   */
  private[this] def get(key:String)(preferenceName:String) = {
    context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE).getInt(key, 0)
  }
}
