package com.example.blood_point;

public class user_data {
    String Blood_group, Phone_no, Name, Time, Date, Address, Location;
    Double Latitude, Longitude;

    public user_data() {
        // Default constructor required for calls to DataSnapshot.getValue(user_data.class)
    }

    public user_data(String blood_group, String phone_no, String name, String time, String date, String address, String location, Double latitude, Double longitude) {
        Blood_group = blood_group;
        Phone_no = phone_no;
        Name = name;
        Time = time;
        Date = date;
        Address = address;
        Location = location;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getBlood_group() {
        return Blood_group;
    }

    public void setBlood_group(String blood_group) {
        Blood_group = blood_group;
    }

    public String getPhone_no() {
        return Phone_no;
    }

    public void setPhone_no(String phone_no) {
        Phone_no = phone_no;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
