package com.activetasks.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.pojo.GroupMember;

import java.util.List;

public class MemberAdapter extends ArrayAdapter<GroupMember> {

	private Activity activity;
	private List<GroupMember> items;
	private GroupMember groupMember;
	private int row;

	public MemberAdapter(Activity act, List<GroupMember> arrayList) {
        super(act, R.layout.layout_list_group_member, arrayList);
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

		groupMember = items.get(position);

		holder.name = (TextView) view.findViewById(R.id.tvMember);
        holder.checkMember = (CheckBox) view.findViewById(R.id.chkMember);

		if (holder.name != null && null != groupMember.getName()
				&& groupMember.getName().trim().length() > 0) {
			holder.name.setText(Html.fromHtml(groupMember.getName()));
		}

		return view;
	}

	public class ViewHolder {
        public CheckBox checkMember;
        public TextView name;
	}

}