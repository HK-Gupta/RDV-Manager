package com.example.rdvmanager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rdvmanager.databinding.ActivitySelectTimeBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectTime extends ComponentActivity {

    ActivitySelectTimeBinding binding;
    MeetingDao meetingDao;
    TimePicker alarmTimePicker;
    MeetingHelper meetingDatabase;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectTimeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Room database and DAO
        meetingDatabase = MeetingHelper.getInstance(this);
        meetingDao = meetingDatabase.meetingDao();

        binding.time2Min.setOnClickListener(view -> scheduleAlarm(2));
        binding.time5Min.setOnClickListener(view -> scheduleAlarm(5));
        binding.time30Min.setOnClickListener(view -> scheduleAlarm(30));
        binding.time1Hour.setOnClickListener(view -> scheduleAlarm(60));
        binding.time1Day.setOnClickListener(view -> scheduleAlarm(24 * 60));
        binding.time2Day.setOnClickListener(view -> scheduleAlarm(2 * 24 * 60));

    }

    private void scheduleAlarm(int time) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("time", time);
        startActivity(intent);
    }


}
