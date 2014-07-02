package io.sensable.model;

/**
 * Created by madine on 01/07/14.
 */
public class SensableSender {
    private String sensable; //Will be type 'Sensable'
    private int interval;


    public String getSensable() {
        return sensable;
    }

    public void setSensable(String sensable) {
        this.sensable = sensable;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
