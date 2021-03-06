package com.nxplayr.fsl.util.frascozoomable.zoomable

import android.os.Build
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import java.util.ArrayList


class MultiGestureListener : GestureDetector.SimpleOnGestureListener() {

    private val mListeners = ArrayList<GestureDetector.SimpleOnGestureListener>()

    /**
     * Adds a listener to the multi gesture listener.
     *
     *
     * NOTE: The order of the listeners is important since gesture events can be consumed.
     *
     * @param listener the listener to be added
     */
    @Synchronized
    fun addListener(listener: GestureDetector.SimpleOnGestureListener) {
        mListeners.add(listener)
    }

    /**
     * Removes the given listener so that it will not be notified about future events.
     *
     *
     * NOTE: The order of the listeners is important since gesture events can be consumed.
     *
     * @param listener the listener to remove
     */
    @Synchronized
    fun removeListener(listener: GestureDetector.SimpleOnGestureListener) {
        mListeners.remove(listener)
    }

    @Synchronized
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onSingleTapUp(e)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onLongPress(e: MotionEvent) {
        val size = mListeners.size
        for (i in 0 until size) {
            mListeners[i].onLongPress(e)
        }
    }

    @Synchronized
    override fun onScroll(
        e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float
    ): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onScroll(e1, e2, distanceX, distanceY)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onFling(e1, e2, velocityX, velocityY)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onShowPress(e: MotionEvent) {
        val size = mListeners.size
        for (i in 0 until size) {
            mListeners[i].onShowPress(e)
        }
    }

    @Synchronized
    override fun onDown(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onDown(e)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onDoubleTap(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onDoubleTap(e)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onDoubleTapEvent(e)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onSingleTapConfirmed(e)) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Synchronized
    override fun onContextClick(e: MotionEvent): Boolean {
        val size = mListeners.size
        for (i in 0 until size) {
            if (mListeners[i].onContextClick(e)) {
                return true
            }
        }
        return false
    }
}