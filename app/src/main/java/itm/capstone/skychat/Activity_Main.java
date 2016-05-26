package itm.capstone.skychat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import info.androidhive.webgroupchat.R;

public class Activity_Main extends AppCompatActivity {

	// LogCat tag
	private static final String TAG = Activity_Main.class.getSimpleName();


    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        mNavItems.add(new NavItem("Login", "로그인 하세요", R.drawable.bg_messages));
        mNavItems.add(new NavItem("Chat", "채팅 하세요", R.drawable.drawer_shadow));
        mNavItems.add(new NavItem("Mypage", "마이페이지", R.drawable.drawer_shadow));
        mNavItems.add(new NavItem("Event", "이벤트", R.drawable.drawer_shadow));

// DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        Adapter_DrawerList adapter = new Adapter_DrawerList(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d(TAG, "onDrawerClosed: " + getTitle());

                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fragment fragment = Fragment_Chat.newInstance(getApplicationContext());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "fragment_id").commit();
	}

    /** Called when a particular item from the navigation drawer
    * is selected.
    **/
    private void selectItemFromDrawer(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = null;
        switch(position) {
            case 0: // Login
                fragment = (Fragment) Fragment_Login.newInstance(getBaseContext());
                break;
            case 1: // Chat
                Fragment tmp_fragment = fragmentManager.findFragmentByTag("fragment_id");
                if (tmp_fragment != null && tmp_fragment.isVisible()) {
                    break;
                }
                fragment = (Fragment) Fragment_Chat.newInstance(getBaseContext());
                break;
            case 2: // Mypage
                fragment = (Fragment) Fragment_Mypage.newInstance(getBaseContext());
                break;
            case 3: // Event
                //fragment = (Fragment) Fragment_Event.newInstance(getBaseContext());
                Dialog_Record dialog_record = Dialog_Record.newInstance(getBaseContext());
                dialog_record.show(getSupportFragmentManager(),"dialog");
                break;
        }
        if(fragment != null) {
            ft.addToBackStack(null);
            ft.replace(R.id.mainContent, fragment).commit();
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
	/**
	 * Method to send message to web socket server
	 * */
}
