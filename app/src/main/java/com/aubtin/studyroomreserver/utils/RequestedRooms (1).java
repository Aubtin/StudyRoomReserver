package com.aubtin.studyroomreserver.utils;

import java.util.List;

/**
 * Created by Aubtin on 1/30/2017.
 */

public class RequestedRooms
{
    private String key;
    private int day;
    private int startTime;
    private int endTime;
    private int roomNumber;
    private String reservedByID;
    private String reservedByName;
    private String assignedUserID;
    private String assignedUserName;

    public RequestedRooms() {}

    public RequestedRooms(String key, int day, int startTime, int endTime, int roomNumber, String reservedByID, String reservedByName, String assignedUserID, String assignedUserName)
    {
        this.key = key;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumber = roomNumber;
        this.reservedByID = reservedByID;
        this.reservedByName = reservedByName;
        this.assignedUserID = assignedUserID;
        this.assignedUserName = assignedUserName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getReservedByID() {
        return reservedByID;
    }

    public void setReservedByID(String reservedByID) {
        this.reservedByID = reservedByID;
    }

    public String getReservedByName() {
        return reservedByName;
    }

    public void setReservedByName(String reservedByName) {
        this.reservedByName = reservedByName;
    }

    public String getAssignedUserID() {
        return assignedUserID;
    }

    public void setAssignedUserID(String assignedUserID) {
        this.assignedUserID = assignedUserID;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getReadableTime(int timeValue)
    {
        final String[] TIMES_READABLE = {"8:00am", "8:30am", "9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am",
                "12:00pm", "12:30pm", "1:00pm", "1:30pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm", "4:30pm", "5:00pm", "5:30pm",
                "6:00pm", "6:30pm", "7:00pm", "7:30pm", "8:00pm", "8:30pm", "9:00pm"};

        return TIMES_READABLE[timeValue];
    }

    public String getReadableDay(int dayValue)
    {
        final String[] DAY_READABLE = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        return DAY_READABLE[dayValue];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestedRooms that = (RequestedRooms) o;

        if (getDay() != that.getDay()) return false;
        if (getStartTime() != that.getStartTime()) return false;
        if (getEndTime() != that.getEndTime()) return false;
        if (getRoomNumber() != that.getRoomNumber()) return false;
        if (!getKey().equals(that.getKey())) return false;
        if (!getReservedByID().equals(that.getReservedByID())) return false;
        if (!getReservedByName().equals(that.getReservedByName())) return false;
        if (!getAssignedUserID().equals(that.getAssignedUserID())) return false;
        return getAssignedUserName().equals(that.getAssignedUserName());

    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + getDay();
        result = 31 * result + getStartTime();
        result = 31 * result + getEndTime();
        result = 31 * result + getRoomNumber();
        result = 31 * result + getReservedByID().hashCode();
        result = 31 * result + getReservedByName().hashCode();
        result = 31 * result + getAssignedUserID().hashCode();
        result = 31 * result + getAssignedUserName().hashCode();
        return result;
    }

    public String getReadableRoom(int roomValue)
    {
        final String[] ROOM_READABLE = {"200H", "200I", "200J", "200G", "100E"};

        return ROOM_READABLE[roomValue];
    }
}
