package jp.modal.soul.KeikyuTimeTable.worker

import android.content.{AsyncTaskLoader, Context}
import jp.modal.soul.KeikyuTimeTable.model.entity._
import jp.modal.soul.KeikyuTimeTable.view.adapter.time.TimeTableItem

/**
 * Created by imae on 2015/05/16.
 */
case class TimeTableLoader(context:Context, routeId:Long, busStopId:Long) extends AsyncTaskLoader[TimeTableItem](context) {
  override def loadInBackground(): TimeTableItem = {
    val route = RouteDao(context).routeById(routeId).get
    val busStop = BusStopDao(context).busStopById(busStopId).get
    val timeDao = TimeDao(context)
    val weekday = timeDao.weekDayTimes(routeId, busStopId)
    val saturday = timeDao.saturdayTimes(routeId, busStopId)
    val holiday = timeDao.holidayTimes(routeId, busStopId)
    TimeTableItem(route, busStop, weekday, saturday, holiday)
  }
}