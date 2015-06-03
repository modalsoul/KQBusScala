package jp.modal.soul.KeikyuTimeTable.view.fragment.timetable

import java.util.Date

import android.app.Fragment
import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{LinearLayout, TextView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, Route, TrafficInfo}
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity
import jp.modal.soul.KeikyuTimeTable.worker.TrafficInfoLoader

/**
 * Created by imae on 2015/05/24.
 */
class TrafficTabFragment extends Fragment with LogTag with LoaderCallbacks[Seq[TrafficInfo]] {
  implicit var context:TimeTableActivity = null
  private[this] var view:View = null
  private[this] var route:Route = null
  private[this] var busStop:BusStop = null
  private[this] var message:TextView = null
  private[this] var busStopName:Option[TextView] = None

  private[this] var three:Option[LinearLayout] = None
  private[this] var two:Option[LinearLayout] = None
  private[this] var one:Option[LinearLayout] = None
  private[this] var just:Option[LinearLayout] = None

  private[this] var lastUpdateAt:Option[TextView] = None
  private[this] var updatedTime:Date = null

  val onUpdateClickListener = new OnClickListener {
    override def onClick(v: View): Unit = {
      context.showLoadingSpinner
      reload
    }
  }

  def reload = {
    getLoaderManager.restartLoader(0, null, TrafficTabFragment.this)
    updatedTime = new Date(System.currentTimeMillis)
  }

  override def onCreate(savedInstanceState:Bundle): Unit = {
    super.onCreate(savedInstanceState)
    context = getActivity.asInstanceOf[TimeTableActivity]
  }

  override def onCreateView(inflater:LayoutInflater, container:ViewGroup, savedInstanceState:Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.traffic_tab_fragment, container, false)
    (R.id.three_before_title::R.id.two_before_title::R.id.one_before_title::Nil).foreach(textById(view, _))
    message = textById(view, R.id.message).map{t => t.setText(R.string.there_is_no_bus); t}.get
    lastUpdateAt = textById(view, R.id.last_update_time)
    buttonById(view, R.id.update).foreach(_.setOnClickListener(onUpdateClickListener))
    route = context.route
    busStop = context.busStop
    if(route != null && busStop != null) setupView(route, busStop)
    else context.setOnRouteBusStopLoadFinished(setupView)
    view
  }

  override def onStart(): Unit = {
    super.onStart()
    three = findAViewById(context, R.id.three_before_time)
    two = findAViewById(context, R.id.two_before_time)
    one = findAViewById(context, R.id.one_before_time)
    just = findAViewById(context, R.id.ride_bus_stop_time)
  }

  def setupView(route:Route, busStop:BusStop) {
    this.route = route
    this.busStop = busStop
    getLoaderManager.initLoader(0, null, TrafficTabFragment.this)
    updatedTime = new Date(System.currentTimeMillis)
  }

  override def onCreateLoader(id: Int, args: Bundle): Loader[Seq[TrafficInfo]] = {
    val loader = TrafficInfoLoader(context, route, busStop)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(loader: Loader[Seq[TrafficInfo]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[TrafficInfo]], data: Seq[TrafficInfo]): Unit = {
    busStopName = textById(context, R.id.ride_bus_stop_name)
    busStopName.foreach(_.setText(busStop.name))

    (three::two::one::just::Nil).foreach(_.foreach(_.removeAllViews()))

    data.foreach{
      case TrafficInfo(TrafficInfo.THREE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        addTraffic(three, ride, arrive)
      case TrafficInfo(TrafficInfo.TWO_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        addTraffic(two, ride, arrive)
      case TrafficInfo(TrafficInfo.ONE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        addTraffic(one, ride, arrive)
      case TrafficInfo(TrafficInfo.JUST_THIS, _, Some(terminal)) =>
        addTraffic(just, "", terminal)
      case _ => Log.e(TAG, "NO MATCH")
    }

    message.setVisibility(if(data.isEmpty) View.VISIBLE else View.GONE)

    lastUpdateAt.foreach(_.setText(getString(R.string.last_update_time, DateFormat.format("H：mm", updatedTime))))

    context.hideLoadingSpinner
  }

  private[this] val willArriveAt = (s:String) => getString(R.string.will_arrive_at, s)
  private[this] val terminalArriveAt = (s:String) => getString(R.string.will_terminal_at, s)

  private[this] def addTraffic(view:Option[LinearLayout], ride:String, arrive:String): Unit ={
    val inflater = context.getLayoutInflater
    val row = inflater.inflate(R.layout.traffic_tab_row, null)
    if(ride != "") textById(row, R.id.arrive_time).foreach(_.setText(willArriveAt(ride)))
    textById(row, R.id.terminal_time).foreach(_.setText(terminalArriveAt(arrive)))
    view.foreach(_.addView(row))
  }
}
