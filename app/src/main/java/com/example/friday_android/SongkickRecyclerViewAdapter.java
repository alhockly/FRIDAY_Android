package com.example.friday_android;

import android.app.Application;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongkickRecyclerViewAdapter extends RecyclerView.Adapter<SongkickRecyclerViewAdapter.ViewHolder>  {

    GsonSongKickParser songKickData;
    IModifyUI modifyUI;
    List<GsonSongKickParser.CalenderEntry> calenderEntries;

    String[] months = {"Jan","Feb","March","April","May","June","July","Aug","Sep","Oct","Nov","Dec"};

    public SongkickRecyclerViewAdapter(GsonSongKickParser aGson, IModifyUI modUI){
        calenderEntries = aGson.getevents();
        modifyUI = modUI;
    }

    @NonNull
    @Override
    public SongkickRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater = LayoutInflater.from(modifyUI.getContext());

        View view = inflater.inflate(R.layout.songkick_recycler_row, parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SongkickRecyclerViewAdapter.ViewHolder holder, int position) {
        GsonSongKickParser.CalenderEntry entry = calenderEntries.get(position);
        holder.name.setText(entry.event.performance.get(0).displayName);
        holder.venue.setText("@ "+entry.event.venue.displayName);
        holder.features.setText(entry.event.featuredArtists);
        if (entry.event.start.dateobj != null) {
            int date = entry.event.start.dateobj.getDate();
            int month = entry.event.start.dateobj.getMonth();

            holder.date.setText(date+"\n"+months[month]);
        }



    }

    @Override
    public int getItemCount() {
        return calenderEntries.size();
        //return 10;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, venue, date, features;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artistName);
            venue = itemView.findViewById(R.id.venueName);
            date = itemView.findViewById(R.id.dateInfo);
            features = itemView.findViewById(R.id.artistFeatures);

            //define views
        }
    }
}
