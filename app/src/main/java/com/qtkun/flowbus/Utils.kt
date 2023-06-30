package com.qtkun.flowbus

import kotlin.random.Random

fun generateRandomString(length: Int): String {
    val chars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..length)
        .map { Random.nextInt(0, chars.size) }
        .map(chars::get)
        .joinToString("")
}