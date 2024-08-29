package com.afkanerd.deku.DefaultSMS.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Ignore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Contacts {

    @Ignore
    public int type;

    public long id;

    public String number = "";
    public String contactName = "";

    Context context;

    public static final int TYPE_OLD_CONTACT = 1;
    public static final int TYPE_NEW_CONTACT = 2;

    public Contacts(Context context, long id, String contactName, @NonNull String number){
        this.id = id;
        this.contactName = contactName;
        this.context = context;
        this.type = TYPE_OLD_CONTACT;
        this.number = number;

    }

    public Contacts(){ }

    public static Cursor filterContacts(Context context, String filter) {
        if(filter.isEmpty())
            return null;
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " <> '' AND (" +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + filter + "%' OR " +
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + filter + "%')";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    public static Cursor getPhonebookContacts(Context context) {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL AND " +
                ContactsContract.CommonDataKinds.Phone.NUMBER + " <> ''";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
    }

    public static String retrieveContactName(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},
                null,
                null, null);

        if(cursor != null && cursor.moveToFirst()) {
            int displayNameIndex = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String contactName = cursor.getString(displayNameIndex);
            cursor.close();
            return contactName;
        }

        return null;
    }

    public static String retrieveContactPhoto(Context context, String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI},
                null,
                null, null);

        String contactPhotoThumbUri = "";
        if(cursor.moveToFirst()) {
            int displayContactPhoto = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI);
            contactPhotoThumbUri = String.valueOf(cursor.getString(displayContactPhoto));
        }

        return contactPhotoThumbUri;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj != null) {
            Contacts contacts = (Contacts) obj;
            return contacts.number.equals(this.number) &&
                    contacts.contactName.equals(this.contactName);
        }
        return false;
    }

    public static final DiffUtil.ItemCallback<Contacts> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Contacts>() {
                @Override
                public boolean areItemsTheSame(@NonNull Contacts oldItem, @NonNull Contacts newItem) {
                    return oldItem.number.equals(newItem.number);
                }

                @Override
                public boolean areContentsTheSame(@NonNull Contacts oldItem, @NonNull Contacts newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public static Bitmap getContactBitmapPhoto(Context context, String phoneNumber) {
        // Get the bitmap.
        try {
            Uri photoUri = Uri.parse(retrieveContactPhoto(context, phoneNumber));

            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(photoUri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Cursor getBlocked(Context context) {
        return context.getContentResolver().query(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                new String[]{BlockedNumberContract.BlockedNumbers.COLUMN_ID,
                        BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                        BlockedNumberContract.BlockedNumbers.COLUMN_E164_NUMBER},
                null, null, null);
    }
}
