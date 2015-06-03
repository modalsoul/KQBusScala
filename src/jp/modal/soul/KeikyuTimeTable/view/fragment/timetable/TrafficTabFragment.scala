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
import android.widget.TextView
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

  private[this] var threeArrive:Option[TextView] = None
  private[this] var threeTerminal:Option[TextView] = None
  private[this] var twoArrive:Option[TextView] = None
  private[this] var twoTerminal:Option[TextView] = None
  private[this] var oneArrive:Option[TextView] = None
  private[this] var oneTerminal:Option[TextView] = None
  private[this] var justTerminal:Option[TextView] = None

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
    textById(view, R.id.three_before_title)
    textById(view, R.id.two_before_title)
    textById(view, R.id.one_before_title)
    message = textById(view, R.id.message).get
    message.setText(R.string.there_is_no_bus)
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
    threeArrive = textById(context, R.id.three_before_arrive_time)
    threeTerminal = textById(context, R.id.three_before_terminal_time)
    twoArrive = textById(context, R.id.two_before_arrive_time)
    twoTerminal = textById(context, R.id.two_before_terminal_time)
    oneArrive = textById(context, R.id.one_before_arrive_time)
    oneTerminal = textById(context, R.id.one_before_terminal_time)
    justTerminal = textById(context, R.id.ride_bus_stop_terminal_time)
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

    if(busStopName.isEmpty) {
      busStopName = textById(context, R.id.ride_bus_stop_name)
      busStopName.foreach(_.setText(busStop.name))
    }

    threeArrive.foreach(_.setText(""))
    threeTerminal.foreach(_.setText(""))
    twoArrive.foreach(_.setText(""))
    twoTerminal.foreach(_.setText(""))
    oneArrive.foreach(_.setText(""))
    oneTerminal.foreach(_.setText(""))
    justTerminal.foreach(_.setText(""))

    val willArriveAt = (s:String) => getString(R.string.will_arrive_at, s)
    val terminalArriveAt = (s:String) => getString(R.string.will_terminal_at, s)
    data.foreach{
      case TrafficInfo(TrafficInfo.THREE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        threeArrive.foreach(_.setText(willArriveAt(ride)))
        threeTerminal.foreach(_.setText(terminalArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.TWO_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        twoArrive.foreach(_.setText(willArriveAt(ride)))
        twoTerminal.foreach(_.setText(terminalArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.ONE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        oneArrive.foreach(_.setText(willArriveAt(ride)))
        oneTerminal.foreach(_.setText(terminalArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.JUST_THIS, _, Some(arrive)) =>
        justTerminal.foreach(_.setText(terminalArriveAt(arrive)))
      case _ => Log.e(TAG, "NO MATCH")
    }

    message.setVisibility(if(data.isEmpty) View.VISIBLE else View.GONE)

    lastUpdateAt.foreach(_.setText(getString(R.string.last_update_time, DateFormat.format("H：mm", updatedTime))))

    context.hideLoadingSpinner
  }
}
