package com.lesson.dg.tomago4e

import android.app.PictureInPictureParams
import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Rational
import android.view.MotionEvent
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager : SensorManager
    private lateinit var accelerometer : Sensor
    private lateinit var compass : Sensor
    private lateinit var light : Sensor
    private lateinit var ground : Ground
    var maxValue = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        compass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        maxValue = light.getMaximumRange();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            //View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            //View.SYSTEM_UI_FLAG_FULLSCREEN
            //View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        ground = Ground(this)
        viewT4e.addView(ground)
        //setContentView(ground)
        //viewT4e = ground
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
     }

    fun doPip(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val rational = Rational(
                    300,
                    300
                )

                val mParams = PictureInPictureParams.Builder()
                    .setAspectRatio(rational)
                    .build()

                enterPictureInPictureMode(mParams)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            pip.hide()
            ground.windowWidth = 470
            ground.windowHeight = 470

            ground.scene.bodies[ground.scene.bodies.size-1].position.x = 150F
            ground.scene.bodies[ground.scene.bodies.size-1].position.y = 150F

        } else {
            pip.show()
            ground.windowWidth = 1080
            ground.windowHeight = 1080
        }
        ground.updateBox()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onStop() {
        super.onStop()
        ground.setRunning(false)
    }
    var currentDegree = 0f

    private var mLastAccelerometerSet = false
    private var mLastMagnetometerSet = false
    private val mR = FloatArray(9)
    private val mOrientation = FloatArray(3)
    private val mLastAccelerometer = FloatArray(3)
    private val mLastMagnetometer = FloatArray(3)

    var isTouch = false
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.getType() ==
            Sensor.TYPE_ACCELEROMETER) {
            ground!!.updateMe(event.values[1] , event.values[0])

            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.size);
            mLastAccelerometerSet = true
        }
        if (event != null && event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            val degree = Math.round(event.values[0])

            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.size)
            mLastMagnetometerSet = true
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer)
            SensorManager.getOrientation(mR, mOrientation)
            val azimuthInRadians = mOrientation[0]
            val azimuthInDegress = (Math.toDegrees(azimuthInRadians.toDouble()) + 360).toFloat() % 360

            currentDegree = -azimuthInDegress
            if (!isTouch) {
                ground!!.updateCompass(currentDegree)
            }
        }

        if (event != null && event.sensor.getType() == Sensor.TYPE_LIGHT) {
            ground!!.updateLight(event.values[0])
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val x = event.x
            val y = event.y
            ground!!.updateTouch(x, y)
            isTouch = true
        }
        when (event!!.action) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isTouch = false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        sensorManager!!.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager!!.registerListener(this, compass, SensorManager.SENSOR_DELAY_GAME)
        sensorManager!!.registerListener(this, light, SensorManager.SENSOR_DELAY_GAME)

        ground.createThread()
        if (ground.thread != null) {
            ground.thread!!.setRunning(true)
        }
        //ground.setRunning(true)
    }

    override fun onPause() {
        super.onPause()
        if (ground.thread != null) {
            ground.thread!!.setRunning(true)
        }
    }

}
