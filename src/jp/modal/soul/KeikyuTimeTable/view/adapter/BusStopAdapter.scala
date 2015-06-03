package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.content.{Intent, Context}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Filter.FilterResults
import android.widget._
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{Route, BusStop}
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity

/**
 * Created by imae on 2015/05/16.
 */
case class BusStopAdapter(busStops:Seq[BusStop], needRouteName:Boolean = false)(implicit context:Context) extends BaseAdapter with Filterable {
  private[this] var filtered = busStops
  private[this] final val inflater = LayoutInflater.from(context)

  def onBusStopClickListener(busStopId:Long, routeId:Long) = new OnClickListener {
    override def onClick(busStopRow:View): Unit = {
      val intent = new Intent(context, classOf[TimeTableActivity])
      intent.putExtra(Route.ROUTE_ID_KEY, routeId)
      intent.putExtra(BusStop.BUS_STOP_ID_KEY, busStopId)
      context.startActivity(intent)
    }
  }

  override def getCount: Int = filtered.length

  override def getItemId(position: Int): Long = filtered(position).id

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val busStop = filtered(position)
    Option(convertView).fold {
      val newConvertView:View = if(needRouteName) {
        inflater.inflate(R.layout.search_busstop_row, null)
      } else {
        inflater.inflate(R.layout.select_busstop_row, null)
      }
      newConvertView.setTag(Holder.viewHolder)
      Holder.viewHolder.busStopName = viewWithFont(findAViewById(newConvertView, R.id.bus_stop_name).get)
      Holder.viewHolder.busStopName.setText(busStop.name)
      if(needRouteName) {
        Holder.viewHolder.routeName = textById(newConvertView, R.id.route_name).get
        Holder.viewHolder.routeName.setText(busStop.routeName.getOrElse(""))
      }
      newConvertView.setOnClickListener(onBusStopClickListener(busStop.id, busStop.routeId))
      newConvertView
    }{ v =>
      Holder.viewHolder = convertView.getTag.asInstanceOf[ViewHolder]
      Holder.viewHolder.busStopName = findAViewById(convertView, R.id.bus_stop_name).get
      Holder.viewHolder.busStopName.setText(busStop.name)
      if(needRouteName) {
        Holder.viewHolder.routeName = textById(convertView, R.id.route_name).get
        Holder.viewHolder.routeName.setText(busStop.routeName.getOrElse(""))
      }
      convertView.setOnClickListener(onBusStopClickListener(busStop.id, busStop.routeId))
      convertView
    }
  }

  override def getItem(position: Int): AnyRef = filtered(position)

  case class ViewHolder(var busStopName:TextView, var routeName:TextView)

  object Holder {
    var viewHolder:ViewHolder = ViewHolder(null, null)
  }

  override def getFilter: Filter = BusStopFilter()

  case class BusStopFilter() extends Filter {
    override def performFiltering(constraint: CharSequence): FilterResults = {
      val results = new FilterResults()
      val keywords = constraint.toString.replace("　", " ").split(" ")
      val filtered = busStops.filter{
        busStop =>
          keywords.forall(busStop.name.contains) || keywords.forall(busStop.routeName.get.contains)
      }
      results.count = filtered.length
      results.values = filtered
      results
    }

    override def publishResults(constraint: CharSequence, results: FilterResults): Unit = {
      filtered = results.values.asInstanceOf[Seq[BusStop]]
      notifyDataSetChanged()
    }
  }
}
