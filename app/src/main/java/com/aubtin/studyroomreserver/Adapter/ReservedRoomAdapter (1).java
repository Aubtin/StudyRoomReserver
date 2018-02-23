package com.aubtin.studyroomreserver.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aubtin.studyroomreserver.R;
import com.aubtin.studyroomreserver.utils.RequestedRooms;
import com.aubtin.studyroomreserver.utils.ReservedRooms;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Aubtin on 1/31/2017.
 */

public class ReservedRoomAdapter extends RecyclerView.Adapter<ReservedRoomAdapter.ViewHolder> {
    private List<ReservedRooms> reservedRoomsTemp;
    ReservedRoomAdapter.ReservedAdapterListeners clickListener;

    public interface ReservedAdapterListeners {
        void clickedItemReserved(int position);
    }
    //Pass in constructor when needed for clicks.
//    public interface RequestedRoomsAdapterListener {
//
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView timeTV;
        public TextView dayTV;
        public TextView roomTV;
        public TextView assignedToTV;

        View itemViews;

        public ViewHolder(View view) {
            super(view);
            timeTV = (TextView) view.findViewById(R.id.rqTimeTV);
            dayTV = (TextView) view.findViewById(R.id.rqDayTV);
            roomTV = (TextView) view.findViewById(R.id.rqRoomNameTV);
            assignedToTV = (TextView) view.findViewById(R.id.assignedToTV);

            itemViews = view;
        }
    }

    public ReservedRoomAdapter(List<ReservedRooms> requestedRoomsTemp, ReservedRoomAdapter.ReservedAdapterListeners clickListener) {
        this.reservedRoomsTemp = requestedRoomsTemp;
        this.clickListener = clickListener;
    }

    @Override
    public ReservedRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_requested, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ReservedRoomAdapter.ViewHolder holder, final int position) {
        ReservedRooms currentRoom = reservedRoomsTemp.get(position);
        if(getItemCount() > 0)
        {
            holder.timeTV.setText(currentRoom.getStartTime() + " - " + currentRoom.getEndTime());
            holder.dayTV.setText(currentRoom.getDate());
            holder.roomTV.setText(currentRoom.getRoomName());
            holder.assignedToTV.setVisibility(View.INVISIBLE);

            //Check if it's in the current time block.
//            DateTimeZone timeZone = DateTimeZone.getDefault();
            DateTime now = new DateTime(new Date(currentRoom.getStartEpochTime()));
            Interval currentBlock = new Interval(now, now.plusMinutes(30));
//            Date epochDate = new Date(currentRoom.getStartEpochTime()); // *1000 is to convert seconds to milliseconds
            DateTime dateTime = new DateTime();

            if(currentBlock.contains(dateTime)) {
                holder.itemView.setBackgroundColor(Color.GREEN);
                holder.timeTV.setTextColor(Color.WHITE);
                holder.dayTV.setTextColor(Color.WHITE);
                holder.roomTV.setTextColor(Color.WHITE);
            }
            else
            {
                holder.itemView.setBackgroundColor(Color.WHITE);
                holder.timeTV.setTextColor(Color.BLACK);
                holder.dayTV.setTextColor(Color.BLACK);
                holder.roomTV.setTextColor(Color.BLACK);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.clickedItemReserved(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return reservedRoomsTemp.size();
    }
}
