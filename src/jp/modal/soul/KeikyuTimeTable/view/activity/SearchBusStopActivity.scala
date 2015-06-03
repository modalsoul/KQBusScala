package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.text.{Editable, TextWatcher}
import android.view.View
import android.widget.{EditText, ListView, TextView}
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
  var adapter:BusStopAdapter = null
  var searchBox:EditText = null
  var message:TextView = null

  override def onCreate(bundle:Bundle): Unit = {
    super.onCreate(bundle)
    setContentView(R.layout.search_busstop_activity)
    keywords = intentValue[String](BusStop.BUS_STOP_SEARCH_KEY).split(" ")
    listView = findMyViewById(R.id.busstop_list).get
    searchBox = findMyViewById(R.id.search_bus_stop_keyword).get
    message = textViewById(R.id.search_bus_stop_message).get
  }

  val searchTextWatcher = new TextWatcher {
    override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}

    override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
      adapter.getFilter.filter(s)
    }

    override def afterTextChanged(s: Editable): Unit = {}
  }

  override def onStart(): Unit = {
    super.onStart()
    showLoadingSpinner
    getLoaderManager.initLoader(0, null, SearchBusStopActivity.this)
    searchBox.addTextChangedListener(searchTextWatcher)
  }

  override def onCreateLoader(p1: Int, p2: Bundle): Loader[Seq[BusStop]] = {
    val loader = BusStopByKeywordLoader(this, keywords)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(p1: Loader[Seq[BusStop]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[BusStop]], busStops: Seq[BusStop]): Unit = {
    if(busStops.isEmpty) {
      textViewById(R.id.no_such_bus_stop).foreach(_.setText(R.string.no_such_bus_stop))
      message.setVisibility(View.GONE)
      searchBox.setVisibility(View.GONE)
    }
    else {
      adapter = BusStopAdapter(busStops, needRouteName = true)
      listView.setAdapter(adapter)
    }
    hideLoadingSpinner
  }
}
