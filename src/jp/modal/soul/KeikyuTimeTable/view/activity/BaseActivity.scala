package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.{ActionBar, Activity, Dialog}
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.{MenuItem, View, Window, WindowManager}
import android.widget.{Button, TextView}
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.{R, view}

import scala.reflect.ClassTag

/**
 * Created by imae on 2015/04/07.
 */
class BaseActivity extends Activity with LogTag {
  implicit val context = this
  private[this] var showHomeAsUp:Boolean = true
  var dialog:Dialog = null
  var actionBar:ActionBar = null
  var title:Option[TextView] = None
  var kari:Option[TextView] = None

  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    actionBar = getActionBar
    actionBar.setDisplayShowTitleEnabled(false)
    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM)
    actionBar.setCustomView(R.layout.action_bar)
    val customView = actionBar.getCustomView
    title = textById(customView, R.id.title_string)
    kari = textById(customView, R.id.kari)
  }

  def setTitle(str:String): Unit = {
    title.foreach(_.setText(str))
    kari.foreach(_.setVisibility(View.GONE))
  }

  override def onStart(): Unit ={
    super.onStart()
    actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp)
  }

  override def onOptionsItemSelected(item:MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home => finish()
      case _ =>
    }
    super.onOptionsItemSelected(item)
  }

  def disableHomeAsUp = showHomeAsUp = false


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
  /**
   * 指定IDに該当するButtonをフォント設定済みの状態で返す
   */
  lazy val buttonById:Int => Option[Button] = (id:Int) => view.buttonById(this, id)

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
