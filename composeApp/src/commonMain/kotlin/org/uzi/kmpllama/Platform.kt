package org.uzi.kmpllama

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform