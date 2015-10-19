package com.sreesharp.sreechat.fragments;



import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.activties.ChatActivity;
import com.sreesharp.sreechat.adapters.MessageCursorAdapter;
import com.sreesharp.sreechat.adapters.RecentChatCursorAdapter;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    ListView lvChats;
    RecentChatCursorAdapter recentChatAdapter;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        lvChats = (ListView)view.findViewById(R.id.lvChats);
        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) recentChatAdapter.getItem(position);
                cur.moveToPosition(position);
                String contactId = cur.getString(cur.getColumnIndexOrThrow(DbHelper.KEY_CONTACT_ID));
                String displayName = cur.getString(cur.getColumnIndexOrThrow(DbHelper.KEY_USER_NAME));
                String photoUrl = cur.getString(cur.getColumnIndexOrThrow(DbHelper.KEY_USER_PHOTO_URL ));

                User userTo = new User(contactId,displayName, photoUrl);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userTo", userTo);
                startActivity(intent);
            }
        });

        return view;
    }
    @Override
    public void onResume(){
        super.onResume();

        recentChatAdapter = new RecentChatCursorAdapter(getActivity(), DbHelper.getInstance(getActivity()).getRecentChatsCursor());
        lvChats.setAdapter(recentChatAdapter);
    }

}
