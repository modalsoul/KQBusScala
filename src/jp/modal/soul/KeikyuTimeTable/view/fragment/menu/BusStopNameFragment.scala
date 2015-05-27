package jp.modal.soul.KeikyuTimeTable.view.fragment.menu

import android.app.{Dialog, DialogFragment}
import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{View, Window, WindowManager}
import android.widget.{Button, EditText}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.BusStop
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.SearchBusStopActivity

/**
 * Created by imae on 2015/05/21.
 */
class BusStopNameFragment extends DialogFragment with LogTag {
  var myDialog:Option[Dialog] = None

  var editText:EditText = null
  var cancel:Button = null
  var clear:Button = null
  var search:Button = null

  override def onCreateDialog(savedInstance:Bundle): Dialog = {
    myDialog.fold{
      myDialog = Option(new Dialog(getActivity()))
      myDialog.map(setupDialog).get
    }(identity)
  }

  /**
   * ダイアログ内のviewを組み立てて、ダイアログを返す
   * @param dialog
   * @return
   */
  def setupDialog(dialog:Dialog):Dialog = {
    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.getWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    dialog.setContentView(R.layout.bus_stop_name_fragment)
    implicit val context = getActivity()
    val label = textById(dialog, R.id.dialog_search_label)
    editText = findAViewById[EditText, Dialog](dialog, R.id.bus_stop_dialog_input).get
    cancel = buttonById(dialog, R.id.dialog_cancel).get
    cancel.setOnClickListener(onCancelClickListener)
    clear = buttonById(dialog, R.id.dialog_clear).get
    clear.setOnClickListener(onClearClickListener)
    search = buttonById(dialog, R.id.dialog_search).get
    search.setOnClickListener(onSearchClickListener)

    dialog
  }

  val onCancelClickListener = new OnClickListener {
    override def onClick(p1: View): Unit = dismiss()
  }

  val onClearClickListener = new OnClickListener {
    override def onClick(p1: View): Unit = editText.getText.clear()
  }

  val onSearchClickListener = new OnClickListener {
    override def onClick(p1: View): Unit = {
      val keyword = editText.getText().toString.replace("　", " ").trim
      if(!keyword.isEmpty) {
        val intent = new Intent(getActivity, classOf[SearchBusStopActivity])
        intent.putExtra(BusStop.BUS_STOP_SEARCH_KEY, keyword)
        getActivity.startActivity(intent)
      }
    }
  }
}

object BusStopNameFragment {
  val get = new BusStopNameFragment
}
