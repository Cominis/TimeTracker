package com.dmt.timetracker.utils

import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalAdjusters
import java.util.*

private val DEFAULT_ZONE_ID = ZoneId.systemDefault()

fun startOfDay(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MIN)

fun endOfDay(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MAX)

fun belongsToCurrentDay(localDateTime: LocalDateTime): Boolean =
    localDateTime.isAfter(startOfDay()) && localDateTime.isBefore(
        endOfDay()
    )



//note that week starts with Monday
fun startOfWeek(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MIN)
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

//note that week ends with Sunday
fun endOfWeek(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MAX)
        .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

fun belongsToCurrentWeek(localDateTime: LocalDateTime): Boolean =
    localDateTime.isAfter(startOfWeek()) && localDateTime.isBefore(
        endOfWeek()
    )



fun startOfMonth(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MIN)
        .with(TemporalAdjusters.firstDayOfMonth())

fun endOfMonth(): LocalDateTime=
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MAX)
        .with(TemporalAdjusters.lastDayOfMonth())

fun belongsToCurrentMonth(localDateTime: LocalDateTime): Boolean =
    localDateTime.isAfter(startOfMonth()) && localDateTime.isBefore(
        endOfMonth()
    )



fun startOfYear(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MIN)
        .with(TemporalAdjusters.firstDayOfYear())

fun endOfYear(): LocalDateTime =
    LocalDateTime.now(DEFAULT_ZONE_ID).with(LocalTime.MAX)
        .with(TemporalAdjusters.lastDayOfYear())

fun belongsToCurrentYear(localDateTime: LocalDateTime): Boolean =
    localDateTime.isAfter(startOfYear()) && localDateTime.isBefore(
        endOfYear()
    )




fun toMillis(localDateTime: LocalDateTime): Long =
    localDateTime.atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli()

fun toDate(localDateTime: LocalDateTime): Date =
    Date.from(localDateTime.atZone(DEFAULT_ZONE_ID).toInstant())

fun toDateTime(localDate: LocalDate, localTime: LocalTime) : LocalDateTime =
    LocalDateTime.of(localDate, localTime)

fun fromDateAndTimeToMillis(localDate: LocalDate, localTime: LocalTime) : Long =
    LocalDateTime.of(localDate, localTime).atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli()




fun dateTimeToString(dateTime: TemporalAccessor = Instant.now(), pattern: String = "yyyy-MM-dd HH:mm:ss") : String =
    DateTimeFormatter.ofPattern(pattern).withZone(DEFAULT_ZONE_ID).format(dateTime)

fun millisToString(timestamp: Long, pattern: String = "yyyy-MM-dd HH:mm:ss") : String =
    DateTimeFormatter.ofPattern(pattern).withZone(DEFAULT_ZONE_ID).format(Instant.ofEpochMilli(timestamp))