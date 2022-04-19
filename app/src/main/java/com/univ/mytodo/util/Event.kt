package com.univ.mytodo.util

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */

// Event is used by the view model to tell the activity to launch another activity
// view model also provided the Bundle in the event that is needed for the Activity
/*
* When app is rotated all the livedata are called again and unnecessary steps are performed.
*  SO to cancel that operations this helper class is used.
* */
data class Event<out T>(private val content: T) {

    private var hasBeenHandled = AtomicBoolean(false)

    /**
     * Returns the content and prevents its use again.
     */
    fun getIfNotHandled(): T? = if (hasBeenHandled.getAndSet(true)) null else content

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content
}
