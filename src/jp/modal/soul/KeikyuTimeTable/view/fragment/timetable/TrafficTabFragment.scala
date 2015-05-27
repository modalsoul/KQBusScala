package jp.modal.soul.KeikyuTimeTable.view.fragment.timetable

import android.app.Fragment
import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{TextView, Button}
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
  private[this] var update:Button = null
  private[this] var message:TextView = null

  val onUpdateClickListener = new OnClickListener {
    override def onClick(v: View): Unit = {
      context.showLoadingSpinner
      getLoaderManager.restartLoader(0, null, TrafficTabFragment.this)
    }
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
    buttonById(view, R.id.update).foreach(_.setOnClickListener(onUpdateClickListener))
    route = context.route
    busStop = context.busStop
    if(route != null && busStop != null) setupView(route, busStop)
    else context.setOnRouteBusStopLoadFinished(setupView)
    view
  }

  def setupView(route:Route, busStop:BusStop) {
    this.route = route
    this.busStop = busStop
    getLoaderManager.initLoader(0, null, TrafficTabFragment.this)
  }

  override def onCreateLoader(id: Int, args: Bundle): Loader[Seq[TrafficInfo]] = {
    val loader = TrafficInfoLoader(context, route, busStop)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(loader: Loader[Seq[TrafficInfo]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[TrafficInfo]], data: Seq[TrafficInfo]): Unit = {
    val threeArrive = textById(context, R.id.three_before_arrive_time)
    val threeTerminal = textById(context, R.id.three_before_terminal_time)
    val twoArrive = textById(context, R.id.two_before_arrive_time)
    val twoTerminal = textById(context, R.id.two_before_terminal_time)
    val oneArrive = textById(context, R.id.one_before_arrive_time)
    val oneTerminal = textById(context, R.id.one_before_terminal_time)
    val justArrive = textById(context, R.id.ride_bus_stop_arrive_time)
    val justTerminal = textById(context, R.id.ride_bus_stop_terminal_time)

    textById(context, R.id.ride_bus_stop_name).foreach(_.setText(busStop.name))

    val willArriveAt = (s:String) => getString(R.string.will_arrive_at, s)
    data.foreach{
      case TrafficInfo(TrafficInfo.THREE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        threeArrive.foreach(_.setText(willArriveAt(ride)))
        threeTerminal.foreach(_.setText(willArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.TWO_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        twoArrive.foreach(_.setText(willArriveAt(ride)))
        twoTerminal.foreach(_.setText(willArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.ONE_BEFORE_INDEX, Some(ride), Some(arrive)) =>
        oneArrive.foreach(_.setText(willArriveAt(ride)))
        oneTerminal.foreach(_.setText(willArriveAt(arrive)))
      case TrafficInfo(TrafficInfo.JUST_THIS, Some(ride), Some(arrive)) =>
        justArrive.foreach(_.setText(willArriveAt(ride)))
        justTerminal.foreach(_.setText(willArriveAt(arrive)))
      case _ => Log.e(TAG, "NO MATCH")
    }
    context.hideLoadingSpinner

    if(data.isEmpty) message.setText(R.string.there_is_no_bus)
  }
}
