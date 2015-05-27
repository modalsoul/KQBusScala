package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.{BusStop, Route}
import jp.modal.soul.KeikyuTimeTable.view.adapter.BusStopAdapter
import jp.modal.soul.KeikyuTimeTable.worker.BusStopByRouteLoader

/**
 * Created by imae on 2015/05/16.
 */
class SelectBusStopActivity extends BaseActivity with LoaderCallbacks[Seq[BusStop]] {
  var routeId:Long = 0
  var routeName:String = ""
  var listView:ListView = null
  
  override def onCreate(bundle:Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.select_busstop_activity)
    routeId = intentValue[Long](Route.ROUTE_ID_KEY)
    routeName = intentValue[String](Route.NAME)
    listView = findMyViewById(R.id.busstop_list).get
    setTitle(routeName)
  }

  override def onStart(): Unit = {
    super.onStart()
    showLoadingSpinner
    getLoaderManager.initLoader(0, null, SelectBusStopActivity.this)
  }

  override def onCreateLoader(id: Int, bundle: Bundle): Loader[Seq[BusStop]] = {
    val loader = BusStopByRouteLoader(this, routeId)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(p1: Loader[Seq[BusStop]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[BusStop]], busStops: Seq[BusStop]): Unit = {
    listView.setAdapter(BusStopAdapter(busStops))
    hideLoadingSpinner
  }
}
