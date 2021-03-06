package com.example.scaledrone.Packages.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.scaledrone.Packages.Activities.RoomChatActivity;
import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.Objects.Message;
import com.example.scaledrone.Packages.Objects.User;
import com.example.scaledrone.Packages.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;


public class MessageAdapter extends BaseAdapter {

    private List<Message> messages = new ArrayList<Message>();
    private Context context;
    private Realm realm;
    private Handler handler;
    private boolean longClicked = false;

    public MessageAdapter(Context context) {
        this.context = context;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        handler = new Handler();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);

        if (message.isBelongsToCurrentUser()) {
            convertView = messageInflater.inflate(R.layout.my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.messageDate = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
            holder.messageBody.setText(message.getText());

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longClicked = true;
                    view.setBackgroundColor(Color.GRAY);

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(context);
                    }
                    builder.setTitle("Delete message")
                            .setMessage("Are you sure you want to delete this message?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteMessage(message);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    view.setBackgroundColor(0);
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    view.setBackgroundColor(0);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return false;
                }
            });

            holder.messageBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.messageDate.setText(message.toString());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.messageDate.setText("");
                        }
                    }, 1000);

                }
            });

        } else {
            convertView = messageInflater.inflate(R.layout.their_message, null);
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            holder.messageDate = (TextView) convertView.findViewById(R.id.message_time);
            convertView.setTag(holder);
            Bitmap bmp = setUpRealm(message.getOwner()).getProfile_picture();
            holder.name.setText(message.getOwner());
            holder.avatar.setImageBitmap(bmp);
            holder.messageBody.setText(message.getText());
            /*else{
                GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
                drawable.setColor(Color.parseColor(getRandomColor()));
                holder.name.setText(message.getOwner());
                Log.d("OWNER", holder.name.getText().toString());
                holder.messageBody.setText(message.getText());}*/

            holder.messageBody.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.messageDate.setText(message.toString());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.messageDate.setText("");
                        }
                    }, 1000);

                }
            });


        }

        return convertView;
    }

    private User setUpRealm(String username) {
        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        User user1 = new User();
        realm = Realm.getInstance(configuration);
       RealmResults<User> users =  realm.where(User.class).equalTo("username", username).findAllAsync();
        for(User user : users){
          user1 = user;
        }
        realm.close();
        return user1;
    }

    private void deleteMessage(Message message){
        messages.remove(message);

        SyncConfiguration configuration = SyncUser.current()
                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                .build();
        Realm realm_2 = Realm.getInstance(configuration);
        realm_2.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Message> message1 = realm_2.where(Message.class).equalTo("messageID", message.getMessageID()).findAll();
                message1.deleteAllFromRealm();
            }
        });
        notifyDataSetChanged();
    }

    public void clearAll(){
        messages.clear();
        notifyDataSetChanged();
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }


}

class MessageViewHolder {
    public CircleImageView avatar;
    public TextView name;
    public TextView messageBody;
    public TextView messageDate;
}