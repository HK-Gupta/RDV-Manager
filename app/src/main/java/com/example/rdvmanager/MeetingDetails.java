package com.example.rdvmanager;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rdvmanager.databinding.ActivityMainBinding;
import com.example.rdvmanager.databinding.ActivityMeetingDetailsBinding;

import java.util.Objects;

public class MeetingDetails extends AppCompatActivity {

    ActivityMeetingDetailsBinding binding;
    private String meetingName, meetingTime, meetingContact, meetingAddress, meetingDate, canEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMeetingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null) {
            meetingName = intent.getStringExtra("meeting_name");
            meetingTime = intent.getStringExtra("meeting_time");
            meetingContact = intent.getStringExtra("meeting_contact");
            meetingAddress = intent.getStringExtra("meeting_address");
            meetingDate = intent.getStringExtra("meeting_date");
            canEdit = intent.getStringExtra("can_editable");

            binding.detailDesc.setText(meetingName);
            binding.detailDate.setText(meetingDate);
            binding.detailTime.setText(meetingTime);
            binding.detailAdd.setText(meetingAddress);
            binding.detailContact.setText(meetingContact);

            if(Objects.equals(canEdit, "Yes")) {
                updateNewMeeting();
            }
            else {
                binding.detailDesc.setKeyListener(null);
                binding.detailDate.setKeyListener(null);
                binding.detailTime.setKeyListener(null);
                binding.detailAdd.setKeyListener(null);
                binding.detailContact.setKeyListener(null);
            }

        }

        binding.goBack.setOnClickListener(view-> {
            finish();
        });


    }

    private void updateNewMeeting() {

        binding.saveMeeting.setVisibility(View.VISIBLE);

        MeetingHelper meetingDatabase = MeetingHelper.getInstance(this);
        MeetingDao meetingDao = meetingDatabase.meetingDao();

        binding.saveMeeting.setOnClickListener(view -> {
            meetingName = binding.detailDesc.getText().toString();
            meetingDate = binding.detailDate.getText().toString();
            meetingTime = binding.detailTime.getText().toString();
            meetingContact = binding.detailContact.getText().toString();
            meetingAddress = binding.detailAdd.getText().toString();

            Meetings meeting = new Meetings(meetingName, meetingDate, meetingTime, meetingContact, meetingAddress);
            meetingDao.addMeeting(meeting);

            MainActivity mainActivity = (MainActivity) getBaseContext();
            mainActivity.showMeetings();

            finish();
        });

    }
}