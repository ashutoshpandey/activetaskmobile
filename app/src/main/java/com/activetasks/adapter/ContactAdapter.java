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

import activetasks.activetasks.R;
import com.activetasks.pojo.Contact;

import java.util.HashSet;
import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {

	private Activity activity;
	private List<Contact> items;
	private Contact contact;
	private HashSet<Integer> selectedContacts = new HashSet<>();

	public ContactAdapter(Activity act, List<Contact> arrayList) {
        super(act, R.layout.layout_list_contact, arrayList);
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
			view = inflater.inflate(R.layout.layout_list_contact, null);

			holder = new ViewHolder();
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if ((items == null) || ((position + 1) > items.size()))
			return view;

		contact = items.get(position);

		holder.name = (TextView) view.findViewById(R.id.tvContact);

		if (holder.name != null && null != contact.getName()
				&& contact.getName().trim().length() > 0) {
			holder.name.setText(Html.fromHtml(contact.getName()));
		}

		return view;
	}

	public class ViewHolder {
        public TextView name;
	}

    public HashSet<Integer> getSelectedContacts(){
        return selectedContacts;
    }
}