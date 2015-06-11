package com.activetasks.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import activetasks.activetasks.R;
import com.activetasks.adapter.TabsPagerAdapter;
import com.activetasks.fragment.ContactFragment;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private TabsPagerAdapter mAdapter;
    private ViewPager viewPager;
    private ActionBar actionBar;

    private Menu menu;

    private String[] tabNames = {"Status", "Tasks", "Assigned", "Groups", "Contacts"};

    private ListView listViewGroup;
    private ListView listViewTask;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(actionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tabName : tabNames) {
            Tab tab = actionBar.newTab();
            tab.setText(tabName);
            tab.setTabListener(this);
            actionBar.addTab(tab);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_group) {
            Intent i = new Intent(MainActivity.this, CreateGroupActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_create_task){
            Intent i = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_create_contact){
            Intent i = new Intent(MainActivity.this, CreateContactActivity.class);
            startActivity(i);
            return true;
        }
        else if(id == R.id.logout){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }
}
