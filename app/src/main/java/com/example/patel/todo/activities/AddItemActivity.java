package com.example.patel.todo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.patel.todo.R;
import com.example.patel.todo.helpers.DatabaseHelper;
import com.example.patel.todo.models.Item;

import java.sql.Date;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    private EditText taskNameEditText;
    private EditText notesEditText;

    private DatePicker dueDatePicker;

    private Spinner prioritySpinner;

    private Button saveButton;

    private ProgressDialog mProgressDialog;

    private boolean isEdit = false;

    private Item toEditItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        taskNameEditText = (EditText) findViewById(R.id.task_name_text_view);
        notesEditText = (EditText) findViewById(R.id.notes_edit_text);

        dueDatePicker = (DatePicker) findViewById(R.id.due_date_picker_view);

        prioritySpinner = (Spinner) findViewById(R.id.priority_spinner);

        saveButton = (Button) findViewById(R.id.save_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.priority_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(adapter);

        dueDatePicker.setMinDate(System.currentTimeMillis() - 1000);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        setTitle("Add Item");

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Saving...");
        mProgressDialog.setCancelable(false);

        if(getIntent().hasExtra("item"))
        {
            isEdit = true;

            toEditItem = (Item) getIntent().getSerializableExtra("item");

            taskNameEditText.setText(toEditItem.getItemTitle());
            notesEditText.setText(toEditItem.getNotes());

            if(toEditItem.getPriority() == Item.Priority.HIGH)
                prioritySpinner.setSelection(0);
            else if(toEditItem.getPriority() == Item.Priority.MEDIUM)
                prioritySpinner.setSelection(1);
            else
                prioritySpinner.setSelection(2);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toEditItem.getFinishDate());

            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            dueDatePicker.updateDate(year, month, day);
        }
    }

    private void addItem()
    {
        if(TextUtils.isEmpty(taskNameEditText.getText()))
        {
            Toast.makeText(AddItemActivity.this, "Please enter task name", Toast.LENGTH_SHORT).show();
            taskNameEditText.requestFocus();
            return;
        }
        else
        {
            mProgressDialog.show();

            Item item = new Item();
            item.setItemTitle(taskNameEditText.getText().toString());
            item.setDone(false);
            System.err.println("added Date "+new Date(Calendar.getInstance().getTimeInMillis()));
            item.setAddedDate(new Date(Calendar.getInstance().getTimeInMillis()));
            item.setNotes(notesEditText.getText().toString().trim());

            if(prioritySpinner.getSelectedItemPosition() == 0)
                item.setPriority(Item.Priority.HIGH);
            else if(prioritySpinner.getSelectedItemPosition() == 1)
                item.setPriority(Item.Priority.MEDIUM);
            else
                item.setPriority(Item.Priority.LOW);

            int day = dueDatePicker.getDayOfMonth();
            int month = dueDatePicker.getMonth();
            int year =  dueDatePicker.getYear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);

            System.err.println("finished Date " + calendar.getTimeInMillis() + " " + new Date(calendar.getTimeInMillis()).getTime());
            item.setFinishDate(new Date(calendar.getTimeInMillis()));

            if(toEditItem != null)
                item.setId(toEditItem.getId());

            new SaveItem().execute(item);
        }
    }

    private class SaveItem extends AsyncTask<Item, Void, Long>
    {
        private Item itemToSave;

        @Override
        protected Long doInBackground(Item... params) {

            itemToSave = params[0];

            DatabaseHelper dbInstance = DatabaseHelper.getInstance(AddItemActivity.this);

            long wasSuccessful;

            if(isEdit)
            {
                wasSuccessful = dbInstance.updateItem(itemToSave);
            }
            else
            {
                wasSuccessful = dbInstance.addItem(itemToSave);
                itemToSave.setId((int)wasSuccessful);
            }

            return wasSuccessful;
        }

        @Override
        protected void onPostExecute(Long wasSuccessful) {
            super.onPostExecute(wasSuccessful);

            mProgressDialog.hide();
            if((isEdit && wasSuccessful > 0) || (!isEdit && wasSuccessful > -1))
            {
                Toast.makeText(AddItemActivity.this, "Item saved successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("addedItem", itemToSave);
                resultIntent.putExtra("isEdit", isEdit);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
            else
            {
                Toast.makeText(AddItemActivity.this, "Item not saved successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
