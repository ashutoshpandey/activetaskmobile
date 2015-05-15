package com.activetasks.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.activetasks.activetasks.R;
import com.activetasks.pojo.GroupMember;
import com.activetasks.pojo.Contact;

import java.util.HashSet;
import java.util.List;

public class PeopleAdapter extends ArrayAdapter<Contact> {

	private Activity activity;
	private List<Contact> items;
	private Contact people;
	private HashSet<Integer> selectedPeople = new HashSet<>();

	public PeopleAdapter(Activity act, List<Contact> arrayList) {
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

		people = items.get(position);

		holder.name = (TextView) view.findViewById(R.id.tvMember);
        holder.checkMember = (CheckBox) view.findViewById(R.id.chkMember);
        holder.checkMember.setTag(people);

        holder.checkMember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Integer memberId = ((GroupMember)((CheckBox)buttonView).getTag()).getId();

                if(isChecked)
                    selectedPeople.add(memberId);
                else
                    selectedPeople.remove(memberId);
            }
        });

		if (holder.name != null && null != people.getName()
				&& people.getName().trim().length() > 0) {
			holder.name.setText(Html.fromHtml(people.getName()));
		}

		return view;
	}

	public class ViewHolder {
        public CheckBox checkMember;
        public TextView name;
	}

    public HashSet<Integer> getSelectedPeople(){
        return selectedPeople;
    }
}