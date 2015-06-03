package jp.modal.soul.KeikyuTimeTable.worker

import android.content.{AsyncTaskLoader, Context}
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, BusStopDao, RouteDao}
import jp.modal.soul.KeikyuTimeTable.util.LogTag

/**
 * Created by imae on 2015/05/16.
 */
case class BusStopByRouteLoader(context:Context, routeId:Long) extends AsyncTaskLoader[Seq[BusStop]](context) with LogTag {
  override def loadInBackground(): Seq[BusStop] = {
    val route = RouteDao(context).routeById(routeId).get
    BusStopDao(context).busStopsByIdsWithOrder(route.busStops)
  }
}

case class BusStopByKeywordLoader(context:Context, keywords:Array[String]) extends AsyncTaskLoader[Seq[BusStop]](context) with LogTag {
  override def loadInBackground(): Seq[BusStop] = BusStopDao(context).busStopsWithRouteByName(keywords)
}