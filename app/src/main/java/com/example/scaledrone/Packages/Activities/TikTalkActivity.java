package com.example.scaledrone.Packages.Activities;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.example.scaledrone.Packages.Fragement.RoomsFragment;
import com.example.scaledrone.Packages.Fragement.CreateRoomFragment;
import com.example.scaledrone.Packages.Objects.User;
import com.example.scaledrone.Packages.R;
import com.example.scaledrone.Packages.Adapters.SectionsPageAdapter;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import io.realm.Realm;
import io.realm.SyncUser;


public class TikTalkActivity extends AppCompatActivity {

    private static final String TAG = "TikTalkActivity";

    private SectionsPageAdapter mSectionsPageAdapter;

    private ViewPager mViewPager;

    private String username;

    private Realm realm;

    MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SyncUser syncUser = SyncUser.current();
            if (syncUser != null) {
                syncUser.logOut();
                Intent intent = new Intent(this, LogInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Bundle bundle = new Bundle();
        bundle.putString("username", getCurrentUsername());
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment( new RoomsFragment(), "Rooms");
        adapter.getItem(0).setArguments(bundle);
        adapter.addFragment(new CreateRoomFragment(), "Create Room");
        adapter.getItem(1).setArguments(bundle);
        viewPager.setAdapter(adapter);
    }

    public String getCurrentUsername(){
        return username;
    }
}