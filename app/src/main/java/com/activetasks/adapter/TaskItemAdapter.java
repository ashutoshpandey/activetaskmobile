package com.activetasks.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.pojo.GroupMember;
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
			view = inflater.inflate(R.layout.layout_list_group_member, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

        taskItem = items.get(position);

		holder.content = (TextView) view.findViewById(R.id.tvTaskItemContent);
        holder.person = (TextView) view.findViewById(R.id.tvTaskItemPerson);
        holder.date = (TextView) view.findViewById(R.id.tvTaskItemDate);

		if (holder.content != null && null != taskItem.getContent()
				&& taskItem.getContent().trim().length() > 0) {
			holder.content.setText(Html.fromHtml(taskItem.getContent()));
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
        public TextView content;
        public TextView person;
        public TextView date;
	}

}