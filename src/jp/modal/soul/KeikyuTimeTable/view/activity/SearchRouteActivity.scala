package jp.modal.soul.KeikyuTimeTable.view.activity

import android.app.LoaderManager.LoaderCallbacks
import android.content.Loader
import android.os.Bundle
import android.text.{Editable, TextWatcher}
import android.widget.{EditText, ListView}
import jp.modal.soul.KeikyuTimeTable.R
import jp.modal.soul.KeikyuTimeTable.model.entity.Route
import jp.modal.soul.KeikyuTimeTable.view.adapter.RouteAdapter
import jp.modal.soul.KeikyuTimeTable.worker.RouteLoader

/**
 * Created by imae on 2015/04/12.
 */
class SearchRouteActivity extends BaseActivity with LoaderCallbacks[Seq[Route]] {
  var listView:ListView = null
  var adapter:RouteAdapter = null
  var searchBox:EditText = null

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.search_route_activity)
    listView = findMyViewById(R.id.route_list).get
    searchBox = findMyViewById(R.id.search_route_keyword).get
    textViewById(R.id.search_route_message)
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
    getLoaderManager.initLoader(0, null, SearchRouteActivity.this)
    searchBox.addTextChangedListener(searchTextWatcher)
  }

  override def onCreateLoader(id: Int, bundle: Bundle): Loader[Seq[Route]] = {
    val loader = RouteLoader(this)
    loader.forceLoad()
    loader
  }

  override def onLoaderReset(loader: Loader[Seq[Route]]): Unit = {}

  override def onLoadFinished(loader: Loader[Seq[Route]], routes: Seq[Route]): Unit = {
    adapter = RouteAdapter(routes)
    listView.setAdapter(adapter)
    hideLoadingSpinner
  }
}
