package jp.modal.soul.KeikyuTimeTable.view.adapter.menu


import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, History, HistoryPreference, Route}
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view.activity.{MenuActivity, SearchRouteActivity, TimeTableActivity}
import jp.modal.soul.KeikyuTimeTable.view.fragment.menu.{BusStopNameFragment, HistoryFragment}

/**
 * Created by imae on 2015/04/22.
 */
case class MenuGridItems(implicit context:MenuActivity) extends LogTag {
  private[this] val MAX_MENU_HISTORY_COUNT = 5
  /**
   * 路線から探すボタンクリックのリスナー
   */
  def onMenuRouteClickListener = new OnClickListener {
    def onClick(p1: View): Unit = {
      val intent = new Intent(context, classOf[SearchRouteActivity])
      context.startActivity(intent)
    }
  }

  /**
   * バス停から探すボタンクリックのリスナー
   * @return
   */
  def onMenuBusStopClickListener = new OnClickListener {
    def onClick(p1: View): Unit = {
      BusStopNameFragment.get.show(context.getFragmentManager, "search_bus_stop")
    }
  }

  /**
   * 履歴から探すボタンクリックのリスナー
   * @return
   */
  def onMenuHistoryClickListener = new OnClickListener {
    override def onClick(p1: View): Unit = {
      HistoryFragment.get.show(context.getFragmentManager, "history_dialog")
    }
  }

  def onHistoryClickListener(routeId:Long, busStopId:Long) = new OnClickListener {
    override def onClick(p1: View): Unit = {
      val intent = new Intent(context, classOf[TimeTableActivity])
      intent.putExtra(Route.ROUTE_ID_KEY, routeId)
      intent.putExtra(BusStop.BUS_STOP_ID_KEY, busStopId)
      context.startActivity(intent)
    }
  }

  private[this] val histories = HistoryPreference.get.take(MAX_MENU_HISTORY_COUNT)
  private[this] val historyColors = Iterator(R.drawable.flat_panel_black, R.drawable.flat_panel_red, R.drawable.flat_panel_green, R.drawable.flat_panel_blue, R.drawable.flat_panel_white, R.drawable.flat_panel_black)

  private[this] def toMenuItem(history:History):MenuItem = {
    MenuItem(s"${history.routeName}\n${history.busStopName}",
      historyColors.next(),
      onHistoryClickListener(history.routeId, history.busStopId)
    )
  }

  /**
   * メニュー画面のアイテム
   */
  final val menuItems:Seq[MenuItem] = {
    val fixedMenuTitle = Seq("路線から\n探す", "バス停から\n探す", "履歴から\n探す")
    val fixedMenuColor = Seq(R.drawable.flat_panel_green, R.drawable.flat_panel_blue, R.drawable.flat_panel_white)
    val fixedMenuOnClickListener = Seq(onMenuRouteClickListener, onMenuBusStopClickListener, onMenuHistoryClickListener)
    for(i <- 0 until fixedMenuTitle.length) yield MenuItem(fixedMenuTitle(i), fixedMenuColor(i), fixedMenuOnClickListener(i))
  }++:histories.map(toMenuItem)

  final val count = menuItems.length

  def get(position:Int) = menuItems(position)

}

case class MenuItem(name:String, colorId:Int, onClick:OnClickListener)
