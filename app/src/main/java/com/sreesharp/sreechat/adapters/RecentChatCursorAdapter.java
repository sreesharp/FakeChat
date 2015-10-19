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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;
import com.sreesharp.sreechat.utilities.Utility;

/**
 * Created by purayil on 10/18/2015.
 */
 public class RecentChatCursorAdapter extends CursorAdapter {
        public RecentChatCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor,true);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.item_recentchat, parent, false);
        }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //super.bindView(view, context, cursor);
        TextView tvName = (TextView) view.findViewById(R.id.tvContactName);
        SimpleDraweeView ivProfile = (SimpleDraweeView) view.findViewById(R.id.ivProfile);
        TextView tvRecentMsg = (TextView) view.findViewById(R.id.tvRecentMsg);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);

        // Extract properties from cursor
        String contactName = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_USER_NAME));
        String profileURL = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_USER_PHOTO_URL));
        String contactId = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_CONTACT_ID));
        String msg = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_MESSAGE_TEXT));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_MESSAGE_DATE));


        //Fix - https://github.com/facebook/fresco/issues/97
        profileURL = profileURL.replace("/photo","");
        tvName.setText(contactName);
        ivProfile.setImageURI(Uri.parse(profileURL));
        tvRecentMsg.setText(msg);
        tvTime.setText(Utility.getRelativeTimeAgo(date));;
    }


}