package com.sreesharp.sreechat.models;

import com.sreesharp.sreechat.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by purayil on 10/17/2015.
 */
public class Message {
    public String Id;
    public String fromUser;
    public String toUser;
    public String text;
    public String date; //yyyy-MM-dd HH:mm:ss
    public MessageType type;

    public Message(){

    }

    public Message(String fromUserId, String toUserId, String text){
        initialize(fromUserId, toUserId, text, MessageType.Text);
    }

    public Message(String fromUserId, String toUserId, String text, MessageType type){
        initialize(fromUserId, toUserId, text, type);

    }

    private void initialize(String fromUserId, String toUserId, String text, MessageType type) {
        this.fromUser = fromUserId;
        this.toUser = toUserId;
        this.text = text;
        this.type = type;
        this.date =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }


}
