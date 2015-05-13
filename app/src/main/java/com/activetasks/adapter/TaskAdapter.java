package com.activetasks.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.activetasks.activetasks.R;

import com.activetasks.pojo.Task;

public class TaskAdapter extends ArrayAdapter<Task> {

	private Activity activity;
	private List<Task> items;
	private Task task;

	public TaskAdapter(Activity act, List<Task> arrayList) {
        super(act, R.layout.layout_list_task, arrayList);
		this.activity = act;
		this.items = arrayList;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.layout_list_task, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		task = items.get(position);

		holder.title = (TextView) view.findViewById(R.id.tvtitle);
		holder.description = (TextView) view.findViewById(R.id.tvdesc);
		holder.date = (TextView) view.findViewById(R.id.tvdate);
		holder.statusImage = (ImageView) view.findViewById(R.id.image);

		if (holder.title != null && null != task.getName()
				&& task.getName().trim().length() > 0) {
			holder.title.setText(Html.fromHtml(task.getName()));
		}
		if (holder.description != null && null != task.getDescription()
				&& task.getDescription().trim().length() > 0) {
			holder.description.setText(Html.fromHtml(task.getDescription()));
		}
		if (
                holder.date != null &&
                task.getStartDate() != null &&
                task.getEndDate() != null &&
                task.getStartDate().trim().length() > 0 &&
                task.getEndDate().trim().length() > 0) {
			        holder.date.setText(Html.fromHtml(task.getStartDate()) + " - " + Html.fromHtml(task.getEndDate()));
		}
		if (holder.statusImage != null) {
    		holder.statusImage.setImageResource(R.drawable.ic_launcher);
		}

		return view;
	}

	public class ViewHolder {

        public TextView title;
        public TextView description;
        public TextView date;
        public ImageView statusImage;
	}

}