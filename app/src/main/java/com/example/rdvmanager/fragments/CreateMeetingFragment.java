package com.example.rdvmanager.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.database.DatabaseUtils;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.rdvmanager.MainActivity;
import com.example.rdvmanager.MeetingDao;
import com.example.rdvmanager.MeetingHelper;
import com.example.rdvmanager.Meetings;
import com.example.rdvmanager.R;
import com.example.rdvmanager.adapter.MeetingAdapter;
import com.example.rdvmanager.databinding.FragmentCreateMeetingBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CreateMeetingFragment extends BottomSheetDialogFragment {

    private FragmentCreateMeetingBinding binding;
    private MeetingDao meetingDao;

    public CreateMeetingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateMeetingBinding.inflate(inflater, container, false);

        MeetingHelper meetingDatabase = MeetingHelper.getInstance(requireContext());
        meetingDao = meetingDatabase.meetingDao();

        binding.meetingDate.setOnClickListener(view -> {
            openDialogDate();
        });
        binding.meetingTime.setOnClickListener(view-> {
            openDialogTime();
        });

        binding.confirmMeeting.setOnClickListener(view -> {
            String desc = binding.meetingDesc.getText().toString();
            String date = binding.meetingDate.getText().toString();
            String time = binding.meetingTime.getText().toString();
            String contact = binding.contactNumber.getText().toString();
            String add = binding.address.getText().toString();

            // Create a new instance of the Meetings class
            Meetings meeting = new Meetings(desc, date, time, contact, add);
            meetingDao.addMeeting(meeting);

            ((MainActivity) requireActivity()).showMeetings();

            dismiss();

        });

        return binding.getRoot();
    }

//    private void openDialogTime() {
//        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker timePicker, int hour, int time) {
//                binding.meetingTime.setText(hour + ":" + time);
//            }
//        }, 00, 00, true);
//        dialog.show();
//    }
//
//    private void openDialogDate() {
//        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                binding.meetingDate.setText(day + "-" + (month+1) + "-" + year);
//            }
//        }, 2024, 3, 12);
//        dialog.show();
//    }

    private void openDialogTime() {
        // Get the current time
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);

        // Create a Calendar instance and set it to the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Extract the current hour and minute
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create the time picker dialog with the current time as initial values
        TimePickerDialog dialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                binding.meetingTime.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        dialog.show();
    }

    private void openDialogDate() {
        // Get the current date
        long currentTimeMillis = System.currentTimeMillis();
        Date currentDate = new Date(currentTimeMillis);

        // Create a Calendar instance and set it to the current date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Extract the current year, month, and day
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the date picker dialog with the current date as initial values
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                binding.meetingDate.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);
            }
        }, year, month, day);
        dialog.show();
    }


}