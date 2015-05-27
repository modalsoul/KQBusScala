package jp.modal.soul.KeikyuTimeTable.model.entity

import android.content.Context
import android.database.Cursor
import jp.modal.soul.KeikyuTimeTable.model._
import jp.modal.soul.KeikyuTimeTable.util.Loan

import scala.collection.mutable.ArrayBuffer

/**
 * Created by imae on 2015/05/16.
 */
case class Time(
  id:Long,
  busStopId:Long,
  routeId:Long,
  dayType:Int,
  startingTime:String) extends Comparable[Time] with Serializable {
  final val serialVersionUID = 1L

  override def compareTo(another: Time): Int = (this.id - another.id).toInt
}
object Time {
  final val WEEKDAY = 0
  final val SATURDAY = 1
  final val HOLIDAY = 2
}

case class TimeDao(context:Context) extends Dao {
  override final val tableName:String = "time_table"
  final val COLUMN_ID = "id"
  final val COLUMN_BUS_STOP_ID = "bus_stop_id"
  final val COLUMN_ROUTE_ID = "route_id"
  final val COLUMN_WEEK_TYPE = "type"
  final val COLUMN_STARTING_TIME = "starting_time"
  override val COLUMNS: Array[String] = Array(
    COLUMN_ID,
    COLUMN_BUS_STOP_ID,
    COLUMN_ROUTE_ID,
    COLUMN_WEEK_TYPE,
    COLUMN_STARTING_TIME
  )

  private[this] def time(cursor:Cursor) = {
    Time(
      id = cursor.getLong(0),
      busStopId = cursor.getLong(1),
      routeId = cursor.getLong(2),
      dayType = cursor.getInt(3),
      startingTime = cursor.getString(4)
    )
  }

  private[this] def times(columns:Array[String] = COLUMNS,
                               selection:String = null,
                               selectionArgs:Array[String] = null,
                               groupBy:String = null,
                               having:String = null,
                               orderBy:String = null,
                               limit:String = null) = {
    val times = ArrayBuffer[Time]()
    for {
      db <- Loan(readableDatabase)
      cursor <- Loan(db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit))
    } {
      while(cursor.moveToNext()) times.append(time(cursor))
    }
    times.toSeq
  }

  /**
   * 指定路線ID、バス停ID、日種別IDの時間を時間ID順で返す
   * @param routeId
   * @param busStopId
   * @param dayType
   * @return
   */
  private[this] def timesByRouteBusStopDayTypeOrderById(routeId:Long, busStopId:Long, dayType:Int) = {
    times(
      selection = SELECTION_AND(Seq(COLUMN_ROUTE_ID, COLUMN_BUS_STOP_ID, COLUMN_WEEK_TYPE)),
      selectionArgs = SELECTION_ARGS(Seq(routeId, busStopId, dayType)),
      orderBy = ASC(COLUMN_ID)
    )
  }

  /**
   * 平日の時間リストを返す
   * @param routeId
   * @param busStopId
   * @return
   */
  def weekDayTimes(routeId:Long, busStopId:Long) = timesByRouteBusStopDayTypeOrderById(routeId, busStopId, Time.WEEKDAY)

  /**
   * 土曜の時間リストを返す
   * @param routeId
   * @param busStopId
   * @return
   */
  def saturdayTimes(routeId:Long, busStopId:Long) = timesByRouteBusStopDayTypeOrderById(routeId, busStopId, Time.SATURDAY)

  /**
   * 日祝日の時間リストを返す
   * @param routeId
   * @param busStopId
   * @return
   */
  def holidayTimes(routeId:Long, busStopId:Long) = timesByRouteBusStopDayTypeOrderById(routeId, busStopId, Time.HOLIDAY)
}
