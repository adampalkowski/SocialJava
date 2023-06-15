package com.example.socialv2.Model;

public class UserActivity {
    private String LengthHours;
    private String LengthMinutes;
    private String activityName;
    private String date;
    private String location;
    private String startTimeHour;
    private String startTimeMinute;

    public UserActivity(String lengthHours, String lengthMinutes, String activityName, String date, String location, String startTimeHour, String startTimeMinute) {
        this.LengthHours = lengthHours;
        this.LengthMinutes = lengthMinutes;
        this.activityName = activityName;
        this.date = date;
        this.location = location;
        this.startTimeHour = startTimeHour;
        this.startTimeMinute = startTimeMinute;
    }

    public UserActivity() {
        LengthHours = "";
        LengthMinutes = "";
        this.activityName = "none";
        this.date = "";
        this.location = "";
        this.startTimeHour = "";
        this.startTimeMinute = "";
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "LengthHours='" + LengthHours + '\'' +
                ", LengthMinutes='" + LengthMinutes + '\'' +
                ", activityName='" + activityName + '\'' +
                ", date='" + date + '\'' +
                ", location='" + location + '\'' +
                ", startTimeHour='" + startTimeHour + '\'' +
                ", startTimeMinute='" + startTimeMinute + '\'' +
                '}';
    }

    public String getLengthHours() {
        return LengthHours;
    }

    public void setLengthHours(String lengthHours) {
        LengthHours = lengthHours;
    }

    public String getLengthMinutes() {
        return LengthMinutes;
    }

    public void setLengthMinutes(String lengthMinutes) {
        LengthMinutes = lengthMinutes;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTimeHour() {
        return startTimeHour;
    }

    public void setStartTimeHour(String startTimeHour) {
        this.startTimeHour = startTimeHour;
    }

    public String getStartTimeMinute() {
        return startTimeMinute;
    }

    public void setStartTimeMinute(String startTimeMinute) {
        this.startTimeMinute = startTimeMinute;
    }
}
