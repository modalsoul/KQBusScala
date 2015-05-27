package jp.modal.soul.KeikyuTimeTable.model.entity

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * Created by imae on 2015/05/24.
 */
case class TrafficInfo(index:Int, ride:Option[String], arrive:Option[String])
object TrafficInfo {
  final val THREE_BEFORE_INDEX:Int = 0
  final val TWO_BEFORE_INDEX:Int = 1
  final val ONE_BEFORE_INDEX:Int = 2
  final val JUST_THIS:Int = 3

  private[this] final val TIME = "[0-9]+:[0-9]+".r

  def apply(index:Int, text:String):Option[TrafficInfo] = {
    index match {
      case JUST_THIS =>
        Option(TrafficInfo(index, None, TIME.findFirstIn(text)))
      case _ =>
        val times = TIME.findAllMatchIn(text)
        Option(TrafficInfo(index, Option(times.next().toString()), Option(times.next().toString())))
    }
  }
}

object TrafficParser {
  def getInfo(src:String) = {
    import collection.JavaConversions._
    val doc = Jsoup.parse(src)
    val dds:Iterator[(Element, Int)] = doc.getElementsByTag("dd").iterator().zipWithIndex

    dds.map {
      case (dd, index) =>
        (dd.getElementsByAttributeValue("class", "bus"), index)
    }.filter{
      case (td, index) => !td.isEmpty
    }.flatMap {
      case (td, index) => TrafficInfo(index, td.text())
    }.toSeq

  }
}
