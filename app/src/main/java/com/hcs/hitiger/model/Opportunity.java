package com.hcs.hitiger.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hcs.hitiger.api.model.user.Sport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by anuj gupta on 5/10/16.
 */
public class Opportunity implements Parcelable {
    private Calendar date;
    private Calendar time;
    private boolean isAllDay;
    private boolean isFree;
    private boolean isToday;
    private boolean isTommorow;
    private String amount;
    private List<String> listOfAdditionalInformation;
    private AddressData mAddressData;
    private Sport sport;

    public Opportunity() {
        // intiliaze default values like time ,date required ones
        listOfAdditionalInformation = new ArrayList<>();
    }

    protected Opportunity(Parcel in) {
        this.date = (Calendar) in.readSerializable();
        this.time = (Calendar) in.readSerializable();
        isAllDay = in.readByte() != 0;
        isFree = in.readByte() != 0;
        isToday = in.readByte() != 0;
        isTommorow = in.readByte() != 0;
        amount = in.readString();
        listOfAdditionalInformation = in.createStringArrayList();
        mAddressData = in.readParcelable(AddressData.class.getClassLoader());
        sport = in.readParcelable(Sport.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.date);
        dest.writeSerializable(this.time);
        dest.writeByte((byte) (isAllDay ? 1 : 0));
        dest.writeByte((byte) (isFree ? 1 : 0));
        dest.writeByte((byte) (isToday ? 1 : 0));
        dest.writeByte((byte) (isTommorow ? 1 : 0));
        dest.writeString(amount);
        dest.writeStringList(listOfAdditionalInformation);
        dest.writeParcelable(mAddressData, flags);
        dest.writeParcelable(sport, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Opportunity> CREATOR = new Creator<Opportunity>() {
        @Override
        public Opportunity createFromParcel(Parcel in) {
            return new Opportunity(in);
        }

        @Override
        public Opportunity[] newArray(int size) {
            return new Opportunity[size];
        }
    };

    public void setAddressData(AddressData addressData) {
        mAddressData = addressData;
    }

    public Opportunity(Calendar date, Calendar time, boolean isAllDay, boolean isFree, boolean isToday, boolean isTommorow, String amount, AddressData address, List<String> listOfAdditionalInformation) {
        this.date = date;
        this.time = time;
        this.isAllDay = isAllDay;
        this.isFree = isFree;
        this.isToday = isToday;
        this.isTommorow = isTommorow;
        this.amount = amount;
        this.mAddressData = address;
        this.listOfAdditionalInformation = listOfAdditionalInformation;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        if (today) {
            isTommorow = false;
            this.date = setDateToZero(Calendar.getInstance());
        }
        isToday = today;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public boolean isTommorow() {
        return isTommorow;
    }

    public void setTommorow(boolean tommorow) {
        if (tommorow) {
            isToday = false;
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            this.date = setDateToZero(calendar);
        }
        isTommorow = tommorow;
    }

    public List<String> getListOfAdditionalInformation() {
        return listOfAdditionalInformation;
    }

    public void setListOfAdditionalInformation(List<String> listOfAdditionalInformation) {
        this.listOfAdditionalInformation = listOfAdditionalInformation;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
//        isToday = false;
//        isTommorow = false;
        this.date = setDateToZero(date);
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        if (time == null) {
            this.time = time;
        } else {
            this.time = getTimeTillSelectedDate(time);
        }
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public AddressData getAddressData() {
        return mAddressData;
    }

    public void setAddress(AddressData address) {
        this.mAddressData = address;
    }

    Calendar setDateToZero(Calendar calendar) {
        if (calendar != null) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar;
        } else {
            return null;
        }
    }

    private Calendar getTimeTillSelectedDate(Calendar time) {
        long timeInMills = time.getTimeInMillis();
        Calendar calendar = setDateToZero(time);
        long difference = timeInMills - calendar.getTimeInMillis();
        calendar.setTimeInMillis(this.date.getTimeInMillis() + difference);
        return calendar;
    }
}
