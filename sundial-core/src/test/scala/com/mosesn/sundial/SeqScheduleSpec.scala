package com.mosesn.sundial

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import scala.util.Random

import com.twitter.util.Time

class SeqScheduleSpec extends FunSpec with ShouldMatchers {
  val rand = new Random()
  val nowSeconds = Time.now.inSeconds

  describe("SeqSchedule") {
    it("should allow for a single day") {
      val cur = Time.now
      val sched = new SeqSchedule(cur :: Nil)
      sched.time should be (cur)
      sched.next should be (Schedule.Empty)
    }

    it("should fail on empty lists") {
      val e = evaluating {new SeqSchedule(Nil)} should produce [IllegalArgumentException]
      e.getMessage should endWith ("seq cannot be empty, try using Schedule.Empty instead")
    }

    it("should allow for a few times") {
      val numTimes = rand.nextInt(50)
      var sched: Schedule = new SeqSchedule(List.fill(numTimes)(Time.fromSeconds(rand.nextInt(nowSeconds))))
      for (i <- 0 until numTimes) {
        sched.time should be <= Time.fromSeconds(nowSeconds)
        sched.hasNext should be (true)
        sched = sched.next
      }
      sched should be (Schedule.Empty)
    }

    it("should work fine with infinite streams") {
      val cur = Time.now
      val sched: Schedule = new SeqSchedule(Stream.continually(cur))

      // check the regular api isn't greedy
      var iteratingSched = sched
      for (i <- 0 until rand.nextInt(50) + 50) {
        iteratingSched.time should be (cur)
        iteratingSched.hasNext should be (true)
        iteratingSched = iteratingSched.next
      }

      // check the infinite iterator is ok
      val iterator = sched.iterator
      for (i <- 0 until rand.nextInt(50) + 50) {
        iterator.hasNext should be (true)
        iterator.next() should be (cur)
      }

      // check take doesn't force eager evaluation
      for (item <- sched.take(rand.nextInt(50) + 50)) {
        item should be (cur)
      }
    }
  }
}
