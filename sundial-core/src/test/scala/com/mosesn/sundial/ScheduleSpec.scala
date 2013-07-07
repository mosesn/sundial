package com.mosesn.sundial

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import com.twitter.util.Time
import com.twitter.util.TimeConversions.intToTimeableNumber

class ScheduleSpec extends FunSpec with ShouldMatchers {
  describe("Schedule") {
    it("should allow a single date") {
      val cur = Time.now
      val sched = Schedule(cur)
      sched.time should be (cur)
      sched.next should be (Schedule.Empty)
    }

    it("should allow a few dates") {
      val cur = Time.now
      val sched = Schedule(cur, cur + 5.minutes, cur + 10.minutes)
      sched.time should be (cur)
      sched.next.time should be (cur + 5.minutes)
      sched.next.next.time should be (cur + 10.minutes)
      sched.next.next.next should be (Schedule.Empty)
    }

    it("should validate a good schedule") {
      val cur = Time.now
      val sched = Schedule(cur, cur + 5.minutes, cur + 10.minutes)
      Schedule.validate(sched) should be (true)
    }

    it("should invalidate a good schedule") {
      val cur = Time.now
      val sched = Schedule(cur + 10.minutes, cur + 5.minutes, cur)
      Schedule.validate(sched) should be (false)
    }

    it("should invalidate simultaneous times") {
      val cur = Time.now
      val sched = Schedule(cur, cur)
      Schedule.validate(sched) should be (false)
    }

    it("should merge schedules properly") {
      val cur = Time.now
      val sched1 = Schedule(cur + 5.minutes)
      val sched2 = Schedule(cur, cur + 10.minutes)
      Schedule.validate(sched1) should be (true)
      Schedule.validate(sched2) should be (true)
      val merged = Schedule.merge(sched1, sched2)
      Schedule.validate(merged) should be (true)
      merged.time should be (cur)
      merged.next.time should be (cur + 5.minutes)
      merged.next.next.time should be (cur + 10.minutes)
      merged.next.next.next should be (Schedule.Empty)
    }
  }
}
