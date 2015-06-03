package jp.modal.soul.KeikyuTimeTable.view.fragment.timetable

import android.app.{ActionBar, Fragment, FragmentTransaction}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity

/**
 * Created by imae on 2015/05/16.
 */
case class BusStopTabListener[T<:Fragment](activity:TimeTableActivity, tag:String, cls:Class[T]) extends ActionBar.TabListener with LogTag {
  private[this] var fragment = activity.getFragmentManager.findFragmentByTag(tag)

  def onTabSelected(tab:ActionBar.Tab, ft:FragmentTransaction): Unit = {
    if(fragment == null) {
      fragment = Fragment.instantiate(activity, cls.getName)
      ft.add(R.id.bus_stop_container, fragment, tag)
    } else {
      if(fragment.isDetached) ft.attach(fragment)
    }
  }

  def onTabUnselected(tab:ActionBar.Tab, ft:FragmentTransaction): Unit = {
    if(fragment != null) ft.detach(fragment)
    activity.showLoadingSpinner
  }

  def onTabReselected(tab:ActionBar.Tab, ft:FragmentTransaction): Unit = {
    if(tab == activity.actionBar.getTabAt(3)) fragment.asInstanceOf[TrafficTabFragment].reload
  }
}
