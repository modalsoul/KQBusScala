package jp.modal.soul.KeikyuTimeTable.worker

import android.content.{AsyncTaskLoader, Context}
import jp.modal.soul.KeikyuTimeTable.model.entity.{RouteDao, Route}

/**
 * Created by imae on 2015/05/15.
 */
case class RouteLoader(context:Context) extends AsyncTaskLoader[Seq[Route]](context) {

  override def loadInBackground(): Seq[Route] = {
    RouteDao(context).routesOrderById
  }
}
