package com.desarollo.luisvillalobos.gardenkit.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceJ implements Parcelable {
    protected String descripcion;
    protected String apiKey;
    protected String device;
    protected String user;
    protected int fk;


    public DeviceJ() {
    }

    public DeviceJ(String descripcion, String apiKey, String device, String user, int fk) {
        this.descripcion = descripcion;
        this.apiKey = apiKey;
        this.device = device;
        this.user = user;
        this.fk = fk;
    }

    public int getFk() {
        return fk;
    }

    public void setFk(int fk) {
        this.fk = fk;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    protected DeviceJ(Parcel in) {
        descripcion = in.readString();
        apiKey = in.readString();
        device = in.readString();
        user = in.readString();
        fk = in.readInt();
    }

    public static final Creator<DeviceJ> CREATOR = new Creator<DeviceJ>() {
        @Override
        public DeviceJ createFromParcel(Parcel in) {
            return new DeviceJ(in);
        }

        @Override
        public DeviceJ[] newArray(int size) {
            return new DeviceJ[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(descripcion);
        parcel.writeString(apiKey);
        parcel.writeString(device);
        parcel.writeString(user);
        parcel.writeInt(fk);
    }
    /*
    public static final Creator<DeviceJ> CREATOR = new Creator<DeviceJ>() {
        @Override
        public DeviceJ createFromParcel(Parcel in) {
            return new DeviceJ(in);
        }

        @Override
        public DeviceJ[] newArray(int size) {
            return new DeviceJ[size];
        }
    };

    protected DeviceJ(Parcel in) {
        String[] data = new String[4];
        in.readStringArray(data);
        this.descripcion = data[0];
        this.apiKey = data[1];
        this.device = data[2];
        this.user = data[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                this.descripcion,
                this.apiKey,
                this.device,
                this.user,
        });
    }*/
}


