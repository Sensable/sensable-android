package io.sensable.client;

import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by madine on 16/07/14.
 */
public class SensorHelper {

    public static String determineUnit(int sensorType) {

        String unit = "";
        switch(sensorType) {
            case Sensor.TYPE_ACCELEROMETER: unit = "m/s^2";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD: unit = "uT";
                break;
            case Sensor.TYPE_GYROSCOPE: unit = "rad/s";
                break;
            case Sensor.TYPE_LIGHT: unit = "lux";
                break;
            case Sensor.TYPE_PRESSURE: unit = "hPa";
                break;
            case Sensor.TYPE_PROXIMITY: unit = "cm";
                break;
            case Sensor.TYPE_GRAVITY: unit = "m/s^2";
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION: unit = "m/s^2";
                break;
            case Sensor.TYPE_ROTATION_VECTOR: unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_ORIENTATION: unit = "<degx, degy, degz>";
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY: unit = "%";
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE: unit = "°C";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED: unit = "uT";
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR: unit = "<θx, θy, θz>";
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED: unit = "rad/s";
                break;
        }

        return unit;
    }

}
