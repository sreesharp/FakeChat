package com.sreesharp.sreechat.activties;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.adapters.TabPagerAdapter;
import com.sreesharp.sreechat.fragments.ChatsFragment;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //User user = User.getGetCurrentUser(this);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager  = new TabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapterViewPager);

        // Connect the TabLayout with viewpager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //TODO: Temporary hack to set the current user
        if(User.getGetCurrentUser() == null)
            User.setCurrentUser(new User("0000","Self","NoPic"));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
