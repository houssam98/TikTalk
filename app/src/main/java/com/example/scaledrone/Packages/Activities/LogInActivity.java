package com.example.scaledrone.Packages.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scaledrone.Packages.Objects.Constants;
import com.example.scaledrone.Packages.R;
import com.example.scaledrone.Packages.Objects.User;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

public class LogInActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button login;
    Button signup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        Realm.init(getApplicationContext());


        if (SyncUser.current() != null) {
            this.goToTalkActivity(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("USERNAME", "")); // CHHHHHHHHHHHHHHHHHHEEEEEEEEEECKKKKKKKKKKKKKK

        }

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.login_button);
        signup = findViewById(R.id.button_signup);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncCredentials credentials = SyncCredentials.usernamePassword(username.getText().toString(), password.getText().toString());
                SyncUser.logInAsync(credentials, Constants.AUTH_URL, new SyncUser.Callback<SyncUser>() {
                    @Override
                    public void onSuccess(SyncUser user) {
                        // showProgress(false);
                        goToTalkActivity(credentials.getUserIdentifier());
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("USERNAME", username.getText().toString()).apply();
                    }

                    @Override
                    public void onError(ObjectServerError error) {
                        // showProgress(false);
                        password.setError("Uh oh something went wrong!");
                        password.requestFocus();
                        Log.e("Login error", error.toString());
                    }
                });
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToNextActivity = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(goToNextActivity);
            }
        });

    }

    private void goToTalkActivity(String username){
        Intent intent = new Intent(LogInActivity.this, TikTalkActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }


}