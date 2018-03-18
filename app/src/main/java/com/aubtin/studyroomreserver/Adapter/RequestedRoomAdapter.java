package com.aubtin.studyroomreserver.Adapter;

import android.app.DownloadManager;
import android.graphics.Color;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aubtin.studyroomreserver.R;
import com.aubtin.studyroomreserver.utils.RequestedRooms;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Aubtin on 1/31/2017.
 */

public class RequestedRoomAdapter extends RecyclerView.Adapter<RequestedRoomAdapter.ViewHolder> {
    private List<RequestedRooms> requestedRoomsTemp;
    RequestAdapterListeners clickListener;

    public interface RequestAdapterListeners {
        void clickedItemRequest(int position);
    }

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

    public RequestedRoomAdapter(List<RequestedRooms> requestedRoomsTemp, RequestAdapterListeners clickListener) {
        this.requestedRoomsTemp = requestedRoomsTemp;
        this.clickListener = clickListener;
    }

    @Override
    public RequestedRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_requested, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RequestedRoomAdapter.ViewHolder holder, final int position) {
        RequestedRooms reqObj = requestedRoomsTemp.get(position);
        if(getItemCount() > 0)
        {
            holder.timeTV.setText(reqObj.getReadableTime(reqObj.getStartTime()) + " - " + reqObj.getReadableTime(reqObj.getEndTime()));
            holder.dayTV.setText(reqObj.getReadableDay(reqObj.getDay()));
            holder.roomTV.setText(reqObj.getReadableRoom(reqObj.getRoomNumber()));
            holder.assignedToTV.setText(reqObj.getAssignedUserName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.clickedItemRequest(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return requestedRoomsTemp.size();
    }
}
