package com.sreesharp.sreechat;

import android.test.AndroidTestCase;

import com.sreesharp.sreechat.models.ChatUser;
import com.sreesharp.sreechat.models.Message;
import com.sreesharp.sreechat.models.MessageType;
import com.sreesharp.sreechat.models.User;
import com.sreesharp.sreechat.utilities.DbHelper;
import com.sreesharp.sreechat.utilities.Utility;

import junit.framework.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by purayil on 10/17/2015.
 */
public class DbHelperTest  extends AndroidTestCase {


    public void testInitializeDb()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        Assert.assertTrue(dbHelper != null);
    }

    public void testAddUser1()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        User user = new User("12","user1","http://url22");
        ChatUser chUser = new ChatUser();
        chUser.user = user;
        chUser.lastMessage = "testfrom 12";
        long id = dbHelper.addorUpdateUser(chUser);
        Assert.assertTrue(id != -1);
    }


    public void testAddUser2()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        User user = new User("13","user2","http://url");
        ChatUser chUser = new ChatUser();
        chUser.user = user;
        chUser.lastMessage = "testfrom 12";
        long id = dbHelper.addorUpdateUser(chUser);
        Assert.assertTrue(id != -1);
    }

    public void testAddMessage1()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        Message msg = new Message();
        msg.fromUser = "12";
        msg.toUser = "13";
        msg.text = "First message";
        msg.type = MessageType.Text;
        msg.date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        long id = dbHelper.addMessage(msg);
        Assert.assertTrue(id != -1);
    }

    public void testAddMessage2()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        Message msg = new Message();
        msg.fromUser = "13";
        msg.toUser = "12";
        msg.text = "Second message";
        msg.type = MessageType.Text;
        msg.date =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        long id = dbHelper.addMessage(msg);
        Assert.assertTrue(id != -1);
    }


    public void testGetAllMessages()  {
        DbHelper dbHelper = DbHelper.getInstance(getContext());

        List<Message> msgs = dbHelper.getAllMessages("12","13");
        for(Message msg: msgs){
            System.out.println(msg.fromUser + "->" +msg.toUser + " type " + msg.type + " "+  msg.text + " on " + Utility.formatDateTime(getContext(), msg.date));
        }
        Assert.assertTrue(msgs.size() != 0);
    }

}
