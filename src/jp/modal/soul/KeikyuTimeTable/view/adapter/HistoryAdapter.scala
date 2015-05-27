package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.content.{Context, Intent}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseAdapter, TextView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, History, Route}
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity

/**
 * Created by imae on 2015/05/24.
 */
case class HistoryAdapter(histories:Seq[History])(implicit context:Context) extends BaseAdapter {
  private[this] final val inflater = LayoutInflater.from(context)

  override def getCount: Int = histories.length

  override def getItemId(position: Int): Long = position

  override def getView(position: Int, row: View, parent: ViewGroup): View = {
    val history = histories(position)
    val newRow = Option(row).fold{
      val newRow = inflater.inflate(R.layout.search_busstop_row, null)
      textById(newRow, R.id.route_name)
      textById(newRow, R.id.bus_stop_name)
      newRow
    }(identity)
    findAViewById[TextView, View](newRow, R.id.route_name).foreach(_.setText(history.routeName))
    findAViewById[TextView, View](newRow, R.id.bus_stop_name).foreach(_.setText(history.busStopName))
    newRow.setOnClickListener(onClickListener(history.routeId, history.busStopId))
    newRow
  }

  override def getItem(position: Int): AnyRef = histories(position)

  def onClickListener(routeId:Long, busStopId:Long)(implicit context:Context) = new OnClickListener {
    override def onClick(p1: View): Unit = {
      val intent = new Intent(context, classOf[TimeTableActivity])
      intent.putExtra(Route.ROUTE_ID_KEY, routeId)
      intent.putExtra(BusStop.BUS_STOP_ID_KEY, busStopId)
      context.startActivity(intent)
    }
  }
}
