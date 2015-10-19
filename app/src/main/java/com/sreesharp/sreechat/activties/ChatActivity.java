package com.sreesharp.sreechat.activties;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.adapters.MessageCursorAdapter;
import com.sreesharp.sreechat.models.ChatUser;
import com.sreesharp.sreechat.models.Message;
import com.sreesharp.sreechat.models.MessageType;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;
import com.sreesharp.sreechat.utilities.Utility;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity {

    private EditText etPostMessage;
    private ImageView ivPostMessage;
    private ListView lvMessageList;
    private MessageCursorAdapter messageAdapter;
    //Limit for the last message in chat list
    private static final int MESSAGE_LENGTH_LIMIT = 30;
    private int PICK_IMAGE_REQUEST = 1;
    User userFrom, userTo;
    ChatUser chatUser = new ChatUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        lvMessageList = (ListView)findViewById(R.id.lvMessageList);
        etPostMessage = (EditText)findViewById(R.id.etPostMessage);
        ivPostMessage = (ImageView)findViewById(R.id.ivPostMessage);

        userFrom = User.getGetCurrentUser();
        userTo = (User) getIntent().getSerializableExtra("userTo");
        chatUser.user = userTo;

        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(userTo.displayName);
        getSupportActionBar().setSubtitle("online");
   /*
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(userTo.photoUrl));
            Resources res = getResources();
            BitmapDrawable icon = new BitmapDrawable(res,bitmap);
            getSupportActionBar().setIcon(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        ivPostMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etPostMessage.getText().toString();
                if(message !=null  &&  !message.isEmpty()) {
                    postMessage(message);
                    etPostMessage.setText(null);
                }
            }
        });

        messageAdapter = new MessageCursorAdapter(this, DbHelper.getInstance(this).getAllMessagesCursor(userTo.contactId, userFrom.contactId));
        lvMessageList.setAdapter(messageAdapter);

    }

    private void postMessage(String message, MessageType type) {
        Message msg = new Message(userFrom.contactId,userTo.contactId, message);
        msg.type = type;
        PersistMessage(msg);
        new ReplyBot().execute(msg);
        refresh();
    }
    private void postMessage(String message) {
        postMessage(message, MessageType.Text);
    }

    private void PersistMessage(Message msg){
        //Add to the message table
        DbHelper.getInstance(this).addMessage(msg);
        //Update the user table
        String lastMessage = msg.text;
        if(lastMessage.length() > MESSAGE_LENGTH_LIMIT) {
            lastMessage = lastMessage.substring(0, MESSAGE_LENGTH_LIMIT);
            lastMessage = lastMessage + "...";
        }
        if(msg.type == MessageType.Text)
            chatUser.lastMessage = lastMessage;
        else
            chatUser.lastMessage ="<<image>>";
        chatUser.lastMessageDate = msg.date;
        DbHelper.getInstance(this).addorUpdateUser(chatUser);
    }

    public void refresh(){
        messageAdapter.changeCursor(DbHelper.getInstance(this).getAllMessagesCursor(userTo.contactId, userFrom.contactId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_attach:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                break;
            // action with ID action_settings was selected
            case R.id.action_settings:
                Toast.makeText(this, "Not yet implemented!", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String photoUrl = uri.toString();
            postMessage(photoUrl, MessageType.Image);
        }
    }

    private void PostReply(Message msg)
    {
        PersistMessage(msg);
        refresh();
    }

    public class ReplyBot extends AsyncTask<Message,Integer, Message > {

        @Override
        protected void onPreExecute() {
            getSupportActionBar().setSubtitle("typing...");
        }

        @Override
        protected void onPostExecute(Message message) {
            PostReply(message);
            getSupportActionBar().setSubtitle("online");
        }

        @Override
        protected Message doInBackground(Message... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Message msgIn = params[0];
            String replyText = Utility.getRandomTexts(msgIn.type);

            Message msgOut = new Message(msgIn.toUser, msgIn.fromUser,replyText);
            return msgOut;
        }
    }
}
