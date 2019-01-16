package com.example.kirich.kekks

import android.graphics.Canvas
import android.graphics.Paint

class Tama {

    enum class State {
        Sleeping,
        Blinking,
        Awake
    }

    var x: Float
    var y: Float
    val wakeUpTime = 3000
    val fallAsleepTime = 3000

    private val radius = 50f
    private val blinkTime = 150

    private var eye1: Eye? = null
    private var eye2: Eye? = null
    private var maxX: Float? = null
    private var maxY: Float? = null
    private var eyesClosed: Boolean? = null
    private var state : State? = null
    private var lastEyeChanged : Long? = null

    var v = Pair(0f, 0f)

    constructor(startX: Float, startY: Float, max_x: Float, max_y: Float) {
        y = startY
        x = startX

        eye1 = Eye(-25f, -15f)
        eye2 = Eye(25f, -15f)

        maxX = max_x
        maxY = max_y

        eyesClosed = false
        state = State.Awake
        lastEyeChanged = 0
    }

    fun drawItselfOn(c: Canvas) {
        val p = Paint()
        p.color = -0x35e8
        c.drawOval(x - radius, y - radius, x + radius, y + radius, p)

        if (state == State.Blinking && System.currentTimeMillis() - (lastEyeChanged as Long) > blinkTime) {
            invertEyes()
        }

        if (eyesClosed == true) { //?
            eye1?.close()
            eye2?.close()
        }
        else {
            eye1?.open()
            eye2?.open()
        }
        eye1?.draw(c, x, y)
        eye2?.draw(c, x, y)
    }

    fun applyForce(f: Pair<Float, Float>) {
        v = Pair(v.first + f.first, v.second + f.second)
    }

    fun move() {
        val frictionCoef = 0.99f
        val bounceCoef = 0.8f
        val dt = 0.17f

        v = Pair(v.first * frictionCoef, v.second * frictionCoef)
        if (x + v.first * dt - radius < 0 || x + v.first * dt + radius > (maxX as Float)) {
            v = Pair(-v.first * bounceCoef, v.second)
        }
        else {
            x += v.first * dt
        }

        if (y + v.second * dt - radius < 0 || y + v.second * dt + radius > (maxY as Float)) {
            v = Pair(v.first, -v.second * bounceCoef)
        }
        else {
            y += v.second * dt
        }
    }

    private fun closeEyes() {
        eyesClosed = true
        lastEyeChanged = System.currentTimeMillis()
    }

    private fun openEyes() {
        eyesClosed = false
        lastEyeChanged = System.currentTimeMillis()
    }

    private fun invertEyes() {
        eyesClosed = !(eyesClosed as Boolean)
        lastEyeChanged = System.currentTimeMillis()
    }

    fun setMagnetVec(vec: FloatArray) {
        eye1?.setMagnetVec(vec)
        eye2?.setMagnetVec(vec)
    }

    fun getState() : State { return state as State }

    fun setState(st: State) {
        state = st

        when(state) {
            State.Sleeping -> {closeEyes()}
            State.Awake -> {openEyes()}
        }
    }
}