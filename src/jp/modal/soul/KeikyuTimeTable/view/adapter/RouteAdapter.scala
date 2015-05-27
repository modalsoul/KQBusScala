package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.content.{Context, Intent}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseAdapter, TextView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.Route
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.SelectBusStopActivity

/**
 * Created by imae on 2015/05/12.
 */
case class RouteAdapter(routes:Seq[Route])(implicit context:Context) extends BaseAdapter {
  private[this] final val inflater = LayoutInflater.from(context)

  def onRouteClickListener(route:Route) = new OnClickListener {
    override def onClick(routeRow: View): Unit = {
      val intent = new Intent(context, classOf[SelectBusStopActivity])
      intent.putExtra(Route.ROUTE_ID_KEY, route.id)
      intent.putExtra(Route.NAME, route.name)
      context.startActivity(intent)
    }
  }

  override def getCount: Int = routes.length

  override def getItemId(position: Int): Long = routes(position).id

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    Option(convertView).fold{
      val newConvertView:View = inflater.inflate(R.layout.search_route_row, null)
      newConvertView.setTag(Holder.viewHolder)
      Holder.viewHolder.routeName = viewWithFont(findAViewById(newConvertView, R.id.route_name).get)
      Holder.viewHolder.routeName.setText(routes(position).name)
      Holder.viewHolder.terminalName = viewWithFont(findAViewById(newConvertView, R.id.terminal_name).get)
      Holder.viewHolder.terminalName.setText(routes(position).terminal)
      newConvertView.setOnClickListener(onRouteClickListener(routes(position)))
      newConvertView
    }{ v =>
      Holder.viewHolder = convertView.getTag.asInstanceOf[ViewHolder]
      Holder.viewHolder.routeName = findAViewById(convertView, R.id.route_name).get
      Holder.viewHolder.routeName.setText(routes(position).name)
      Holder.viewHolder.terminalName = findAViewById(convertView, R.id.terminal_name).get
      Holder.viewHolder.terminalName.setText(routes(position).terminal)
      convertView.setOnClickListener(onRouteClickListener(routes(position)))
      convertView
    }
  }

  override def getItem(position: Int): AnyRef = routes(position)

  case class ViewHolder(var routeName:TextView, var terminalName:TextView)

  object Holder {
    var viewHolder:ViewHolder = ViewHolder(null, null)
  }
}
