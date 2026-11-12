package com.bookie.ios

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform