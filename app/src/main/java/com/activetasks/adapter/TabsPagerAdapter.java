package com.activetasks.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.activetasks.fragment.AssignedTaskFragment;
import com.activetasks.fragment.ContactFragment;
import com.activetasks.fragment.GroupFragment;
import com.activetasks.fragment.MainFragment;
import com.activetasks.fragment.TaskFragment;

/**
 * Created by ashutosh on 06/05/2015.
 */

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new MainFragment();
            case 1:
                return new TaskFragment();
            case 2:
                return new AssignedTaskFragment();
            case 3:
                return new GroupFragment();
            case 4:
                return new ContactFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }

}