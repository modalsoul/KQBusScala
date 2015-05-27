package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.widget.{ListView, Toast}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.BusStop
import jp.modal.soul.KeikyuTimeTable.view.adapter.BusStopAdapter
import jp.modal.soul.KeikyuTimeTable.worker.BusStopByKeywordLoader

/**
 * Created by imae on 2015/05/23.
 */
class SearchBusStopActivity extends BaseActivity with LoaderCallbacks[Seq[BusStop]] {
  private[this] var keywords:Array[String] = Array()
  var listView:ListView = null

  override def onCreate(bundle:Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.search_busstop_activity)
    keywords = intentValue[String](BusStop.BUS_STOP_SEARCH_KEY).split(" ")
    listView = findMyViewById(R.id.busstop_list).get
  }

  override def onStart(): Unit = {
    super.onStart()
    getLoaderManager.initLoader(0, null, SearchBusStopActivity.this)
  }

  override def onCreateLoader(p1: Int, p2: Bundle): Loader[Seq[BusStop]] = {
    val loader = BusStopByKeywordLoader(this, keywords)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(p1: Loader[Seq[BusStop]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[BusStop]], busStops: Seq[BusStop]): Unit = {
      listView.setAdapter(BusStopAdapter(busStops, needRouteName = true))

  }
}
