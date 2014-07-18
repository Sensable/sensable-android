package io.sensable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madine on 01/07/14.
 */
public class Sample implements Parcelable {
    private long timestamp;
    private double value;
//    private double[] location;

    public Sample() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(timestamp);
        dest.writeDouble(value);
    }

    public static final Parcelable.Creator<Sample> CREATOR
            = new Parcelable.Creator<Sample>() {
        public Sample createFromParcel(Parcel in) {
            return new Sample(in);
        }

        public Sample[] newArray(int size) {
            return new Sample[size];
        }
    };

    private Sample(Parcel in) {
        timestamp = in.readLong();
        value = in.readDouble();
    }

    @Override
    public String toString() {
        return this.getTimestamp() + ": " + this.getValue();
    }

//    public double[] getLocation() {
//        return location;
//    }

//    public void setLocation(double[] location) {
//        this.location = location;
//    }
}