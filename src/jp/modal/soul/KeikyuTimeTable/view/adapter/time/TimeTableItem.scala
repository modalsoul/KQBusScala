package jp.modal.soul.KeikyuTimeTable.view.adapter.time

import jp.modal.soul.KeikyuTimeTable.model.entity.{Time, BusStop, Route}

/**
 * Created by imae on 2015/05/17.
 */
case class TimeTableItem(route:Route, busStop:BusStop, weekday:Seq[Time], saturday:Seq[Time], holiday:Seq[Time])
