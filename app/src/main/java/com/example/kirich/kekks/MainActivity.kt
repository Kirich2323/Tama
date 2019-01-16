package com.example.kirich.kekks

import android.app.PictureInPictureParams
import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Rational
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var mSensorManager : SensorManager ?= null
    private var mAccelerometer : Sensor ?= null
    private var mLightSensor : Sensor ?= null
    private var mMagnetSensor : Sensor ?= null

    private var world : World? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        world = World(imageView)
        Thread(Runnable {
            world?.run()
        }).start()

        pictureInPictureButton.setOnClickListener {
            val ar = Rational(world!!.maxX, world!!.maxY)
            val params = PictureInPictureParams.Builder().setAspectRatio(ar)
            enterPictureInPictureMode(params.build())
        }

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mLightSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT)
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mMagnetSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        if (isInPictureInPictureMode) {
            supportActionBar?.hide()
            pictureInPictureButton.visibility = View.INVISIBLE
        }
        else {
            supportActionBar?.show()
            pictureInPictureButton.visibility = View.VISIBLE
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            when(event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> world?.setGravity(-event.values[0], event.values[1])
                Sensor.TYPE_LIGHT -> world?.setLuminosity(event.values[0], event.timestamp)
                Sensor.TYPE_MAGNETIC_FIELD -> world?.setMagnet(event.values)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mAccelerometer, SensorManager.SENSOR_DELAY_GAME)
        mSensorManager!!.registerListener(this,mLightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        mSensorManager!!.registerListener(this,mMagnetSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        //mSensorManager!!.unregisterListener(this)
    }
}
