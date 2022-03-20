package com.nxplayr.fsl.util.frascozoomable.zoomable

import android.graphics.Matrix
import com.nxplayr.fsl.zoomable.ZoomableController
import java.util.ArrayList

class MultiZoomableControllerListener : ZoomableController.Listener {

    private val mListeners = ArrayList<ZoomableController.Listener>()

    @Synchronized
    override fun onTransformBegin(transform: Matrix) {
        for (listener in mListeners) {
            listener.onTransformBegin(transform)
        }
    }

    @Synchronized
    override fun onTransformChanged(transform: Matrix) {
        for (listener in mListeners) {
            listener.onTransformChanged(transform)
        }
    }

    @Synchronized
    override fun onTransformEnd(transform: Matrix) {
        for (listener in mListeners) {
            listener.onTransformEnd(transform)
        }
    }

    @Synchronized
    fun addListener(listener: ZoomableController.Listener) {
        mListeners.add(listener)
    }

    @Synchronized
    fun removeListener(listener: ZoomableController.Listener) {
        mListeners.remove(listener)
    }
}