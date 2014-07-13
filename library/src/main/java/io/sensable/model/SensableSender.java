package io.sensable.model;

/**
 * Created by madine on 01/07/14.
 */
public class SensableSender {
    private Sensable sensable; //Will be type 'Sensable'
    private int interval;

    public Sensable getSensable() {
        return sensable;
    }

    public void setSensable(Sensable sensable) {
        this.sensable = sensable;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
