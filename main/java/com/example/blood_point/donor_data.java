package com.example.blood_point;

public class donor_data
{
    String Name,Phone_no,Address;
    public donor_data() {

    }
    public donor_data(String name, String phone_no, String address) {
        Name = name;
        Phone_no = phone_no;
        Address = address;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone_no() {
        return Phone_no;
    }

    public void setPhone_no(String phone_no) {
        Phone_no = phone_no;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
