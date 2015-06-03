package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.content.{Context, Intent}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Filter.FilterResults
import android.widget.{Filter, Filterable, BaseAdapter, TextView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.Route
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.SelectBusStopActivity

/**
 * Created by imae on 2015/05/12.
 */
case class RouteAdapter(routes:Seq[Route])(implicit context:Context) extends BaseAdapter with Filterable {
  private[this] var filtered = routes
  private[this] final val inflater = LayoutInflater.from(context)

  def onRouteClickListener(route:Route) = new OnClickListener {
    override def onClick(routeRow: View): Unit = {
      val intent = new Intent(context, classOf[SelectBusStopActivity])
      intent.putExtra(Route.ROUTE_ID_KEY, route.id)
      intent.putExtra(Route.NAME, route.name)
      context.startActivity(intent)
    }
  }

  override def getCount: Int = filtered.length

  override def getItemId(position: Int): Long = filtered(position).id

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val terminalIs = (s:String) => context.getString(R.string.terminal_is, s)
    Option(convertView).fold{
      val newConvertView:View = inflater.inflate(R.layout.search_route_row, null)
      newConvertView.setTag(Holder.viewHolder)
      Holder.viewHolder.routeName = viewWithFont(findAViewById(newConvertView, R.id.route_name).get)
      Holder.viewHolder.routeName.setText(filtered(position).name)
      Holder.viewHolder.terminalName = viewWithFont(findAViewById(newConvertView, R.id.terminal_name).get)
      Holder.viewHolder.terminalName.setText(terminalIs(filtered(position).terminal))
      newConvertView.setOnClickListener(onRouteClickListener(filtered(position)))
      newConvertView
    }{ v =>
      Holder.viewHolder = convertView.getTag.asInstanceOf[ViewHolder]
      Holder.viewHolder.routeName = findAViewById(convertView, R.id.route_name).get
      Holder.viewHolder.routeName.setText(filtered(position).name)
      Holder.viewHolder.terminalName = findAViewById(convertView, R.id.terminal_name).get
      Holder.viewHolder.terminalName.setText(terminalIs(filtered(position).terminal))
      convertView.setOnClickListener(onRouteClickListener(filtered(position)))
      convertView
    }
  }

  override def getItem(position: Int): AnyRef = filtered(position)

  case class ViewHolder(var routeName:TextView, var terminalName:TextView)

  object Holder {
    var viewHolder:ViewHolder = ViewHolder(null, null)
  }

  override def getFilter: Filter = RouteFilter()

  case class RouteFilter() extends Filter {
    override def performFiltering(constraint: CharSequence): FilterResults = {
      val results = new FilterResults()
      val keywords = constraint.toString.replace("　", " ").split(" ")
      val filtered = routes.filter(route => keywords.forall(route.name.contains))
      results.count = filtered.length
      results.values = filtered
      results
    }

    override def publishResults(constraint: CharSequence, results: FilterResults): Unit = {
      filtered = results.values.asInstanceOf[Seq[Route]]
      notifyDataSetChanged()
    }
  }
}
