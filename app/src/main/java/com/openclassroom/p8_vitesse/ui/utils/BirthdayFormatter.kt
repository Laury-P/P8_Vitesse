package com.openclassroom.p8_vitesse.ui.utils

import java.time.LocalDate
import java.util.Locale

object BirthdayFormatter {
    fun format (date : LocalDate) : String {
        val local = Locale.getDefault()
        val pattern = when (local.language) {
            Locale.FRENCH.language -> "dd/MM/yyyy"
            else -> "MM/dd/yyyy"
        }
        val birthday = date.format(java.time.format.DateTimeFormatter.ofPattern(pattern))
        return birthday
    }
}