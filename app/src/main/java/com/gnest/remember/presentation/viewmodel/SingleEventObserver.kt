package com.gnest.remember.presentation.viewmodel

import androidx.lifecycle.Observer

open class SingleEventObserver<Content>(private val onChangedFunc: (Content) -> Unit) : Observer<Event<Content>> {

    override fun onChanged(event: Event<Content>) {
        event.getContent()?.let { onChangedFunc(it) }
    }
}