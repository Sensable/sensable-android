package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by madine on 01/07/14.
 */
public class Sensable implements Parcelable {
    private double[] location;
    private String sensorid;
    private Sample[] samples;
    private String unit;

    public Sensable() {
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getSensorid() {
        return sensorid;
    }

    public void setSensorid(String sensorId) {
        this.sensorid = sensorId;
    }

    public Sample[] getSamples() {
        return samples;
    }

    public void setSamples(Sample[] samples) {
        this.samples = samples;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        if(this.getSamples().length > 0) {
            return this.getSensorid() + " - " + this.getSamples()[0].getValue() + this.getUnit();
        } else {
            return this.getSensorid() + " - " + this.getUnit();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDoubleArray(location);
        dest.writeString(sensorid);
        dest.writeParcelableArray(samples, flags);
        dest.writeString(unit);
    }

    public static final Parcelable.Creator<Sensable> CREATOR = new Parcelable.Creator<Sensable>() {
        public Sensable createFromParcel(Parcel in) {
            return new Sensable(in);
        }

        public Sensable[] newArray(int size) {
            return new Sensable[size];
        }
    };

    private Sensable(Parcel in) {
        location = in.createDoubleArray();
        sensorid = in.readString();

        Parcelable[] parcelableArray = in.readParcelableArray(Sample.class.getClassLoader());
        samples = null;
        if (parcelableArray != null) {
            samples = Arrays.copyOf(parcelableArray, parcelableArray.length, Sample[].class);
        }

        unit = in.readString();
    }


}
