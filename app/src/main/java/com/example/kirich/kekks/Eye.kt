package com.example.kirich.kekks

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class Eye {
    var x: Float = 0f
    var y: Float = 0f
    val radius: Float = 15f
    val minRadius = 7f
    val rotateRadius = 5f
    //var mass: Float = 10f //?
    var closed: Boolean? = null

    var direction: FloatArray? = null

    constructor(x_:Float, y_:Float) {
        x = x_
        y = y_
        closed = false
        direction = arrayOf(0f,0f).toFloatArray()
    }

    fun draw(c: Canvas, cx: Float, cy: Float) {
        val p = Paint()
        if (closed == false) { //?
            p.color = Color.WHITE
            c.drawOval(cx + x - radius, cy + y - radius, cx + x + radius, cy + y + radius, p)

            p.color = Color.BLACK
            var xr = direction!![0]
            var yr = direction!![1]
            val len = kotlin.math.sqrt(xr*xr+yr*yr)
            xr /= len
            yr /= -len
            c.drawOval(cx + x - minRadius + xr * rotateRadius,
                cy + y - minRadius + yr * rotateRadius,
                cx + x + minRadius + xr * rotateRadius,
                cy + y + minRadius + yr * rotateRadius, p)
        }
        else {
            p.color = Color.BLACK
            c.drawOval(cx + x - radius, cy + y - radius, cx + x + radius, cy + y + radius, p)
        }
    }

    fun close() { closed = true }

    fun open() { closed = false }

    fun setMagnetVec(vec : FloatArray) { direction = arrayOf(vec[0],vec[1]).toFloatArray() }
}