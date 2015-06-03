package jp.modal.soul.KeikyuTimeTable.model.entity

import jp.modal.soul.KeikyuTimeTable.util.LogTag
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * Created by imae on 2015/05/24.
 */
case class TrafficInfo(index:Int, ride:Option[String], arrive:Option[String])
object TrafficInfo extends LogTag {
  final val THREE_BEFORE_INDEX:Int = 0
  final val TWO_BEFORE_INDEX:Int = 1
  final val ONE_BEFORE_INDEX:Int = 2
  final val JUST_THIS:Int = 3

  private[this] final val TIME = "[0-9]+:[0-9]+".r

  def apply(index:Int, text:String):Option[TrafficInfo] = {
    index match {
      case i if i == JUST_THIS =>
        Option(TrafficInfo(index, None, Option(TIME.findAllMatchIn(text).next().toString())))
      case i if index < 3 =>
        val times = TIME.findAllMatchIn(text)
        Option(TrafficInfo(index, Option(times.next().toString()), Option(times.next().toString())))
      case _ => None
    }
  }
}

object TrafficParser {
  def getInfo(src:String) = {
    import scala.collection.JavaConversions._
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
