package com.example.dbapp.contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "contactManager",
    TABLE_CONTACTS = "contacts",
    KEY_ID = "id",
    KEY_NAME = "name",
    KEY_PHONE = "phone",
    KEY_ADDRESS = "address",
    KEY_EMAIL = "email",
    KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT," + KEY_ADDRESS + " TEXT," + KEY_IMAGEURI + " TEXT, " + KEY_EMAIL + " TEXT " + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }


    //CRUD

    //Create new contact and add it to the database
    public void createContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_EMAIL, contact.getEmail());
//        values.put(KEY_IMAGEURI, contact.getImageURI().toString());

        db.insert(TABLE_CONTACTS, null, values);

        db.close();

    }

    //Read single contact row. It accepts id as parameter and will return matched row from the database
    public Contact getContact(int id){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS,new String[]{KEY_ID, KEY_NAME, KEY_PHONE, KEY_ADDRESS, KEY_EMAIL}, KEY_ID + "=?", new String[]{ String.valueOf(id) }, null, null,null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        db.close();
        cursor.close();

        return contact;
    }

    //  Get the total number of records in the table
    public int getContactsCount(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        return count;
    }

    //    Update single contact in the database. Use contact instance as parameter
    public int updateContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        // Set new row's values
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_ADDRESS, contact.getAddress());
        values.put(KEY_EMAIL, contact.getEmail());

    //  update this row by id
        return db.update(TABLE_CONTACTS, values, KEY_ID + "=?", new String[]{ String.valueOf(contact.getId()) });

    }


    //    Delete single contact from database
    public void deleteContact(Contact contact){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + "=?", new String[]{ String.valueOf(contact.getId()) });
        db.close();
    }

    public void deleteAllContacts(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();
    }


    //Get All Contacts
    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<Contact>();
//    Why get all contacts need a writable database?????
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);

//        Loop through all rows and adding to list
        if (cursor.moveToFirst()){
            do {
                Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

                contacts.add(contact);

            }while (cursor.moveToNext());
        }

        return contacts;
    }


}
