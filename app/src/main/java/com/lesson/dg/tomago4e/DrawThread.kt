package com.lesson.dg.tomago4e

import android.graphics.Canvas
import android.view.SurfaceHolder

class DrawThread(surfaceHolder: SurfaceHolder, panel: Ground): Thread() {
    private var surfaceHolder :SurfaceHolder
    private var panel : Ground
    private var run = false

    init {
        this.surfaceHolder = surfaceHolder
        this.panel = panel
    }

    fun setRunning(run : Boolean){
        this.run = run
    }

    override fun run() {
        var c: Canvas ?= null
        while(run) {
            c = null
            try {
                c = surfaceHolder!!.lockCanvas(null)
                synchronized(surfaceHolder!!){
                    if (panel != null) {
                        panel!!.draw(c)
                    }
                }
            } finally {
                if (c!= null){
                    surfaceHolder!!.unlockCanvasAndPost(c)
                }
            }
        }
    }

}