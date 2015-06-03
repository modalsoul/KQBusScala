package jp.modal.soul.KeikyuTimeTable.view.fragment.timetable

import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.Button
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.BusStop
import jp.modal.soul.KeikyuTimeTable.util.LogTag
import jp.modal.soul.KeikyuTimeTable.view._
import jp.modal.soul.KeikyuTimeTable.view.activity.TimeTableActivity

/**
 * Created by imae on 2015/05/28.
 */
class MapTabFragment extends Fragment with LogTag {
  implicit var context: TimeTableActivity = null
  private[this] var view: View = null
  private[this] var busStop:BusStop = null
  private[this] var openMap: Option[Button] = None
  private[this] var goToNavi: Option[Button] = None

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    context = getActivity.asInstanceOf[TimeTableActivity]
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    view = inflater.inflate(R.layout.map_tab_fragment, container, false)
    openMap = buttonById(view, R.id.open_map)
    goToNavi = buttonById(view, R.id.go_to_navi)
    view
  }

  override def onStart(): Unit = {
    super.onStart()
    Option(context.busStop).fold(context.setOnLoadFinishedForMap(setOnClick))(setOnClick)
  }

  val setOnClick = (busStop:BusStop) => {
    this.busStop = busStop
    openMap.foreach(_.setOnClickListener(onOpenMapClickListener))
    goToNavi.foreach(_.setOnClickListener(onGoToNaviClickListener))
    context.hideLoadingSpinner
  }

  val onOpenMapClickListener = new OnClickListener {
    override def onClick(v: View): Unit = {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=\"" + busStop.name + "（バス）\"")))
    }
  }

  val onGoToNaviClickListener = new OnClickListener {
    override def onClick(v: View): Unit = {
      val intent = new Intent();
      intent.setAction(Intent.ACTION_VIEW);
      intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
      intent.setData(Uri.parse("http://maps.google.com/maps?daddr=" + busStop.name +"（バス）&dirflg=w"));
      startActivity(intent)
    }
  }
}
