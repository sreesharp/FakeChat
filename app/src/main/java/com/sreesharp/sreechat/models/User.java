package com.sreesharp.sreechat.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.Serializable;

/**
 * Created by purayil on 10/17/2015.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 517222050535318633L;
    public String contactId;
    public String displayName;
    public String photoUrl;

    private static User currentUser = null;

    public User(){}

    public User(String id, String name, String photo){
        contactId = id;
        displayName = name;
        photoUrl = photo;
    }



    public static User getGetCurrentUser() {
        if (currentUser != null)
            return currentUser;
        else
            return null;
    }

    //TODO: Temporary method. Will be removed once I fix getGetCurrentUser issue
    public static void setCurrentUser(User user) {
        if (currentUser == null)
            currentUser = user;
    }

    //TODO: Fix this method, not working as expected
    private static User getGetCurrentUser(Context context) {
        if(currentUser != null)
            return currentUser;
        final ContentResolver content = context.getContentResolver();
        final Cursor cursor = content.query(
                // Retrieves data rows for the device user's 'profile' contact
                Uri.withAppendedPath(
                        ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,

                // Selects only email addresses or names
                ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                },

                // Show primary rows first. Note that there won't be a primary email address if the
                // user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"
        );

        final User user = new User();
        String mime_type;
        while (cursor.moveToNext()) {
            mime_type = cursor.getString(ProfileQuery.MIME_TYPE);
            if (mime_type.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE))

            {}
            else if (mime_type.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE))
                user.displayName = cursor.getString(ProfileQuery.GIVEN_NAME) + " " + cursor.getString(ProfileQuery.FAMILY_NAME);
            else if (mime_type.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {

            }
            else if (mime_type.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE))
                user.photoUrl = cursor.getString(ProfileQuery.PHOTO);
        }

        cursor.close();

        return user;
    }

    private interface ProfileQuery {
        /** The set of columns to extract from the profile query results */
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
                ContactsContract.Contacts.Data.MIMETYPE
        };

        /** Column index for the email address in the profile query results */
        int EMAIL = 0;
        /** Column index for the primary email address indicator in the profile query results */
        int IS_PRIMARY_EMAIL = 1;
        /** Column index for the family name in the profile query results */
        int FAMILY_NAME = 2;
        /** Column index for the given name in the profile query results */
        int GIVEN_NAME = 3;
        /** Column index for the phone number in the profile query results */
        int PHONE_NUMBER = 4;
        /** Column index for the primary phone number in the profile query results */
        int IS_PRIMARY_PHONE_NUMBER = 5;
        /** Column index for the photo in the profile query results */
        int PHOTO = 6;
        /** Column index for the MIME type in the profile query results */
        int MIME_TYPE = 7;
    }
}
