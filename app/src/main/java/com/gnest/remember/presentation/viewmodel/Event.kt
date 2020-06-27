package com.gnest.remember.presentation.viewmodel

class Event<out T>(private val content: T) {

    private var isHandled = false

    fun getContent(): T? = when {
        isHandled -> null
        else -> {
            isHandled = true
            content
        }
    }
}

fun <T : Any> T.asEvent(): Event<T> = Event(this)
