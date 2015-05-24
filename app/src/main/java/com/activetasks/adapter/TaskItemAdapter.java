package com.activetasks.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.helper.DateHelper;
import com.activetasks.pojo.TaskItem;

import java.util.List;

public class TaskItemAdapter extends ArrayAdapter<TaskItem> {

	private Activity activity;
	private List<TaskItem> items;
	private TaskItem taskItem;
	private int row;

	public TaskItemAdapter(Activity act, List<TaskItem> arrayList) {
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
			view = inflater.inflate(R.layout.layout_list_task_item, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

        taskItem = items.get(position);

		holder.description = (TextView) view.findViewById(R.id.tvTaskItemContent);
        holder.assignedName = (TextView) view.findViewById(R.id.tvTaskItemPerson);
        holder.dateStart = (TextView) view.findViewById(R.id.tvTaskItemDateStart);
        holder.dateEnd = (TextView) view.findViewById(R.id.tvTaskItemDateEnd);

		if (holder.description != null && null != taskItem.getDescription()
				&& taskItem.getDescription().trim().length() > 0) {
			holder.description.setText(Html.fromHtml(taskItem.getDescription()));
		}
        Log.d("null checking", ((null != holder.assignedName)  + " , " + (null != taskItem.getAssignedName())
                + " , " + (taskItem.getAssignedName().trim().length() > 0)));

        if (null != holder.assignedName  && null != taskItem.getAssignedName()
                && taskItem.getAssignedName().trim().length() > 0) {
            holder.assignedName.setText(Html.fromHtml(taskItem.getAssignedName()));
        }

        if (
                holder.dateStart != null && holder.dateEnd != null &&
                        taskItem.getStartDate() != null &&
                        taskItem.getEndDate() != null &&
                        taskItem.getStartDate().trim().length() > 0 &&
                        taskItem.getEndDate().trim().length() > 0) {
            holder.dateStart.setText(Html.fromHtml(DateHelper.formatStringDate(taskItem.getStartDate())) + " (start) ");
            holder.dateEnd.setText(Html.fromHtml(DateHelper.formatStringDate(taskItem.getEndDate())) + " (end) ");
        }

		return view;
	}

	public class ViewHolder {
        public TextView description;
        public TextView assignedName;
        public TextView dateStart;
        public TextView dateEnd;
	}

}