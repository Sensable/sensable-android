package io.sensable.model;

/**
 * Created by madine on 01/07/14.
 */
public class SensableSender {
    private int id;
    private String sensorid;
    private int internalSensorId;
    private String sensortype;
    private String unit;
    private int pending;
    private Sample sample;
    private boolean privateSensor;
    private String accessToken;

    // Remove when location is part of sample
    private double[] location;

    public SensableSender() {
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

    public int getInternalSensorId() {
        return internalSensorId;
    }

    public void setInternalSensorId(int sensorId) {
        this.internalSensorId = sensorId;
    }

    public String getSensortype() {
        return sensortype;
    }

    public void setSensortype(String sensortype) {
        this.sensortype = sensortype;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public boolean isPrivateSensor() {
        return privateSensor;
    }

    public void setPrivateSensor(boolean privateSensor) {
        this.privateSensor = privateSensor;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

//    public double[] getLocation() {
//        if (this.sample == null) {
//            this.sample = new Sample();
//        }
//        return this.sample.getLocation();
//    }
//
//    public void setLocation(double[] location) {
//        if (this.sample == null) {
//            this.sample = new Sample();
//        }
//        this.sample.setLocation(location);
//
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
}