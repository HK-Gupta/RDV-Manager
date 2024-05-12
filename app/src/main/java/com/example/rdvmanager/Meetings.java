package com.example.rdvmanager;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = Constants.TABLE_NAME)
public class Meetings {

    @PrimaryKey(autoGenerate = true)
    int id = 0;

    @ColumnInfo(name = Constants.COLUMN1)
    public String MeetingName;
    @ColumnInfo(name = Constants.COLUMN2)
    public String MeetingDate;
    @ColumnInfo(name = Constants.COLUMN3)
    public String MeetingTime;
    @ColumnInfo(name = Constants.COLUMN4)
    public String MeetingContact;
    @ColumnInfo(name = Constants.COLUMN5)
    public String MeetingAddress;

    // Empty constructor
    public Meetings() {
    }

    // Constructor with all fields
    public Meetings(int id, String meetingName, String meetingDate, String meetingTime, String meetingContact, String meetingAddress) {
        this.id = id;
        MeetingName = meetingName;
        MeetingDate = meetingDate;
        MeetingTime = meetingTime;
        MeetingContact = meetingContact;
        MeetingAddress = meetingAddress;
    }

    // Constructor without id field (assuming auto-generated)
    public Meetings(String meetingName, String meetingDate, String meetingTime, String meetingContact, String meetingAddress) {
        MeetingName = meetingName;
        MeetingDate = meetingDate;
        MeetingTime = meetingTime;
        MeetingContact = meetingContact;
        MeetingAddress = meetingAddress;
    }
}
