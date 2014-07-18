package io.sensable.model;

/**
 * Created by simonmadine on 18/07/2014.
 */
public class SampleResponse {
    private String message;
    private String sensorid;

    public SampleResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }
}
