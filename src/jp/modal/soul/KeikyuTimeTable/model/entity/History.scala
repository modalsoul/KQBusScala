package jp.modal.soul.KeikyuTimeTable.model.entity

import android.content.Context

/**
 * Created by imae on 2015/05/23.
 */
case class History(routeId:Long, busStopId:Long, routeName:String, busStopName:String) {
  def toCSV = s"$routeId,$busStopId,$routeName,$busStopName"
}
object History {
  def apply(str:String):Option[History] = {
    if(str.isEmpty) None
    else {
      val csv = str.split(",")
      Option(History(csv(0).toLong, csv(1).toLong, csv(2), csv(3)))
    }
  }
}

object HistoryPreference {
  private[this] final val SP_NAME:String = "history"
  private[this] final val MAX_HISTORY:Int = 5
  private[this] val KEY = (index:Int) => s"${SP_NAME}_$index"

  def get(implicit context:Context):Seq[History] = {
    (for(index <- 0 until MAX_HISTORY) yield History(sharedPreference.getString(KEY(index),""))).flatten.distinct
  }

  def sharedPreference(implicit context:Context) = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
  def add(route:Route, busStop:BusStop)(implicit context:Context): Unit = {
    val edit = sharedPreference.edit()
    for {
      (history, index) <-
      (History(route.id, busStop.id, route.name, busStop.name) +: get.take(MAX_HISTORY)).zipWithIndex
    } {
      edit.putString(KEY(index), history.toCSV)
    }
    edit.commit()
  }
}

