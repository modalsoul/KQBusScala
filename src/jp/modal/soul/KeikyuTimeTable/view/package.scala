package jp.modal.soul.KeikyuTimeTable

import android.app.{Dialog, Activity}
import android.content.Context
import android.content.res.AssetManager
import android.graphics.{Color, Typeface}
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.{WindowManager, Window, View}
import android.widget.{EditText, TextView, Button}
import jp.modal.soul.KeikyuTimeTable.util.LogTag

/**
 * Created by imae on 2015/04/11.
 */
package object view extends LogTag {
  final val FONT = "RiiPopkkR.otf"
  final val face = (assets:AssetManager) => Typeface.createFromAsset(assets, FONT)

  final val MENU_ROUTE_ID = 0
  final val MENU_BUS_STOP_ID = 1
  final val MENU_HISTORY_ID = 2

  /**
   * フォント設定可能なインスタンスにフォント設定を施し、返す
   * @param t
   * @tparam T
   * @return
   */
  def viewWithFont[T<:{ def setTypeface(tf:Typeface):Unit}, S<:{ def getAssets():AssetManager }](t:T)(implicit context:S) = {
    t.setTypeface(face(context.getAssets))
    t
  }

  /**
   * 対象から指定IDに該当するA型のViewを返す
   * @param self
   * @param id
   * @tparam A
   * @tparam B
   * @return
   */
  def findAViewById[A <: View, B <: { def findViewById(id:Int):View }](self:B, id:Int):Option[A] = {
    Option(self.findViewById(id)).map { v =>
      val tv = v.asInstanceOf[A]
      tv
    }
  }

  def buttonById[T <: { def findViewById(id:Int):View }](self:T, id:Int)(implicit context:Context) = {
    findAViewById[Button, T](self, id).map(viewWithFont(_))
  }

  def textById[T <: { def findViewById(id:Int):View }](self:T, id:Int)(implicit context:Context) = {
    findAViewById[TextView, T](self, id).map(viewWithFont(_))
  }

  object Loading {
    def dialog(activity:Activity) = {
      val dialog = new Dialog(activity)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.getWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
      dialog.getWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT))
      dialog.setContentView(R.layout.progress)
      dialog.setCancelable(false)
      dialog
    }
  }
}
