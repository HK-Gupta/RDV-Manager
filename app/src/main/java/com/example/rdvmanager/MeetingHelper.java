package com.example.rdvmanager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Meetings.class}, version = 1, exportSchema = false)
public abstract class MeetingHelper extends RoomDatabase {
    private static final String DB_NAME = "meeting_db";
    private static MeetingHelper instance;
    public abstract MeetingDao meetingDao();
    public static synchronized MeetingHelper getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MeetingHelper.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }


}
