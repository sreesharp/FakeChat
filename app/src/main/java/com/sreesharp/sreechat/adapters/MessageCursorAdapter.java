package com.sreesharp.sreechat.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.models.MessageType;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;
import com.sreesharp.sreechat.utilities.Utility;

import java.io.IOException;

/**
 * Created by purayil on 10/17/2015.
 */
public class MessageCursorAdapter extends CursorAdapter {
    public MessageCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor,true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        RelativeLayout msgLayout = (RelativeLayout)view.findViewById(R.id.rlMessage);
        ImageView ivMessage = (ImageView)view.findViewById(R.id.ivMessage);
        // Extract properties from cursor
        String text = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_MESSAGE_TEXT));
        String time = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_MESSAGE_DATE));
        String fromUser = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_FROM_USER_ID));
        String toUser = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.KEY_TO_USER_ID));
        MessageType msgType = MessageType.fromInteger(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.KEY_MESSAGE_TYPE)));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)msgLayout.getLayoutParams();
        if(fromUser.equals(User.getGetCurrentUser().contactId)) { //Current user is the owner of Msg
            params.setMargins(150, 0, 0, 0);
        }else{
            params.setMargins(0, 0, 150, 0);
        }
        msgLayout.setLayoutParams(params);
        tvBody.setText(text);
        tvTime.setText(Utility.getRelativeTimeAgo(time));

        if(msgType == MessageType.Image){
            tvBody.setVisibility(View.GONE);
            ivMessage.setVisibility(view.VISIBLE);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(view.getContext().getContentResolver(), Uri.parse(text));
                ivMessage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
