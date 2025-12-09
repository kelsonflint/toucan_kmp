package com.kelson.toucan

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform