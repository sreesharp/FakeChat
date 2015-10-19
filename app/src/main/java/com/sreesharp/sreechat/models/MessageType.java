package com.sreesharp.sreechat.models;

/**
 * Created by purayil on 10/17/2015.
 */
public enum MessageType {

    Text,
    Image,
    Video,
    Audio;

    public static MessageType fromInteger(int x) {
        switch(x) {
            case 0:
                return Text;
            case 1:
                return Image;
            case 2:
                return Video;
            case 3:
                return Audio;
        }
        return null;
    }
}
