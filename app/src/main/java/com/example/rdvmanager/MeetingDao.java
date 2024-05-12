package com.example.rdvmanager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MeetingDao {

    @Query("SELECT * FROM " + Constants.TABLE_NAME)
    List<Meetings> getAllMeetings();

    @Insert
    void addMeeting(Meetings meetings);

    @Delete
    void  deleteMeeting(Meetings meetings);

    @Update
    void updateMeeting(Meetings meetings);


}

