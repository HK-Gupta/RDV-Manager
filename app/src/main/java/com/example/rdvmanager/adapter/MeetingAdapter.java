package com.example.rdvmanager.adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rdvmanager.MainActivity;
import com.example.rdvmanager.MeetingDao;
import com.example.rdvmanager.MeetingDetails;
import com.example.rdvmanager.MeetingHelper;
import com.example.rdvmanager.Meetings;
import com.example.rdvmanager.R;
import com.example.rdvmanager.databinding.MeetingItemBinding;
import com.example.rdvmanager.fragments.CreateMeetingFragment;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder> {

    private List<Meetings> meetingList;
    public Context mContext;

    public MeetingAdapter(List<Meetings> mList) {
        meetingList = mList;
    }

    @NonNull
    @Override
    public MeetingAdapter.MeetingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MeetingItemBinding binding = MeetingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MeetingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingAdapter.MeetingViewHolder holder, int position) {
        Meetings meeting = meetingList.get(position);
        holder.bind(meeting);
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }


    public class MeetingViewHolder extends RecyclerView.ViewHolder {
        private MeetingItemBinding binding;
        private String dayVal, dateVal, monthVal;
        private MeetingDao meetingDao;



        public MeetingViewHolder(MeetingItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            meetingDao = MeetingHelper.getInstance(binding.getRoot().getContext()).meetingDao();
        }



        public void bind(Meetings meetings) {
            binding.textMeetingInfo.setText(meetings.MeetingName);
            binding.textAddress.setText(meetings.MeetingTime);
            binding.textPersonName.setText(meetings.MeetingContact);
            binding.textAddress.setText(meetings.MeetingAddress);
            binding.textTime.setText(meetings.MeetingTime);

            findDayDate(meetings.MeetingDate);
            binding.textDay.setText(dayVal);
            binding.textDate.setText(dateVal);
            binding.textMonth.setText(monthVal);

            binding.cardView.setOnClickListener(view-> {
                // Create an Intent to start MeetingDetailsActivity
                callIntent(view, meetings, "No");
            });

            binding.imgShare.setOnClickListener(view -> {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareBody = "Meeting Pending\n" +
                        "Meeting Name: " + meetings.MeetingName +
                        "\nMeeting Time: " + meetings.MeetingTime +
                        "\nMeeting Contact: " + meetings.MeetingContact +
                        "\nMeeting Address: " + meetings.MeetingAddress +
                        "\nMeeting Date: " + meetings.MeetingDate;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                view.getContext().startActivity(Intent.createChooser(shareIntent, "Share meeting details via"));
            });

            binding.imgCall.setOnClickListener(view -> {
                // Create an Intent to dial the contact number
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + meetings.MeetingContact));
                view.getContext().startActivity(intent);
            });

            binding.imgMenu.setOnClickListener(view-> {
                showPopupMenu(meetings, view);
            });

            binding.imgLoc.setOnClickListener(view -> {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(meetings.MeetingAddress));

                // Create an Intent with the action VIEW and the Uri
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                // Set the package to Google Maps
                mapIntent.setPackage("com.google.android.apps.maps");

                // Check if there is an activity to handle the Intent
                if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
                    // Start the activity
                    view.getContext().startActivity(mapIntent);
                } else {
                    // Handle the case where Google Maps is not installed
                    Toast.makeText(view.getContext(), "Google Maps is not installed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void callIntent(View view, Meetings meetings, String canEdit) {
            Intent intent = new Intent(view.getContext(), MeetingDetails.class);
            // Pass the meeting details using Intent extras
            intent.putExtra("meeting_name", meetings.MeetingName);
            intent.putExtra("meeting_time", meetings.MeetingTime);
            intent.putExtra("meeting_contact", meetings.MeetingContact);
            intent.putExtra("meeting_address", meetings.MeetingAddress);
            intent.putExtra("meeting_date", meetings.MeetingDate);
            intent.putExtra("can_editable", canEdit);
            view.getContext().startActivity(intent);
        }

        private void showPopupMenu(Meetings meetings, View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_item);
            popupMenu.setOnMenuItemClickListener(item-> {
                if(item.getItemId() == R.id.menuDelete)
                    deleteItem(meetings);
                else if(item.getItemId() == R.id.menuUpdate)
                    updateItem(meetings);
                else if(item.getItemId() == R.id.menuComplete) {
                    showPopupMenuComplete(meetings, view);
                }

                return true;
            });


            popupMenu.show();
        }

        private void showPopupMenuComplete(Meetings meetings, View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.menu_item);
            popupMenu.setOnMenuItemClickListener(item-> {
                if(item.getItemId() == R.id.menuDelete)
                    deleteItem(meetings);
                else if(item.getItemId() == R.id.menuUpdate)
                    updateItem(meetings);
                else if(item.getItemId() == R.id.menuComplete) {
                    // Show dialog for confirmation
                    showCompleteConfirmationDialog(meetings);
                }

                return true;
            });

            popupMenu.show();
        }
        private void showCompleteConfirmationDialog(Meetings meetings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Complete Meeting");
            builder.setMessage("Are you sure you want to mark this meeting as complete?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Perform action on completion
                Log.d("Meeting Complete", "Meeting Complete");
                deleteItem(meetings);
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // Do nothing, just dismiss the dialog
                dialog.dismiss();
            });
            builder.show();
        }
        private void updateItem(Meetings meetings) {
            meetingList.remove(meetings);
            meetingDao.deleteMeeting(meetings);
            callIntent(itemView, meetings, "Yes");

            notifyDataSetChanged();
        }

        private void deleteItem(Meetings meetings) {
            meetingList.remove(meetings);
            meetingDao.deleteMeeting(meetings);
            notifyDataSetChanged();
        }

        private void findDayDate(String meetingDate) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                Date date = sdf.parse(meetingDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                // Extract day, date, and month
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);

                // Convert dayOfWeek to a human-readable string (short format)
                DateFormatSymbols dfs = new DateFormatSymbols(Locale.getDefault());
                String[] weekdaysShort = dfs.getShortWeekdays();
                String dayOfWeekString = weekdaysShort[dayOfWeek];

                // Convert monthVal to a human-readable string (short format)
                String[] monthsShort = dfs.getShortMonths();
                String monthString = monthsShort[month];

                dayVal = dayOfWeekString;
                dateVal = String.valueOf(dayOfMonth); // Convert dayOfMonth to String
                monthVal = monthString;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



    }

}
