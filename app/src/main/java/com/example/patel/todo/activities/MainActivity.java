package com.example.patel.todo.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.patel.todo.R;
import com.example.patel.todo.adapters.ItemAdapter;
import com.example.patel.todo.helpers.DatabaseHelper;
import com.example.patel.todo.models.Item;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Item> items = new ArrayList<>();

    private TextView noItemsTextView;

    private ListView itemsListView;

    private ProgressDialog mProgressDialog;

    private ItemAdapter mItemAdapter;

    private int clickedItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsListView = (ListView) findViewById(R.id.items_list_view);

        noItemsTextView = (TextView) findViewById(R.id.no_items_text_view);

        setTitle("Items");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Retrieving...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        setupItemClickListener();
        setUpItemLongClickListener();

        new GetItems().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:

                navigateToAddItem();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setupItemClickListener()
    {
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clickedItem = position;

                Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                intent.putExtra("item", items.get(position));
                startActivityForResult(intent, 0);
            }
        });
    }

    private void setUpItemLongClickListener()
    {
        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                final int deletePosition = position;

                builder.setTitle("Delete Item?");
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mProgressDialog.show();

                        new DeleteItem().execute(deletePosition);

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog deleteDialog = builder.create();
                deleteDialog.show();

                return true;
            }
        });
    }

    private void navigateToAddItem()
    {
        Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                if(data.hasExtra("addedItem")) {

                    boolean isEdit = false;

                    if(data.hasExtra("isEdit"))
                        isEdit = data.getBooleanExtra("isEdit", false);

                    Item addedItem = (Item) data.getSerializableExtra("addedItem");

                    if(isEdit) {
                        items.set(clickedItem, addedItem);
                    }
                    else {
                        items.add(addedItem);
                    }

                    if(mItemAdapter == null)
                    {
                        mItemAdapter = new ItemAdapter(MainActivity.this, R.layout.item_row_layout, items);
                        itemsListView.setAdapter(mItemAdapter);
                    }

                    if(items.size() > 0)
                    {
                        noItemsTextView.setVisibility(View.GONE);
                        itemsListView.setVisibility(View.VISIBLE);
                    }

                    mItemAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class GetItems extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);

            items = dbHelper.readItems();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressDialog.hide();

            if(items.size() > 0)
            {
                noItemsTextView.setVisibility(View.GONE);
                itemsListView.setVisibility(View.VISIBLE);

                mItemAdapter = new ItemAdapter(MainActivity.this, R.layout.item_row_layout, items);
                itemsListView.setAdapter(mItemAdapter);
            }
        }
    }

    private class DeleteItem extends AsyncTask<Integer, Void, Void>
    {
        private int deletePosition;

        @Override
        protected Void doInBackground(Integer... params) {

            deletePosition = params[0];
            Item toDeleteItem = items.get(params[0]);

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(MainActivity.this);
            dbHelper.deleteItem(toDeleteItem);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            items.remove(deletePosition);

            mItemAdapter.notifyDataSetChanged();

            mProgressDialog.hide();

            if (items.size() == 0) {
                itemsListView.setVisibility(View.GONE);
                noItemsTextView.setVisibility(View.VISIBLE);
            }
        }
    }
}
