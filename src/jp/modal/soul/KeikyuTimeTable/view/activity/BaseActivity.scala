package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.{ActionBar, Dialog, Activity}
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.{WindowManager, Window, View}
import android.widget.{Button, TextView}
import jp.modal.soul.KeikyuTimeTable.{view, R}
import jp.modal.soul.KeikyuTimeTable.util.{LogTag, Const}
import jp.modal.soul.KeikyuTimeTable.view._

import scala.reflect.ClassTag

/**
 * Created by imae on 2015/04/07.
 */
class BaseActivity extends Activity with LogTag {
  implicit val context = this
  var dialog:Dialog = null

  var actionBar:ActionBar = null

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    actionBar = getActionBar

    val titleId = getResources.getIdentifier("action_bar_title", "id", "android")

    val title = findMyViewById[TextView](titleId).map(viewWithFont(_)).get
    title.setTextSize(Const.TITLE_FONT_SIZE)
    title.setTextColor(R.color.main_white)


//    actionBar.setDisplayShowCustomEnabled(true)
//    actionBar.setDisplayShowTitleEnabled(true)
//
//    val inflator = LayoutInflater.from(this)
//    val view = inflator.inflate(R.layout.action_bar, null)
//    view.findViewById(R.id.title_string)
//    findAViewById[TextView, View](view, R.id.title_string).map(viewWithFont)
//
//    actionBar.setCustomView(view)
  }

  /**
   * 指定IDに該当するT型のViewを返す
   * @param id
   * @tparam T
   * @return
   */
  def findMyViewById[T <: View](id:Int):Option[T] = findAViewById[T, Activity](this, id)

  /**
   * 指定IDのTextViewをフォント設定済みの状態で返す
   */
  lazy val textViewById:Int => Option[TextView] = (id:Int) => view.textById(this, id)
//  lazy val textViewById:Int => Option[TextView] = (id:Int) => findMyViewById[TextView](id).map(viewWithFont(_))
  /**
   * 指定IDに該当するButtonをフォント設定済みの状態で返す
   */
  lazy val buttonById:Int => Option[Button] = (id:Int) => view.buttonById(this, id)
//  lazy val buttonById:Int => Option[Button] = (id:Int) => findMyViewById[Button](id).map(viewWithFont(_))


  def intentValue[T](key:String)(implicit c:ClassTag[T]):T = {
    val extras = getIntent.getExtras()
    import scala.reflect._

    if(c == classTag[Long]) extras.getLong(key).asInstanceOf[T]
    else if(c == classTag[String]) extras.getString(key).asInstanceOf[T]
    else extras.getString(key).asInstanceOf[T]

//    c match {
//      case classTag[Long] =>
//        Log.e(TAG, "LONG")
//        extras.getLong(key).asInstanceOf[T]
//      case classTag[String] =>
//        Log.e(TAG, "String")
//        extras.getString(key).asInstanceOf[T]
//      case _ => extras.getString(key).asInstanceOf[T]
//    }
  }

  def showLoadingSpinner: Unit = {
    dialog = Loading.dialog(this)
    if(dialog != null) dialog.show()
  }

  def hideLoadingSpinner: Unit = {
    if(dialog != null && dialog.isShowing) {
      dialog.dismiss()
      dialog = null
    }
  }
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
