package com.sreesharp.sreechat.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sreesharp.sreechat.fragments.ChatsFragment;
import com.sreesharp.sreechat.fragments.ContactsFragment;

/**
 * Created by purayil on 10/16/2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 2;
    private final String[] tabTitles = new String[] { "CHATS", "CONTACTS" };

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) //First tab
            return new ChatsFragment();
        else
            return new ContactsFragment();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
