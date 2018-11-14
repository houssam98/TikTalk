package com.example.scaledrone.Packages.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.Objects.Message;
import com.example.scaledrone.Packages.Objects.Room;
import com.example.scaledrone.Packages.R;
import com.example.scaledrone.Packages.Objects.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;


public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.MyViewHolder>{
    private List<Room> roomsList;
    private Realm realm;

    public RoomsAdapter(List<Room> itemsList) {
        this.roomsList = itemsList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView room_name, room_date, room_message;
        public ImageView room_image, room_password;

        public MyViewHolder(View view) {
            super(view);
            room_name = (TextView) view.findViewById(R.id.room_name);
            room_date = (TextView) view.findViewById(R.id.room_date);
            room_image = (ImageView) view.findViewById(R.id.room_image);
            room_message = (TextView) view.findViewById(R.id.room_message);
            room_password = (ImageView) view.findViewById(R.id.room_password);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rooms_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Room room = roomsList.get(position);
        holder.room_name.setText(room.getRoomName());
        holder.room_image.setImageBitmap(room.getRoomImage());
        String date = room.getTimestamp().toString();
        String[] items = date.split(" ");
        date = items[1] + " " + items[2];
        holder.room_date.setText(date);
        holder.room_message.setText(getRoomLastMessage(room.getRoomName()));
        if(getRoomLock(roomsList.get(position).getRoomName())){
            holder.room_password.setImageResource(R.drawable.lock);
        }
    }

    public boolean getRoomLock(String roomName){
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        realm = Realm.getInstance(configuration);
        Room room = new Room();
        RealmResults<Room> rooms = realm.where(Room.class).equalTo("roomName", roomName).sort("timestamp").findAllAsync();
        realm.close();
        for(Room room1 : rooms){
            room = room1;
        }
        if(room.getRoomPassword() != null){
            return true;
        }
        return false;
    }


    public String getRoomLastMessage(String roomName){
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        realm = Realm.getInstance(configuration);
        RealmResults<Message> messages = realm.where(Message.class).equalTo("roomName", roomName).sort("timestamp").findAllAsync();
        realm.close();
        Message message = new Message();
        for(Message message1 : messages){
            message = message1;
        }
        String message_final;
        if(message.getOwner().equals("")) {
            message_final = "";
        }
        else {
            message_final = message.getOwner() + ": " + message.getText();
        }
        if(message_final.length() >= 29){
            message_final = message_final.substring(0, 27) + "...";
        }
        return message_final;
    }


    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public void removeItem(int position) {

        roomsList.remove(position);

        notifyItemRemoved(position);
    }


    public List<Room> getData() {
        return roomsList;
    }
}
