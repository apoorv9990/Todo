package com.example.patel.todo.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.patel.todo.R;
import com.example.patel.todo.models.Item;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by patel on 6/14/2016.
 */
public class ItemAdapter extends ArrayAdapter<Item> {

    private List<Item> items;

    private Context mContext;

    private SimpleDateFormat mDateFormat;

    public ItemAdapter(Context context, int resourceID, List<Item> items)
    {
        super(context, resourceID);

        this.items = items;
        mContext = context;

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;

        if(convertView == null)
        {
            // inflate the layout
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_row_layout, parent, false);

            // well set up the ViewHolder
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.itemTitleTextView = (TextView) convertView.findViewById(R.id.item_title_text_view);
            viewHolderItem.dueDateTextView = (TextView) convertView.findViewById(R.id.due_date_text_view);
            viewHolderItem.priorityTextView = (TextView) convertView.findViewById(R.id.priority_text_view);

            // store the holder with the view.
            convertView.setTag(viewHolderItem);
        }
        else
        {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }

        Item currentItem = items.get(position);

        if(currentItem != null)
        {
            viewHolderItem.itemTitleTextView.setText(currentItem.getItemTitle());
            viewHolderItem.dueDateTextView.setText("Due Date: " + mDateFormat.format(currentItem.getFinishDate()));
            viewHolderItem.priorityTextView.setText(currentItem.getPriority().toString());

            switch (currentItem.getPriority().toString())
            {
                case "HIGH":
                    viewHolderItem.priorityTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPriorityHigh));
                    break;
                case "LOW":
                    viewHolderItem.priorityTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPriorityLow));
                    break;
                default:
                    viewHolderItem.priorityTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorPriorityMedium));
                    break;
            }
        }

        return convertView;
    }

    private class ViewHolderItem {
        TextView itemTitleTextView;
        TextView dueDateTextView;
        TextView priorityTextView;
    }

}
