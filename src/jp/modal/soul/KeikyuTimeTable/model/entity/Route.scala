package jp.modal.soul.KeikyuTimeTable.model.entity

import android.content.Context
import android.database.Cursor
import jp.modal.soul.KeikyuTimeTable.util.Loan

import scala.collection.mutable.ArrayBuffer

/**
 * Created by imae on 2015/05/11.
 */
case class Route (
  id:Long,
  name:String,
  terminal:String,
  starting:String,
  busStops:Seq[Long],
  areaId:Long) extends Comparable[Route] with Serializable {
  lazy val terminalId = busStops.last
  final val serialVersionUID = 2L
  override def compareTo(another: Route): Int = (this.id - another.id).toInt
}
object Route {
  final val ROUTE_ID_KEY = "route_id"
  final val NAME = "route_name"
}

case class RouteDao(context:Context) extends Dao {
  override val tableName: String = "route"
  /** 路線ID　*/
  final val COLUMN_ID = "id"
  /** 路線名 */
  final val COLUMN_ROUTE_NAME = "route_name"
  /** 終着バス停名 */
  final val COLUMN_TERMINAL = "terminal"
  /** 始発バス停名 */
  final val COLUMN_STARTING = "starting"
  /** バス停 */
  final val COLUMN_BUS_STOPS = "bus_stops"
  /** エリアID */
  final val COLUMN_AREA_ID = "area_id"

  override val COLUMNS: Array[String] = Array(
    COLUMN_ID,
    COLUMN_ROUTE_NAME,
    COLUMN_TERMINAL,
    COLUMN_STARTING,
    COLUMN_BUS_STOPS,
    COLUMN_AREA_ID
  )

  private[this] def route(cursor:Cursor) = {
    Route(
    id = cursor.getLong(0),
    name = cursor.getString(1),
    terminal = cursor.getString(2),
    starting = cursor.getString(3),
    busStops = cursor.getString(4).split(",").map(_.trim.toLong),
    areaId = cursor.getInt(5)
    )
  }

  private[this] def routes(columns:Array[String] = COLUMNS,
                           selection:String = null,
                           selectionArgs:Array[String] = null,
                           groupBy:String = null,
                           having:String = null,
                           orderBy:String = null,
                           limit:String = null) = {
    val routes = new ArrayBuffer[Route]()
    for {
      db <- Loan(readableDatabase)
      cursor <- Loan(db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit))
    } {
      while(cursor.moveToNext()) routes.append(route(cursor))
    }
    routes.toSeq
  }

  /**
   * 路線ID順の路線リストを返す
   * @return
   */
  def routesOrderById = routes(orderBy = ASC(COLUMN_ID))

  /**
   * 指定路線IDの路線を返す
   * @param id
   * @return
   */
  def routeById(id:Long) = routes(selection = SELECTION(COLUMN_ID), selectionArgs = SELECTION_ARG(id)).headOption

  def routeByName(keyword:String) = {
    keyword.split("""[ 　]""")
  }
}


