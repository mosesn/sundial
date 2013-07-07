# sundial
a library for schedules

## motivation
I want to have something like smart cronjobs, this is a building block

## api
```
import com.twitter.util.Time
import com.mosesn.sundial.Schedule

val now = Time.now
val point = Schedule(now)
val sometimes = Schedule(now, now + 5.minutes, now + 10.minutes)
val increasing = Schedule.validate(sometimes)
val plusFive = Schedule(Stream.continually(Time.now + 5.minutes): _*)
val merged = Schedule.merge(point, sometimes.next, plusFive)
val mergedNext = if (merged.hasNext) merged.next else merged

val time = mergedNext.time
for (time <- merged.take(5)) {
    println(time)
}
```

## TODO
check out [TODO](TODO.md), or search for TODO or FIXME

## authors
Moses Nakamura - @mosesn
