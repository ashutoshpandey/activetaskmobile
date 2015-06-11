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
import com.activetasks.pojo.Group;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<Group> {

	private Activity activity;
	private List<Group> items;
	private Group group;
	private int row;

	public GroupAdapter(Activity act, List<Group> arrayList) {
        super(act, R.layout.layout_list_group, arrayList);
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
			view = inflater.inflate(R.layout.layout_list_group, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		group = items.get(position);

		holder.name = (TextView) view.findViewById(R.id.tvname);

		if (holder.name != null && null != group.getName()
				&& group.getName().trim().length() > 0) {
			holder.name.setText(Html.fromHtml(group.getName()));
		}

		return view;
	}

	public class ViewHolder {
        public TextView name;
	}

}