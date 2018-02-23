package com.aubtin.studyroomreserver.utils;

import java.util.List;

/**
 * Created by Aubtin on 1/30/2017.
 */

public class ReservedRooms {
    private long startEpochTime;
    private String reservedSlot;

    public ReservedRooms(long startEpochTime, String reservedSlot) {
        this.startEpochTime = startEpochTime;
        this.reservedSlot = reservedSlot;
    }

    public String getRoomName() {
        int startingPoint = reservedSlot.indexOf("Room");

        return reservedSlot.substring(startingPoint + 5, startingPoint + 10);
    }

    public String getStartTime()
    {
        int timeStartPoint = reservedSlot.indexOf(getRoomName()) + 5;
        int timeEndPoint = reservedSlot.indexOf("am") + 2;

        //Find end point
        if((timeEndPoint - timeStartPoint - 2) < 0 || (timeEndPoint - timeStartPoint) > 9)
            timeEndPoint = reservedSlot.indexOf("pm") + 2;

        return reservedSlot.substring(timeStartPoint, timeEndPoint);
    }

    public String getEndTime()
    {
        //Cut the string after finding the room to avoid the first dash.
        String cutString = reservedSlot.substring(reservedSlot.indexOf(getRoomName()) + 5);

        //Find the second dash
        int timeStartPoint = cutString.indexOf("-") + 1;
        //Modify the modified string to start from the beginning of timeStartPoint
        cutString = cutString.substring(timeStartPoint);

        //Find the end of the time.
        int timeEndPoint = cutString.indexOf("am") + 2;
        if (timeEndPoint - 2 == -1) {
            timeEndPoint = cutString.indexOf("pm") + 2;
        }

        return cutString.substring(0, timeEndPoint);
    }

    public String getDate()
    {
        String cutStringToStart = reservedSlot.substring(reservedSlot.indexOf(getEndTime()));

        //Get to the end of the am or pm of last time.
        int timeEndCutOff = cutStringToStart.indexOf("am") + 2;
        if (timeEndCutOff - 2 == -1) {
            timeEndCutOff = cutStringToStart.indexOf("pm") + 2;
        }
        //Cut to date
        return cutStringToStart.substring(timeEndCutOff + 1);
    }

    public String getStartEpochTimeString()
    {
        return String.valueOf(startEpochTime);
    }

    public long getStartEpochTime() {
        return startEpochTime;
    }

    public void setStartEpochTime(long startEpochTime) {
        this.startEpochTime = startEpochTime;
    }

    public String getReservedSlot() {
        return reservedSlot;
    }

    public void setReservedSlot(String reservedSlot) {
        this.reservedSlot = reservedSlot;
    }
}
