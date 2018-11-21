package com.example.scaledrone.Packages.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.Objects.MemberData;
import com.example.scaledrone.Packages.Objects.Message;
import com.example.scaledrone.Packages.Adapters.MessageAdapter;
import com.example.scaledrone.Packages.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Member;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;

public class RoomChatActivity extends AppCompatActivity implements RoomListener {

    private String channelID = "AmMpKS4ir9CUKeYS";
    private String roomName;
    private EditText editText;
    private TextView roomName_textview;
    private ImageButton button_back;
    private CircleImageView room_image;
    private Scaledrone scaledrone;
    private MessageAdapter messageAdapter;
    private ListView messagesView;
    private String username;
    private MemberData memberdata;
    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        messageAdapter = new MessageAdapter(this);
        messagesView = (ListView) findViewById(R.id.messages_view);
        messagesView.setAdapter(messageAdapter);

        Intent intentExtras = getIntent();
        roomName = "observable-" + intentExtras.getStringExtra("room_name");
        username = intentExtras.getStringExtra("username");
        memberdata = new MemberData(username, getRandomColor());

        roomName_textview = (TextView) findViewById(R.id.room_name);
        button_back = (ImageButton) findViewById(R.id.back_image_button);
        room_image = (CircleImageView) findViewById(R.id.room_image);

        roomName_textview.setText(getRoomName(roomName));
        if(getRoomImage(roomName) != null) {
            room_image.setImageBitmap(getRoomImage(roomName));
        }
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomChatActivity.this, TikTalkActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        getMessageHistory(roomName);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scaledrone = new Scaledrone(channelID, memberdata);
        scaledrone.connect(new Listener() {
            @Override
            public void onOpen() {
                System.out.println("Scaledrone connection open");
                scaledrone.subscribe(roomName, RoomChatActivity.this);

            }

            @Override
            public void onOpenFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onFailure(Exception ex) {
                System.err.println(ex);
            }

            @Override
            public void onClosed(String reason) {
                System.err.println(reason);
            }
        });
    }

    public void sendMessage(View view) {
        String message = editText.getText().toString();

        if (message.length() > 0) {
            SyncConfiguration configuration = SyncUser.current()
                    .createConfiguration(Constants.REALM_BASE_URL + "/default")
                    .build();
            realm = Realm.getInstance(configuration);

                    realm.executeTransactionAsync(realm -> {
                        Message message_db = realm.createObject(Message.class, UUID.randomUUID().toString());
                        message_db.setText(message);
                        message_db.setOwner(username);
                        message_db.setRoomName(getRoomName(roomName));
                        realm.insert(message_db);
                    });
                    realm.close();
                    scaledrone.publish(roomName, message);
                    editText.getText().clear();
        }
    }

    @Override
    public void onOpen(Room room) {
        System.out.println("Conneted to room");
    }

    @Override
    public void onOpenFailure(Room room, Exception ex) {
        System.err.println(ex);
    }

    @Override
    public void onMessage(Room room, final JsonNode json, final Member member) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData data = mapper.treeToValue(member.getClientData(), MemberData.class);
            boolean belongsToCurrentUser = member.getId().equals(scaledrone.getClientID());
            final Message message = new Message(json.asText(), data, roomName, belongsToCurrentUser);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RoomChatActivity.this, TikTalkActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    private void getMessageHistory(String roomName){
        String room_name = getRoomName(roomName);
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        realm = Realm.getInstance(configuration);
        RealmResults<Message> messages = realm.where(Message.class).equalTo("roomName", room_name).sort("timestamp").findAllAsync();
        realm.close();

            for (Message message : messages) {
                if(message.getOwner().equals(username)) {
                    message.setBelongsToCurrentUser(true);
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
                else{
                    message.setBelongsToCurrentUser(false);
                    messageAdapter.add(message);
                    messagesView.setSelection(messagesView.getCount() - 1);
                }
            }
    }

    private Bitmap getRoomImage(String roomName){
        Bitmap bmp = null;
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        realm = Realm.getInstance(configuration);
        RealmResults<com.example.scaledrone.Packages.Objects.Room> rooms = realm.where(com.example.scaledrone.Packages.Objects.Room.class).equalTo("roomName", getRoomName(roomName)).findAll();
        com.example.scaledrone.Packages.Objects.Room room = new com.example.scaledrone.Packages.Objects.Room();
        for(com.example.scaledrone.Packages.Objects.Room room1 : rooms){
            room = room1;
        }
        bmp = room.getRoomImage();
        return bmp;
    }

    public String getRoomName(String roomName){
        String[] roomNames = roomName.split("-");
        String room_name = roomNames[1];
        return room_name;
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_room) {
            deleteRoom(getRoomName(roomName));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    private void deleteRoom(String roomName){
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        Realm realm_2 = Realm.getInstance(configuration);
        realm_2.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<com.example.scaledrone.Packages.Objects.Room> rooms = realm_2.where(com.example.scaledrone.Packages.Objects.Room.class).equalTo("roomName", roomName).findAll();
                rooms.deleteAllFromRealm();
                RealmResults<Message> messages = realm_2.where(Message.class).equalTo("roomName", roomName).findAll();
                messages.deleteAllFromRealm();
            }
        });
       realm_2.close();

        Intent intent = new Intent(RoomChatActivity.this, TikTalkActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();

        }

}

