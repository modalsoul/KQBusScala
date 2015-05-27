package jp.modal.soul.KeikyuTimeTable.model.entity

import android.content.Context
import android.database.Cursor
import android.util.Log
import jp.modal.soul.KeikyuTimeTable.model._
import jp.modal.soul.KeikyuTimeTable.util.Loan

import scala.collection.mutable.ArrayBuffer

/**
 * Created by imae on 2015/05/16.
 */
case class BusStop(
  id:Long,
  routeId:Long,
  name:String,
  search:Int,
  routeName:Option[String] = None) extends Comparable[BusStop] with Serializable {
  final val serialVersionUID = 1L

  override def compareTo(another: BusStop):Int = (this.id - another.id).toInt
}
object BusStop {
  final val BUS_STOP_ID_KEY = "bus_stop_id"
  final val BUS_STOP_SEARCH_KEY = "bus_stop_keyword"
}

case class BusStopDao(context:Context) extends Dao {
  override final val tableName: String = "bus_stop"
  final val COLUMN_ID = "id"
  final val COLUMN_ROUTE_ID = "route_id"
  final val COLUMN_NAME = "bus_stop_name"
  final val COLUMN_SEARCH = "search"
  override val COLUMNS: Array[String] = Array(
    COLUMN_ID,
    COLUMN_ROUTE_ID,
    COLUMN_NAME,
    COLUMN_SEARCH
  )

  private[this] def busStop(cursor:Cursor) = {
    BusStop(
      id = cursor.getLong(0),
      routeId = cursor.getLong(1),
      name = cursor.getString(2),
      search = cursor.getInt(3)
    )
  }

  private[this] def busStops(columns:Array[String] = COLUMNS,
                             selection:String = null,
                             selectionArgs:Array[String] = null,
                             groupBy:String = null,
                             having:String = null,
                             orderBy:String = null,
                             limit:String = null) = {
    val busStops = new ArrayBuffer[BusStop]()
    for {
      db <- Loan(readableDatabase)
      cursor <- Loan(db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy, limit))
    } {
      while(cursor.moveToNext()) busStops.append(busStop(cursor))
    }
    busStops.toSeq
  }

  /**
   * バス停名から検索した結果を返す
   * @param keywords
   * @return
   */
  def busStopsWithRouteByName(keywords:Array[String]) = {
    val selectionArgs = keywords.map(key => s"%$key%")
    val query = s"select * from $tableName b, route r where b.${COLUMN_ROUTE_ID} = r.id and " +
      keywords.tail.fold(s"b.$COLUMN_NAME like ?")((z, n) => s"$z and b.$COLUMN_NAME like ?")

    val busStops = new ArrayBuffer[BusStop]()
    for {
      db <- Loan(readableDatabase)
      cursor <- Loan(db.rawQuery(query, selectionArgs))
    } {
      while(cursor.moveToNext()) {
        busStops.append(BusStop(
          id = cursor.getLong(0),
          routeId = cursor.getLong(1),
          name = cursor.getString(2),
          search = cursor.getInt(3),
          routeName = Option(cursor.getString(5))
        ))
      }
    }
    busStops.toSeq
  }

  /**
   * バス停ID順のバス停リストを返す
   * @return
   */
  def busStopsOrderById = busStops(orderBy = ASC(COLUMN_ID))

  /**
   * 指定したバス停IDのバス停リストを指定ID順で返す
   * @param ids
   * @return
   */
  def busStopsByIdsWithOrder(ids:Seq[Long]) = {
    val result = busStops(
      selection = SELECTION_OR(COLUMN_ID)(ids.length),
      selectionArgs = SELECTION_ARGS(ids)
    )

    ids.flatMap{
      id => result.find(_.id == id)
    }
  }

  /***
    * 指定IDのバス停を返す
    * @param id
    * @return
    */
  def busStopById(id:Long) = busStops(selection = SELECTION(COLUMN_ID), selectionArgs = SELECTION_ARG(id)).headOption

}