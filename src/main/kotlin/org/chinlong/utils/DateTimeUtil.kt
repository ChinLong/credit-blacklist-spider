package org.chinlong.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object DateTimeUtil {

    val `yyyyMMdd` = DateTimeFormatter.ofPattern("yyyyMMdd")
    val `yyyy-MM-dd` = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseDate(
            dateStr: String,
            format: DateTimeFormatter = `yyyy-MM-dd`,
            resolver: ResolverStyle = ResolverStyle.SMART
    ): LocalDate = LocalDate.parse(dateStr, format.withResolverStyle(resolver))

    fun formatDate(
            date: LocalDate,
            format: DateTimeFormatter = `yyyy-MM-dd`
    ): String = format.format(date)

}