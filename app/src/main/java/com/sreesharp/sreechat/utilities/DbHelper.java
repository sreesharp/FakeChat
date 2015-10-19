package com.sreesharp.sreechat.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sreesharp.sreechat.models.ChatUser;
import com.sreesharp.sreechat.models.Message;
import com.sreesharp.sreechat.models.MessageType;
import com.sreesharp.sreechat.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purayil on 10/17/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "fakeChatDb";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_USERS = "users";

    // Message Table Columns
    private static final String KEY_MSG_ID = "id";
    public static final String KEY_FROM_USER_ID = "fromUser";
    public static final String KEY_TO_USER_ID = "toUser";
    public static final String KEY_MESSAGE_TEXT = "text";
    public static final String KEY_MESSAGE_TYPE = "type";
    public static final String KEY_MESSAGE_DATE = "date";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    public static final String KEY_CONTACT_ID = "contactId";
    public static final String KEY_USER_NAME = "displayName";
    public static final String KEY_USER_PHOTO_URL = "photoUrl";

    private static DbHelper sInstance;

    public static synchronized DbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_MESSAGES +
                "(" +
                KEY_MSG_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_FROM_USER_ID + " TEXT," +
                KEY_TO_USER_ID + " TEXT," +
                KEY_MESSAGE_TEXT + " TEXT," +
                KEY_MESSAGE_DATE + " TEXT," +
                KEY_MESSAGE_TYPE + " INTEGER" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " INTEGER PRIMARY KEY," +
                KEY_CONTACT_ID + " TEXT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PHOTO_URL + " TEXT," +
                KEY_MESSAGE_TEXT + " TEXT," +
                KEY_MESSAGE_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }




    // Insert or update a user in the database
    public long addorUpdateUser(ChatUser chatUser) {
        SQLiteDatabase db = getWritableDatabase();
        long userId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CONTACT_ID, chatUser.user.contactId);
            values.put(KEY_USER_NAME, chatUser.user.displayName);
            values.put(KEY_USER_PHOTO_URL, chatUser.user.photoUrl);
            values.put(KEY_MESSAGE_DATE, chatUser.lastMessageDate);
            values.put(KEY_MESSAGE_TEXT, chatUser.lastMessage);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_USERS, values, KEY_CONTACT_ID + "= ?", new String[]{chatUser.user.contactId});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_USER_ID, TABLE_USERS, KEY_CONTACT_ID);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(chatUser.user.contactId)});
                try {
                    if (cursor.moveToFirst()) {
                        userId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                userId = db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("DB_ERROR", "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }


    public Cursor getRecentChatsCursor() {
        String SELECT_QUERY =
                String.format("SELECT %s as _id,%s,%s,%s,%s,%s FROM %s ORDER BY %s DESC",
                        KEY_USER_ID, KEY_CONTACT_ID, KEY_USER_NAME, KEY_USER_PHOTO_URL, KEY_MESSAGE_TEXT,
                        KEY_MESSAGE_DATE, TABLE_USERS, KEY_MESSAGE_DATE);

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(SELECT_QUERY, null);
    }

    // Insert a Message into the database
    public long addMessage(Message msg) {
        long msgId = -1;
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_FROM_USER_ID, msg.fromUser);
            values.put(KEY_TO_USER_ID, msg.toUser);
            values.put(KEY_MESSAGE_TEXT, msg.text);
            values.put(KEY_MESSAGE_TYPE, msg.type.ordinal());
            values.put(KEY_MESSAGE_DATE, msg.date);

            msgId = db.insertOrThrow(TABLE_MESSAGES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB_ERROR", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }

        return msgId;
    }

    // Get chat messages between given user ids
    public List<Message> getAllMessages(String userId1, String userId2) {
        List<Message> msgs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = getAllMessagesCursor(userId1,userId2);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Message msg = new Message();
                   // msg.Id = cursor.getString(cursor.getColumnIndex(KEY_MSG_ID));
                    msg.fromUser = cursor.getString(cursor.getColumnIndex(KEY_FROM_USER_ID));
                    msg.toUser = cursor.getString(cursor.getColumnIndex(KEY_TO_USER_ID));
                    msg.text = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_TEXT));
                    msg.type = MessageType.fromInteger(cursor.getInt(cursor.getColumnIndex(KEY_MESSAGE_TYPE)));
                    msg.date = cursor.getString(cursor.getColumnIndex(KEY_MESSAGE_DATE));
                    msgs.add(msg);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("DB_ERROR", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return msgs;
    }

    public Cursor getAllMessagesCursor(String userId1, String userId2) {
        String SELECT_QUERY =
                String.format("SELECT %s as _id,%s,%s,%s,%s,%s FROM %s WHERE (%s = '%s' AND %s = '%s') OR (%s = '%s' AND %s = '%s')",
                        KEY_MSG_ID,KEY_FROM_USER_ID, KEY_TO_USER_ID, KEY_MESSAGE_TEXT,KEY_MESSAGE_TYPE,
                        KEY_MESSAGE_DATE, TABLE_MESSAGES, KEY_FROM_USER_ID,userId1,KEY_TO_USER_ID,userId2,
                        KEY_FROM_USER_ID,userId2,KEY_TO_USER_ID,userId1);

        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(SELECT_QUERY, null);
    }




    // Delete all messages and users in the database
    public void deleteAllMessagesAndUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_MESSAGES, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("DB_ERROR", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }
}
