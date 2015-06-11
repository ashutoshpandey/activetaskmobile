package com.activetasks.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import activetasks.activetasks.R;
import com.activetasks.pojo.TaskUpdateItem;

import java.util.List;

public class TaskDataUpdateAdapter extends ArrayAdapter<TaskUpdateItem> {

	private Activity activity;
	private List<TaskUpdateItem> items;
	private TaskUpdateItem taskItem;
	private int row;

	public TaskDataUpdateAdapter(Activity act, List<TaskUpdateItem> arrayList) {
        super(act, R.layout.layout_task_data_item, arrayList);
		this.activity = act;
		this.items = arrayList;
	}

    int count = 0;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.layout_task_data_item, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

        taskItem = items.get(position);

		holder.description = (TextView) view.findViewById(R.id.tvTaskItemContent);
        holder.date = (TextView) view.findViewById(R.id.tvTaskItemDate);

		if (holder.description != null && null != taskItem.getDescription()
				&& taskItem.getDescription().trim().length() > 0) {
			holder.description.setText(Html.fromHtml(taskItem.getDescription()));
		}

        if (
                holder.date != null &&
                        taskItem.getStartDate() != null &&
                        taskItem.getEndDate() != null &&
                        taskItem.getStartDate().trim().length() > 0 &&
                        taskItem.getEndDate().trim().length() > 0) {
            holder.date.setText(Html.fromHtml(taskItem.getStartDate()) + " - " + Html.fromHtml(taskItem.getEndDate()));
        }

		return view;
	}

	public class ViewHolder {
        public TextView description;
        public TextView date;
	}

}