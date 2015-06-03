package jp.modal.soul.KeikyuTimeTable.view.fragment.timetable

import java.util.Date

import android.app.Fragment
import android.os.Bundle
import android.text.format.DateFormat
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, ListView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.Time
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity
import jp.modal.soul.KeikyuTimeTable.view.adapter.TimeAdapter

/**
 * Created by imae on 2015/05/16.
 */
abstract class TimeTabFragment extends Fragment with LogTag {
  val layoutId:Int
  val tabId:Int
  var view:View = null
  implicit var context:TimeTableActivity = null
  val hour = DateFormat.format("H", new Date(System.currentTimeMillis)).toString.toInt

  override def onCreate(savedInstanceState:Bundle): Unit = {
    super.onCreate(savedInstanceState)
    context = getActivity.asInstanceOf[TimeTableActivity]
  }

  override def onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(layoutId, container, false)
    view
  }

  def showTimeTable(view:View)(times:Seq[Time]): Unit ={
    val tab = findAViewById[LinearLayout, View](view, tabId).get
    val listView = context.getLayoutInflater.inflate(R.layout.time_table_list, null).asInstanceOf[ListView]
    tab.addView(listView)
    listView.setAdapter(TimeAdapter(times))
    times.zipWithIndex.find(_._1.startingTime.startsWith(s"$hour:")).foreach{
      case (t,i) => listView.setSelection(i)
    }
    context.hideLoadingSpinner
  }
}

class WeekdayTabFragment extends TimeTabFragment {
  override val layoutId: Int = R.layout.weekday_tab_fragment
  override val tabId: Int = R.id.weekday_tab

  override def onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle): View = {
    view = super.onCreateView(inflater, container, savedInstanceState)
    context.weekdayTimes.fold(context.setOnLoadFinished[WeekdayTabFragment](showTimeTable(view)))(showTimeTable(view))
    view
  }
}

class SaturdayTabFragment extends TimeTabFragment {
  override val layoutId: Int = R.layout.saturday_tab_fragment
  override val tabId: Int = R.id.saturday_tab

  override def onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle): View = {
    view = super.onCreateView(inflater, container, savedInstanceState)
    context.weekdayTimes.fold(context.setOnLoadFinished[SaturdayTabFragment](showTimeTable(view)))(showTimeTable(view))
    view
  }
}

class HolidayTabFragment extends TimeTabFragment {
  override val layoutId: Int = R.layout.holiday_tab_fragment
  override val tabId: Int = R.id.holiday_tab

  override def onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle): View = {
    view = super.onCreateView(inflater, container, savedInstanceState)
    context.weekdayTimes.fold(context.setOnLoadFinished[HolidayTabFragment](showTimeTable(view)))(showTimeTable(view))
    view
  }
}
