package com.example.scaledrone.Packages.Activities;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scaledrone.Packages.Objects.Room;
import com.example.scaledrone.Packages.Objects.User;
import com.example.scaledrone.Packages.R;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.ObjectServerError;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import io.realm.exceptions.RealmException;

import com.example.scaledrone.Packages.Objects.Constants;


public class SignUpActivity extends AppCompatActivity {

    Realm realm;

    Button change_pic;
    CircleImageView profile_pic;
    EditText username;
    EditText password;
    Button sign_up;

    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    String username1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        Realm.init(this);

        profile_pic = findViewById(R.id.imageView_profile);
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        change_pic = findViewById(R.id.button_image);
        sign_up = findViewById(R.id.button_signup);

        change_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncCredentials credentials = SyncCredentials.usernamePassword(username.getText().toString(), password.getText().toString(), true);
                SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                    @Override
                    public void onSuccess(SyncUser user) {
                       // showProgress(false);
                        username1 = credentials.getUserIdentifier();
                        Log.d("USERNAME", username1);
                        SyncConfiguration configuration = SyncUser.current()
                                .createConfiguration(Constants.REALM_BASE_URL + "/default")
                                .build();
                        realm = Realm.getInstance(configuration);
                        realm.executeTransactionAsync(realm -> {
                            User user1 = new User();
                            user1.setUsername(username1);
                            if(profile_pic != null){
                                user1.setProfile_picture(convertImage());
                            }
                                realm.insert(user1);
                        });
                        realm.close();
                        goToTalkActivity(username1);
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                       // showProgress(false);
                        username.setError("Unfortunately, this name has been taken!");
                        username.requestFocus();
                        Log.e("Login error", error.toString());
                    }
                });
            }
        });
    }

    private void goToTalkActivity(String username1){
        Intent intent = new Intent(SignUpActivity.this, TikTalkActivity.class);
        intent.putExtra("username", username1);
        startActivity(intent);
        finish();
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data.getData();
            profile_pic.setImageURI(imageUri);
        }
    }

    private byte[] convertImage() {
        byte[] imageInByte;
        Bitmap bitmap = ((BitmapDrawable) profile_pic.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageInByte = baos.toByteArray();
        return imageInByte;
    }
}