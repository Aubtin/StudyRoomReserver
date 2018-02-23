package com.aubtin.studyroomreserver.utils;

/**
 * Created by Aubtin on 5/2/2017.
 */

public class CheckedInUser
{
    private String name;
    private String roomNumber;

    public CheckedInUser()
    {}

    public CheckedInUser(String name, String roomNumber) {
        this.name = name;
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
