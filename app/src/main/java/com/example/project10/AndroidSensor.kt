package com.example.project10

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Abstracted sensor class
 */

abstract class AndroidSensor(
    private val context: Context,
    private val sensorFeature: String,
    sensorType: Int
) : MeasurableSensor(sensorType), SensorEventListener {
    /**
     * The androidSensor class utilized the context and sensorFeature and sensor type in order to
     * set default sensors, set the sensor manageretc.
     * @param context: Context
     * @param sensorFeature: String
     * @param sensorType: Int
     */

    override val doesSensorExist: Boolean
        get() = context.packageManager.hasSystemFeature(sensorFeature)

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null

    override fun startListening() {
        // so long as the sensor exists, create it - otherwise it doesn't exist and just return empty
        if (!doesSensorExist) {
            return
        }
        if (!::sensorManager.isInitialized && sensor == null) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(sensorType)
        }
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun stopListening() {
        // if the sensor exists, keep listening - otherwise end
        if (!doesSensorExist || !::sensorManager.isInitialized) {
            return
        }
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // takes in a sensor event and if it is a type (not null) then it is invoked
        if (!doesSensorExist) {
            return
        }
        if (event?.sensor?.type == sensorType) {
            onSensorValuesChanged?.invoke(event.values.toList())
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
}