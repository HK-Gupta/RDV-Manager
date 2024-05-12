package com.example.rdvmanager;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.example.rdvmanager.adapter.MeetingAdapter;
import com.example.rdvmanager.databinding.ActivityMainBinding;
import com.example.rdvmanager.fragments.CreateMeetingFragment;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.Locale;
import android.app.AlarmManager;
import android.app.TimePickerDialog;



public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MeetingAdapter meetingAdapter;
    private MeetingHelper dbHelper;
    private CreateMeetingFragment createMeetingFragment;
    private int time;
    MediaPlayer mp;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    boolean canPlay = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mp = MediaPlayer.create(this, R.raw.solo_leveling_dark_ari);

        dbHelper = MeetingHelper.getInstance(this);

        binding.addMeeting.setOnClickListener(view -> {
            createMeetingFragment = new CreateMeetingFragment();
            createMeetingFragment.show(getSupportFragmentManager(), "Test");
        });

        showMeetings();
        createNotificationChannel();
        Intent intent = getIntent();
        time = intent.getIntExtra("time", 0);

        scheduleAlarm(time);

        binding.setAlarm.setOnClickListener(view-> {
            startActivity(new Intent(this, SelectTime.class));
        });

        binding.playSong.setOnClickListener(view-> {
            if(canPlay) {
                mp.start();
                binding.playSong.setImageResource(R.drawable.speaker_on);
            }
            else {
                mp.pause();
                binding.playSong.setImageResource(R.drawable.speaker_off);
            }
            canPlay = !canPlay;
        });

        binding.changeBG.setOnClickListener(view-> {
            int randomColor = getRandomColor();
            // Set the background color of the main activity
            binding.main.setBackgroundColor(ContextCompat.getColor(view.getContext(), randomColor));
        });

    }

    private int getRandomColor() {
        TypedArray colors = getResources().obtainTypedArray(R.array.background_colors);
        int randomIndex = (int) (Math.random() * colors.length());
        int color = colors.getResourceId(randomIndex, 0);
        colors.recycle();
        return color;
    }

    private void scheduleAlarm(int time) {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        List<Meetings> meetings = dbHelper.meetingDao().getAllMeetings();
        long currentTime = System.currentTimeMillis();

        for (Meetings meeting : meetings) {
            long meetingTime = getMeetingTimeInMillis(meeting.MeetingDate, meeting.MeetingTime);
            long difference = meetingTime - currentTime;

            Log.d("TIMEDIFF", meetingTime + " - " + currentTime);

            // If the meeting is in the future and less than 5 minutes away, set the alarm
            if (difference > 0 && difference <= (time * 60 * 1000)) { // 5 minutes before
                Intent intent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                alarmManager.set(AlarmManager.RTC_WAKEUP, meetingTime - (5 * 60 * 1000), pendingIntent);

                Toast.makeText(this, "Alarm Set for " + meeting.MeetingName + " at " + formatMeetingTime(meeting.MeetingDate, meeting.MeetingTime), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private long getMeetingTimeInMillis(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        try {
            Date meetingDateTime = sdf.parse(date + " " + time);
            return meetingDateTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String formatMeetingTime(String date, String time) {
        return date + " " + time;
    }


    public void showMeetings() {
        ArrayList<Meetings> arrayMeeting = (ArrayList<Meetings>) dbHelper.meetingDao().getAllMeetings();

        if(arrayMeeting.isEmpty()) {
            startAnimation(binding.imageView);
            binding.itemRecyclerView.setVisibility(View.GONE);
            binding.animLayout.setVisibility(View.VISIBLE);
        } else {
            binding.itemRecyclerView.setVisibility(View.VISIBLE);
            binding.animLayout.setVisibility(View.GONE);
            binding.itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            meetingAdapter = new MeetingAdapter(arrayMeeting);
            binding.itemRecyclerView.setAdapter(meetingAdapter);
            meetingAdapter.notifyDataSetChanged();
        }

    }

    private void startAnimation(final ImageView imageView) {
        // Load the animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        animation.setRepeatCount(Animation.INFINITE); // Set the animation to repeat infinitely

        // Set a listener to restart the animation when it ends
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Start the animation again when it ends
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.startAnimation(animation);
                    }
                }, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // Apply animation to ImageView
        imageView.startAnimation(animation);
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}