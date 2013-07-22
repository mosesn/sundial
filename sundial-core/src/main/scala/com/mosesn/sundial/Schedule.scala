package com.mosesn.sundial

import scala.collection.immutable.{Stream, SortedSet}

import com.twitter.util.Time

/**
  * Schedules represent monotonically increasing times.
  * @author Moses Nakamura
  */
trait Schedule extends Iterable[Time] {
  def time: Time
  def next: Schedule
  def hasNext: Boolean
  override def iterator: Iterator[Time] = new ScheduleIterator(this)

  private[this] class ScheduleIterator(var schedule: Schedule) extends Iterator[Time] {
    override def next(): Time = {
      if (!hasNext) {
        throw new NoSuchElementException("next on empty iterator")
      }
      val time = schedule.time
      schedule = schedule.next
      time
    }

    override def hasNext: Boolean = schedule != Schedule.Empty
  }

  def eager: Schedule = new SeqSchedule(toSeq)
}

class SeqSchedule(seq: Seq[Time]) extends Schedule {
  require(seq.isEmpty == false, "seq cannot be empty, try using Schedule.Empty instead")

  override def time: Time = seq.head

  // TODO: can we make this type SeqSchedule?
  override def next: Schedule = if (seq.tail.isEmpty) Schedule.Empty else new SeqSchedule(seq.tail)

  override def hasNext: Boolean = true
}

// TODO test merged schedules
// TODO we can have smarter behavior when merging into a merged schedule
// TODO if we know that all of our schedules are finite, we should be able to squash them
// TODO test that this will fail on trivial schedules
class MergedSchedule(schedules: SortedSet[Schedule]) extends Schedule {
  require(!schedules.isEmpty, "schedules")
  require(!schedules.tail.isEmpty, "merged schedules must be nontrivial")
  override def time: Time = schedules.head.time

  // not internally immutable, but this makes the code clearer
  // TODO: can this return MergedSchedule
  override def next: Schedule = {
    var newSchedules = schedules - schedules.head
    if (schedules.head.hasNext) {
      newSchedules = newSchedules + schedules.head.next
    }
    if (newSchedules.tail.isEmpty) newSchedules.head else new MergedSchedule(newSchedules)
  }

  override def hasNext: Boolean = true
}

object Schedule {
  def apply(times: Time*): Schedule = new SeqSchedule(times)

  object Empty extends Schedule {
    override def time: Time = Time.Top  //TODO should this throw an exception?
    override def hasNext: Boolean = false
    override def next: Schedule = throw new Exception("there is no next schedule")
  }

  def validate(schedule: Schedule): Boolean = schedule.foldLeft[Option[Time]](Some(Time.Bottom))({ (acc, cur) =>
    acc match {
      case Some(time) => if (time < cur) Some(cur) else None
      case None       => None
    }
  }).isDefined

  // TODO should test with infinite schedules
  def merge(schedules: Schedule*): Schedule =
    new MergedSchedule(SortedSet(schedules: _*)(Ordering.by[Schedule, Time](_.time)))
}
