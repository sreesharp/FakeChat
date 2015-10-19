package com.sreesharp.sreechat.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.activties.ChatActivity;
import com.sreesharp.sreechat.activties.MainActivity;
import com.sreesharp.sreechat.adapters.ContactsCursorAdapter;
import com.sreesharp.sreechat.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    private ContactsCursorAdapter adapter;
    private static final int CONTACT_LOADER_ID = 78;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        Fresco.initialize(getActivity());
        setupCursorAdapter();
        getLoaderManager().initLoader(CONTACT_LOADER_ID,
                new Bundle(), contactsLoader);
        ListView lvContacts = (ListView) view.findViewById(R.id.lvContacts);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);
                String contactId = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                String displayName = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String photoUrl = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI ));

                User userTo = new User(contactId,displayName, photoUrl);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("userTo", userTo);
                startActivity(intent);
            }
        });
        return view;
    }


    private void setupCursorAdapter() {
        String[] uiBindFrom = { ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI };
        int[] uiBindTo = { R.id.tvContactName, R.id.ivProfile };
        adapter = new ContactsCursorAdapter( getActivity(), R.layout.item_contact,
                null, uiBindFrom, uiBindTo,0);
    }

    private LoaderManager.LoaderCallbacks<Cursor> contactsLoader =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    // Define the columns to retrieve
                    String[] projectionFields = new String[] { ContactsContract.Contacts._ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.Contacts.PHOTO_URI };
                   // String query = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                   //         + ContactsContract.Contacts.DISPLAY_NAME + " != '' )" + "AND ("+ContactsContract.Contacts.HAS_PHONE_NUMBER +" != '0'"+"))";
                    //Adding filter to get contacts with display picture and display name (just for cool demo)
                    String query = "((" + ContactsContract.Contacts.DISPLAY_NAME + " NOTNULL) AND ("
                            + ContactsContract.Contacts.DISPLAY_NAME + " != '' )" + "AND ("
                            + ContactsContract.Contacts.PHOTO_URI +" != ''"+"))";

                    CursorLoader cursorLoader = new CursorLoader(getActivity(),
                            ContactsContract.Contacts.CONTENT_URI, // URI
                            projectionFields, // projection fields
                            query, // the selection criteria
                            null, // the selection args
                            " DISPLAY_NAME COLLATE LOCALIZED ASC" // the sort order
                    );
                    // Return the loader for use
                    return cursorLoader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    adapter.swapCursor(cursor);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    adapter.swapCursor(null);
                }
            };

}
