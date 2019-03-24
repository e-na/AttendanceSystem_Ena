package com.example.ena.attendancesystem;

public class Attendance_Details_Report {


    String subject;
    String date;
    String description;

    public Attendance_Details_Report(String subject, String date, String description) {

        this.subject = subject;
        this.date = date;
        this.description = description;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
