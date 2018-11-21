package com.example.scaledrone.Packages.Fragement;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.scaledrone.Packages.Activities.RoomChatActivity;
import com.example.scaledrone.Packages.Adapters.RecyclerTouchListener;
import com.example.scaledrone.Packages.Adapters.RoomsAdapter;
import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.Objects.Room;
import com.example.scaledrone.Packages.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class RoomsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RoomsAdapter roomsAdapter;
    private ArrayList<Room> roomsList = new ArrayList<>();
    private String username;
    Realm realm;
    Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        handler = new Handler();
        View view = inflater.inflate(R.layout.rooms_fragment,container,false);
        Realm.init(this.getContext());
        recyclerView = view.findViewById(R.id.recyclerView_rooms);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        RealmResults<Room> rooms = setUpRealm();
        for(Room room : rooms){
            if(room.getRoomName() != null){
                roomsList.add(room);
            }
        }
        roomsAdapter = new RoomsAdapter(roomsList);
        recyclerView.setAdapter(roomsAdapter);

        username = getArguments().getString("username");

       // enableSwipeToDeleteAndUndo();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(roomsList.get(position).getRoomPassword() != null) {
                    showInputDialog(roomsList.get(position).getRoomName(), roomsList.get(position).getRoomPassword(), position);
                }else{
                    Intent goToChat = new Intent(getContext(), RoomChatActivity.class);
                    goToChat.putExtra("room_name", roomsList.get(position).getRoomName());
                    goToChat.putExtra("username", username);
                    startActivity(goToChat);
                    getActivity().finish();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshRooms();
                roomsAdapter.notifyDataSetChanged();
                recyclerView.invalidate();
            }
        }, 1000);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refresh:
                refreshRooms();
                return true;
        }
        return false;
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final Room room = roomsAdapter.getData().get(position);

                roomsAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(getView(), "Room " + room.getRoomName() +"was removed!", Snackbar.LENGTH_SHORT);

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }



    private RealmResults<Room> setUpRealm() {
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();

        realm = Realm.getInstance(configuration);


        return realm
                .where(Room.class).sort("timestamp")
                .findAllAsync();
    }

    private boolean checkPassword(String roomName, String roomPassword){
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();

        realm = Realm.getInstance(configuration);
        Room room = null;
         RealmResults<Room> rooms = realm.where(Room.class).equalTo("roomName", roomName).findAllAsync();
         for(Room rooms1 : rooms){
             room = rooms1;
         }
        if(room.getRoomPassword().equals(roomPassword)){
            return true;
        }
        return false;
    }

    private void showInputDialog(String roomName, String roomPassword, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Enter password: ");
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkPassword(roomName, roomPassword)){
                    Intent goToChat = new Intent(getContext(), RoomChatActivity.class);
                    goToChat.putExtra("room_name", roomsList.get(position).getRoomName());
                    goToChat.putExtra("username", username);
                    startActivity(goToChat);
                    getActivity().finish();
                }else{
                    input.setError("Wrong password! Please try again");
                    input.requestFocus();
                    showInputDialog(roomName, roomPassword, position);
                }
            }
        });
        builder.show();
    }

    public void refreshRooms(){
        roomsList.clear();
        RealmResults<Room> rooms = setUpRealm();
        for(Room room : rooms){
            if(room.getRoomName() != null){
                roomsList.add(room);
            }
        }
        roomsAdapter.notifyDataSetChanged();
    }

}