package com.activetasks.fragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activetasks.activetasks.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class GroupFragment extends Fragment {

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        return rootView;
    }
}

