package com.sreesharp.sreechat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.models.User;

/**
 * Created by purayil on 10/16/2015.
 */
public class ContactsCursorAdapter extends SimpleCursorAdapter {

    public ContactsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context,layout,c, from,to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //super.bindView(view, context, cursor);
        TextView tvName = (TextView) view.findViewById(R.id.tvContactName);
        SimpleDraweeView ivProfile = (SimpleDraweeView) view.findViewById(R.id.ivProfile);
        // Extract properties from cursor
        String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String profileURL = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI));
        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

        //Fix - https://github.com/facebook/fresco/issues/97
        profileURL = profileURL.replace("/photo","");
        tvName.setText(contactName);
        ivProfile.setImageURI(Uri.parse(profileURL));
    }


}
