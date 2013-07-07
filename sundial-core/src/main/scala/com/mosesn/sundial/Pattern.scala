package com.mosesn.sundial

import com.twitter.util.{Duration, Time}
import com.twitter.util.TimeConversions.intToTimeableNumber

trait Periodic { self: Pattern =>
  abstract override def period: Duration
}

trait FiveMinutely { self: Pattern =>
  override def period: Duration = 5.minutes
}

trait Starting { self: Pattern =>
  abstract override def start: Time
}

trait StartingNow { self: Pattern =>
  override def start: Time = Time.now
}

trait Ending { self: Pattern =>
  abstract override def end: Time
}

trait EndingTomorrow { self: Pattern =>
  override def end: Time = Time.now + 1.days
}

trait Pattern {
  def period: Duration = 1.minutes
  def start: Time
  def end: Time
}

class PatternImpl extends Pattern with FiveMinutely with StartingNow with EndingTomorrow
