package com.example.kirich.kekks

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.ImageView

class World {
    val maxX: Int = 900
    val maxY: Int = maxX
    private val tama : Tama = Tama(maxX.toFloat() / 2, maxY.toFloat() / 2, maxX.toFloat(), maxY.toFloat())
    private val luminosityThreshold = 3f
    private val sleepTime : Long = 17

    private var iv: ImageView? = null
    private var gravityVec: Pair<Float, Float> = Pair(0.0f, 9.81f)
    private var luminosity: Float? = null
    private var lastEyesChange: Long? = null

    constructor(iv_: ImageView) {
        iv = iv_
        lastEyesChange = 0
        luminosity = 1000f
    }

    fun setGravity(x: Float, y: Float) {
        gravityVec = Pair(x, y)
    }

    fun setLuminosity(l: Float, time : Long) {
        luminosity = l
    }

    fun setMagnet(vec : FloatArray) {
        tama.setMagnetVec(vec)
    }

    private fun draw() {
        val bm = Bitmap.createBitmap(maxX, maxY, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        drawBackground(c)
        tama.drawItselfOn(c)
        iv?.post { iv?.setImageBitmap(bm) }
    }

    private fun update() {
        val time = System.currentTimeMillis()

        when(tama.getState()) {
            Tama.State.Sleeping -> {
                if (luminosity!!.toFloat() < luminosityThreshold) {
                    lastEyesChange = time
                } else {
                    tama.setState(Tama.State.Blinking)
                }
            }
            Tama.State.Blinking -> {
                if (luminosity!!.toFloat() >= luminosityThreshold) {
                    if (tama.wakeUpTime <= time - lastEyesChange as Long) {
                        tama.setState(Tama.State.Awake)
                    }
                }
                else {
                    tama.setState(Tama.State.Sleeping)
                    lastEyesChange = time
                }
            }
            Tama.State.Awake -> {
                if (luminosity!!.toFloat() > luminosityThreshold) {
                    lastEyesChange = time
                } else if (tama.fallAsleepTime <= time - lastEyesChange as Long) {
                    tama.setState(Tama.State.Sleeping)
                }
            }
        }

        tama.applyForce(Pair(gravityVec.first, gravityVec.second))
        tama.move()
        draw()
    }

    private fun drawBackground(c: Canvas) {
        val p = Paint()
        p.color = Color.BLACK;
        c.drawRect(0f,0f, this.maxX.toFloat(), this.maxY.toFloat(), p)
    }

    fun run() {
        //var cnt = 0
        while (true) {
            update()
            //cnt++
            Thread.sleep(sleepTime)
        }
    }
}
