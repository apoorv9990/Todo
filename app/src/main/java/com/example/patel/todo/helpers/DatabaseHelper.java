package com.example.patel.todo.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.patel.todo.models.Item;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by patel on 6/13/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLiteOpenHelper";

    // database info
    private static final String DATABASE_NAME = "ToDoDatabase";
    private static final int DATABASE_VERSION = 1;

    // table names
    private static final String TABLE_ITEMS = "items";

    // columns for TABLE_ITEMS table
    private static final String KEY_ITEM_ID = "id";
    private static final String KEY_ITEM_TEXT = "text";
    private static final String KEY_ITEM_ADDED_DATE = "addedDate";
    private static final String KEY_ITEM_DUE_DATE = "dueDate";
    private static final String KEY_ITEM_PRIORITY = "priority";
    private static final String KEY_ITEM_DONE = "done";
    private static final String KEY_ITEM_NOTES = "notes";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS +
                "(" +
                KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                KEY_ITEM_TEXT + " TEXT," +
                KEY_ITEM_ADDED_DATE + " TEXT," +
                KEY_ITEM_DUE_DATE + " TEXT," +
                KEY_ITEM_PRIORITY + " TEXT," +
                KEY_ITEM_DONE + " INT," +
                KEY_ITEM_NOTES + " TEXT" +
                ")";

        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }
    }

    public long addItem(Item item)
    {
        long wasInserted = -1;

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, item.getItemTitle());
            values.put(KEY_ITEM_ADDED_DATE, String.valueOf(item.getAddedDate().getTime()));
            values.put(KEY_ITEM_DUE_DATE, String.valueOf(item.getFinishDate().getTime()));

            if(item.isDone())
            {
                values.put(KEY_ITEM_DONE, 1);
            }
            else
            {
                values.put(KEY_ITEM_DONE, 0);
            }

            values.put(KEY_ITEM_PRIORITY, String.valueOf(item.getPriority()));
            values.put(KEY_ITEM_NOTES, item.getNotes());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            wasInserted = db.insertOrThrow(TABLE_ITEMS, null, values) ;
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
            System.err.println(e.getMessage());
        } finally {
            db.endTransaction();
        }

        return wasInserted;
    }

    public long updateItem(Item item)
    {
        long wasInserted = -1;

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_ITEM_TEXT, item.getItemTitle());
            values.put(KEY_ITEM_ADDED_DATE, String.valueOf(item.getAddedDate().getTime()));
            values.put(KEY_ITEM_DUE_DATE, String.valueOf(item.getFinishDate().getTime()));

            if(item.isDone())
            {
                values.put(KEY_ITEM_DONE, 1);
            }
            else
            {
                values.put(KEY_ITEM_DONE, 0);
            }

            values.put(KEY_ITEM_PRIORITY, String.valueOf(item.getPriority()));
            values.put(KEY_ITEM_NOTES, item.getNotes());

            wasInserted = db.update(TABLE_ITEMS, values, KEY_ITEM_ID + "=" + item.getId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
            System.err.println(e.getMessage());
        } finally {
            db.endTransaction();
        }

        return wasInserted;
    }

    public void deleteItem(Item item)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_ITEMS,KEY_ITEM_ID + "=" + item.getId(),null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

    public List<Item> readItems()
    {
        List<Item> items = new ArrayList<>();

        String ITEMS_SELECT_QUERY =
                String.format("SELECT * FROM %s ", TABLE_ITEMS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ITEMS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Item newItem = new Item();

                    newItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_ID)));
                    newItem.setItemTitle(cursor.getString(cursor.getColumnIndex(KEY_ITEM_TEXT)));
                    System.err.println("finished date " + cursor.getString(cursor.getColumnIndex(KEY_ITEM_ADDED_DATE)) + " " + Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_ITEM_ADDED_DATE))));
                    newItem.setAddedDate(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_ITEM_ADDED_DATE)))));
                    newItem.setFinishDate(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_ITEM_DUE_DATE)))));
                    newItem.setNotes(cursor.getString(cursor.getColumnIndex(KEY_ITEM_NOTES)));

                    if(cursor.getInt(cursor.getColumnIndex(KEY_ITEM_DONE)) == 1)
                    {
                        newItem.setDone(true);
                    }
                    else
                    {
                        newItem.setDone(false);
                    }

                    switch (cursor.getString(cursor.getColumnIndex(KEY_ITEM_PRIORITY)))
                    {
                        case "HIGH":
                            newItem.setPriority(Item.Priority.HIGH);
                            break;
                        case "LOW":
                            newItem.setPriority(Item.Priority.LOW);
                            break;
                        default:
                            newItem.setPriority(Item.Priority.MEDIUM);
                            break;
                    }

                    newItem.setNotes(cursor.getString(cursor.getColumnIndex(KEY_ITEM_NOTES)));

                    items.add(newItem);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return items;
    }
}
