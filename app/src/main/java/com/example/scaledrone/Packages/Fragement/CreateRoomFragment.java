package com.example.scaledrone.Packages.Fragement;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scaledrone.Packages.Activities.TikTalkActivity;
import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.Objects.Room;
import com.example.scaledrone.Packages.Objects.User;
import com.example.scaledrone.Packages.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

import static android.app.Activity.RESULT_OK;


public class CreateRoomFragment extends Fragment {
    Realm realm;
    private String username_current;
    private User currentUser;
    private EditText roomName;
    private EditText roomPassword;
    private CircleImageView roomImage;
    private Button btnChoose;
    private Button btnCreate;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    private String username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_room_fragment,container,false);

        Realm.init(this.getContext());

        username = getArguments().getString("username");

        roomName = view.findViewById(R.id.room_name);
        roomPassword = view.findViewById(R.id.room_password);
        roomImage = view.findViewById(R.id.room_image);
        btnChoose = view.findViewById(R.id.button_image);
        btnCreate = view.findViewById(R.id.button_create);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncConfiguration configuration = SyncUser.current()
                        .createConfiguration(Constants.REALM_BASE_URL + "/default")
                        .build();

                realm = Realm.getInstance(configuration);
                realm.executeTransactionAsync(realm -> {
                Room room = new Room();
                room.setRoomName(roomName.getText().toString());
                if(!roomPassword.getText().toString().equals("")){
                    room.setRoomPassword(roomPassword.getText().toString());
                }
                if(roomImage != null){
                    room.setRoomImage(convertImage());
                }
                try {
                    realm.insert(room);
                }
                catch (RealmException e){
                    Toast.makeText(getActivity(), "Unfortunately, this name has been taken!", Toast.LENGTH_SHORT).show();
                }
                });

                realm.close();
                goToRoomsActivity();
            }
        });


        return view;
    }


    private void goToRoomsActivity(){
        Intent intent = new Intent(getContext(), TikTalkActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            roomImage.setImageURI(imageUri);
        }
    }

    private byte[] convertImage() {
        byte[] imageInByte;
        Bitmap bitmap = ((BitmapDrawable) roomImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageInByte = baos.toByteArray();
        return imageInByte;
    }


}