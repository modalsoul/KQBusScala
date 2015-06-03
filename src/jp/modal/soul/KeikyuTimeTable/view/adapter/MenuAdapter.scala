package jp.modal.soul.KeikyuTimeTable.view.adapter

import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{BaseAdapter, Button, ImageView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.MenuActivity
import jp.modal.soul.KeikyuTimeTable.view.adapter.menu.{MenuGridItems, MenuItem}

/**
 * Created by imae on 2015/04/11.
 */
case class MenuAdapter()(implicit context:MenuActivity) extends BaseAdapter {
  private[this] final val inflater = LayoutInflater.from(context)

  private[this] final val menuItems = MenuGridItems()

  override def getCount: Int = menuItems.count

  override def getItemId(position: Int): Long = position

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    if(convertView == null) {
      inflateViewHolder(menuItems.get(position))
    } else {
      getViewHolder(convertView, menuItems.get(position))
    }
  }

  override def getItem(position: Int): AnyRef = menuItems.get(position)

  private[this] def inflateViewHolder(menuItem:MenuItem):View = {
    val newConvertView:View = inflater.inflate(R.layout.menu_grid_item, null)
    Holder.viewHolder.menuButton = findAViewById[Button, View](newConvertView, R.id.grid_item_button).get
    newConvertView.setTag(Holder.viewHolder)
    Holder.viewHolder.menuButton.setBackgroundResource(menuItem.colorId)
    Holder.viewHolder.menuButton.setText(menuItem.name)
    Holder.viewHolder.menuButton = viewWithFont(Holder.viewHolder.menuButton)
    Holder.viewHolder.menuButton.setOnClickListener(menuItem.onClick)
    newConvertView
  }

  private[this] def getViewHolder(convertView:View, menuItem:MenuItem) = {
    Holder.viewHolder = convertView.getTag().asInstanceOf[ViewHolder]
    Holder.viewHolder.menuButton.setBackgroundResource(menuItem.colorId)
    Holder.viewHolder.menuButton = viewWithFont(Holder.viewHolder.menuButton)
    Holder.viewHolder.menuButton.setOnClickListener(menuItem.onClick)
    convertView
  }

  case class ViewHolder(var menuImage:ImageView = null, var menuButton:Button = null)

  object Holder {
    var viewHolder:ViewHolder = ViewHolder()
  }
}
