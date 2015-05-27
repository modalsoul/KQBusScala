package jp.modal.soul.KeikyuTimeTable.view.fragment.menu

import android.app.{Dialog, DialogFragment}
import android.content.{Context, Intent}
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.view.{View, WindowManager, Window}
import android.widget.{ArrayAdapter, ListView, LinearLayout}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, Route, HistoryPreference}
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity
import jp.modal.soul.KeikyuTimeTable.view.adapter.HistoryAdapter

/**
 * Created by imae on 2015/05/23.
 */
class HistoryFragment extends DialogFragment with LogTag {
  var myDialog:Option[Dialog] = None

  override def onCreateDialog(savedInstance:Bundle): Dialog = {
    myDialog.fold{
      myDialog = Option(new Dialog(getActivity))
      myDialog.map(setupDialog).get
    }(identity)
  }

  def setupDialog(dialog:Dialog):Dialog = {
    dialog.getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    dialog.getWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
    dialog.setContentView(R.layout.history_fragment)

    implicit val context = getActivity
    val list = findAViewById[ListView, Dialog](dialog, R.id.history_list)
    list.get.setAdapter(HistoryAdapter(HistoryPreference.get))
    dialog
  }
}

object HistoryFragment {
  val get = new HistoryFragment
}