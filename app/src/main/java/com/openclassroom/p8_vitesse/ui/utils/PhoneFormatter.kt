package com.openclassroom.p8_vitesse.ui.utils

object PhoneFormatter {

    fun format(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return digits.chunked(2).joinToString(" ")
    }
}