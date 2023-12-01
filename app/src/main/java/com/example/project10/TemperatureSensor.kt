package com.example.project10

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor

/**
 * Sensor classes that extend Android sensors.
 */

class TemperatureSensor(
    context: Context
): AndroidSensor(
    /**
     * Represents a temperature sensor that extends the Android sensor functionality.
     *
     * @param context The context in which the sensor operates.
     */
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE,
    sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE
)

class HumiditySensor(
    context: Context
): AndroidSensor(
    /**
     * Represents a humidity sensor that extends the Android sensor functionality.
     *
     * @param context The context in which the sensor operates.
     */
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY,
    sensorType = Sensor.TYPE_RELATIVE_HUMIDITY
)