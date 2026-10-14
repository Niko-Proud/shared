package com.demoedu.campuscompass

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform