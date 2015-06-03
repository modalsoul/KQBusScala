package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseAdapter, TextView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.Time
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._

/**
 * Created by imae on 2015/05/16.
 */
case class TimeAdapter(times:Seq[Time])(implicit context:Context) extends BaseAdapter with LogTag {
  private[this] final val inflater = LayoutInflater.from(context)

  override def getCount: Int = times.length

  override def getItemId(position: Int): Long = times(position).id

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val time = times(position)
    Option(convertView).fold {
      val newConvertView:View = inflater.inflate(R.layout.time_table_row, null)
      newConvertView.setTag(Holder.viewHolder)
      Holder.viewHolder.time = viewWithFont(findAViewById(newConvertView, R.id.time).get)
      Holder.viewHolder.time.setText(time.startingTime)
      newConvertView
    }{ v =>
      Holder.viewHolder = convertView.getTag.asInstanceOf[ViewHolder]
      Holder.viewHolder.time = findAViewById(convertView, R.id.time).get
      Holder.viewHolder.time.setText(time.startingTime)
      convertView
    }
  }

  override def getItem(position: Int): AnyRef = times(position)

  case class ViewHolder(var time:TextView)

  object Holder {
    var viewHolder:ViewHolder = ViewHolder(null)
  }
}
