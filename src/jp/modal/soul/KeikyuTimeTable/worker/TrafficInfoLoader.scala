package jp.modal.soul.KeikyuTimeTable.worker

import android.content.{AsyncTaskLoader, Context}
import com.squareup.okhttp.{OkHttpClient, Request}
import jp.modal.soul.KeikyuTimeTable.model.entity._

import scala.util.Try

/**
 * Created by imae on 2015/05/24.
 */
case class TrafficInfoLoader(context:Context, route:Route, busStop:BusStop) extends AsyncTaskLoader[Seq[TrafficInfo]](context) {
  override def loadInBackground(): Seq[TrafficInfo] = {
    BusStopDao(context).busStopById(route.terminalId).fold(Seq.empty[TrafficInfo]){
      terminal =>
        val request = new Request.Builder()
          .url(s"http://keikyu-bus-loca.jp/BusLocWeb/getInpApchInfo.do?usn=${busStop.search}&dsn=${terminal.search}")
          .get()
          .build()
        val client = new OkHttpClient()

        try {
          val res = client.newCall(request).execute().body().string()
          TrafficParser.getInfo(res)
        } catch {
          case e:Exception => Seq.empty[TrafficInfo]
        }

//        Try(client.newCall(request).execute().body().string())
//          .map(TrafficParser.getInfo).getOrElse(Seq.empty[TrafficInfo])
    }
  }
}
