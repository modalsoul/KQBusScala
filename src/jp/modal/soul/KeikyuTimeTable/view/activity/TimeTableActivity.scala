package jp.modal.soul.KeikyuTimeTable.view.activity

import java.util.Calendar

import android.app.ActionBar
import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, HistoryPreference, Route, Time}
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view.adapter.time.TimeTableItem
import jp.modal.soul.KeikyuTimeTable.view.fragment.timetable._
import jp.modal.soul.KeikyuTimeTable.worker.TimeTableLoader

import scala.reflect.ClassTag

/**
 * Created by imae on 2015/05/16.
 */
class TimeTableActivity extends BaseActivity with LoaderCallbacks[TimeTableItem] with LogTag {
  private[this] final val TAB_COUNT:Int = 4
  var routeId:Long = 0
  var busStopId:Long = 0

  var route:Route = null
  var busStop:BusStop = null

  var weekdayTimes:Option[Seq[Time]] = None
  var saturdayTimes:Option[Seq[Time]] = None
  var holidayTimes:Option[Seq[Time]] = None

  var weekdayListener:Option[Seq[Time] => Unit] = None
  var saturdayListener:Option[Seq[Time] => Unit] = None
  var holidayListener:Option[Seq[Time] => Unit] = None

  var trafficListener:Option[(Route, BusStop) => Unit] = None

//  var actionBar:ActionBar = null

  def setOnLoadFinished[T](listener:Seq[Time] => Unit)(implicit c:ClassTag[T]): Unit = {
    import scala.reflect._
    if(c == classTag[WeekdayTabFragment]) weekdayListener = Option(listener)
    else if(c == classTag[SaturdayTabFragment]) saturdayListener = Option(listener)
    else if(c == classTag[HolidayTabFragment]) holidayListener = Option(listener)
//    c match {
//      case _:ClassTag[WeekdayTabFragment] => weekdayListener = Option(listener)
//      case _:ClassTag[SaturdayTabFragment] => saturdayListener = Option(listener)
//      case _:ClassTag[HolidayTabFragment] => holidayListener = Option(listener)
//    }
  }

  def setOnRouteBusStopLoadFinished(listener:(Route, BusStop) => Unit): Unit = {
    trafficListener = Option(listener)
  }

  override def onCreate(bundle:Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.time_table_activity)

    showLoadingSpinner

    routeId = intentValue[Long](Route.ROUTE_ID_KEY)
    busStopId = intentValue[Long](BusStop.BUS_STOP_ID_KEY)

    actionBar = getActionBar

    getLoaderManager.initLoader(0, null, TimeTableActivity.this)
  }

  override def onStart(): Unit = {
    super.onStart()
    if(actionBar.getTabCount < TAB_COUNT) {
      actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS)
      actionBar.addTab(actionBar.newTab().setText("平日").setTabListener(
        new BusStopTabListener[WeekdayTabFragment](this, "weektab", classOf[WeekdayTabFragment])
      ))

      actionBar.addTab(actionBar.newTab().setText("土曜").setTabListener(
        new BusStopTabListener[SaturdayTabFragment](this, "saturdaytab", classOf[SaturdayTabFragment])
      ))

      actionBar.addTab(actionBar.newTab().setText("休日").setTabListener(
        new BusStopTabListener[HolidayTabFragment](this, "holidaytab", classOf[HolidayTabFragment])
      ))

      actionBar.addTab(actionBar.newTab().setText("運行情報").setTabListener(
        new BusStopTabListener[TrafficTabFragment](this, "traffictab", classOf[TrafficTabFragment])
      ))
    }
  }


  override def onCreateLoader(id: Int, bundle: Bundle): Loader[TimeTableItem] = {
    val loader = TimeTableLoader(this, routeId, busStopId)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(p1: Loader[TimeTableItem]): Unit = {}

  override def onLoadFinished(loader: Loader[TimeTableItem], data: TimeTableItem): Unit = {
    route = data.route
    busStop = data.busStop

    setTitle(busStop.name)

    HistoryPreference.add(route, busStop)

    weekdayTimes = Option(data.weekday)
    saturdayTimes = Option(data.saturday)
    holidayTimes = Option(data.holiday)

    weekdayListener.foreach(_(weekdayTimes.get))
    saturdayListener.foreach(_(saturdayTimes.get))
    holidayListener.foreach(_(holidayTimes.get))

    trafficListener.foreach(_(route, busStop))

    Calendar.getInstance().get(Calendar.DAY_OF_WEEK) match {
      case Calendar.SATURDAY => actionBar.selectTab(actionBar.getTabAt(1))
      case Calendar.SUNDAY => actionBar.selectTab(actionBar.getTabAt(2))
      case _ =>
    }
    hideLoadingSpinner
  }

}
