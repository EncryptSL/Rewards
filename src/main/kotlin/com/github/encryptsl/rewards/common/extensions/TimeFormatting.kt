package com.github.encryptsl.rewards.common.extensions

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

fun convertFancyTime(date: Date, pattern: String): String
{
    val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(Locale.getDefault()).withZone(
        ZoneId.systemDefault()
    )
    return formatter.format(date.toInstant())
}