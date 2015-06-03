package jp.modal.soul.KeikyuTimeTable.worker

import android.content.{AsyncTaskLoader, Context}
import jp.modal.soul.KeikyuTimeTable.model.entity._
import jp.modal.soul.KeikyuTimeTable.view.adapter.time.TimeTableItem

/**
 * Created by imae on 2015/05/16.
 */
case class TimeTableLoader(context:Context, routeId:Long, busStopId:Long) extends AsyncTaskLoader[TimeTableItem](context) {
  override def loadInBackground(): TimeTableItem = {
    val timeDao = TimeDao(context)
    TimeTableItem(
      route = RouteDao(context).routeById(routeId).get,
      busStop = BusStopDao(context).busStopById(busStopId).get,
      weekday = timeDao.weekDayTimes(routeId, busStopId),
      saturday = timeDao.saturdayTimes(routeId, busStopId),
      holiday = timeDao.holidayTimes(routeId, busStopId)
    )
  }
}