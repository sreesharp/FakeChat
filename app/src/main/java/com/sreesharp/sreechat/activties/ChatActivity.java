package com.sreesharp.sreechat.activties;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sreesharp.sreechat.R;
import com.sreesharp.sreechat.adapters.MessageCursorAdapter;
import com.sreesharp.sreechat.models.ChatUser;
import com.sreesharp.sreechat.models.Message;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;

public class ChatActivity extends AppCompatActivity {

    private EditText etPostMessage;
    private ImageView ivPostMessage;
    private ListView lvMessageList;
    private MessageCursorAdapter messageAdapter;
    //Limit for the last message in chat list
    private static final int MESSAGE_LENGTH_LIMIT = 30;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(userTo.displayName);
        //TODO: Fix this to see the user profile pic on Action bar
        //getSupportActionBar().setIcon(userTo.photoUrl);

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

    private void postMessage(String message) {
        Message msg = new Message(userFrom.contactId,userTo.contactId, message);
        PersistMessage (msg);
        informService(msg);
        refresh();
    }

    //Inform service is to send the message to other party
    private void informService(Message msg)
    {
        Message reply = new Message(msg.toUser,msg.fromUser, msg.text);
        PersistMessage(reply);
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
        chatUser.lastMessage = lastMessage;
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
