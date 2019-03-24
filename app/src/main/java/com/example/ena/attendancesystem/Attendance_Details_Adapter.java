package com.example.ena.attendancesystem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Attendance_Details_Adapter extends RecyclerView.Adapter<Attendance_Details_Adapter.MyViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Attendance_Details_Report> arrayList = new ArrayList<>();
    private List<Attendance_Details_Report> contactList;
    private List<Attendance_Details_Report> contactListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTaskDescription, textViewTaskPriority, textViewRandomString,textViewTaskPertainsTo;


        public MyViewHolder(View view) {
            super(view);
            textViewTaskDescription = view.findViewById(R.id.textViewTaskDescription);
            textViewTaskPriority = view.findViewById(R.id.textViewTaskPriority);
            textViewTaskPertainsTo = view.findViewById(R.id.textViewTaskPertainsTo);
            textViewRandomString = view.findViewById(R.id.textViewRandomString);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public Attendance_Details_Adapter(Context context, List<Attendance_Details_Report> contactList, ContactsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_details_report_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Attendance_Details_Report contact = contactListFiltered.get(position);
        holder.textViewTaskDescription.setText(contact.getSubject());
        holder.textViewTaskPriority.setText(contact.getDescription());
    //    holder.textViewTaskPertainsTo.setText(contact.getDate());


    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<Attendance_Details_Report> filteredList = new ArrayList<>();
                    for (Attendance_Details_Report row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSubject().toLowerCase().contains(charString.toLowerCase()) || row.getDescription().toLowerCase().contains(charString.toLowerCase()) || row.getDate().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<Attendance_Details_Report>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public interface ContactsAdapterListener {
        void onContactSelected(Attendance_Details_Report contact);
    }
}