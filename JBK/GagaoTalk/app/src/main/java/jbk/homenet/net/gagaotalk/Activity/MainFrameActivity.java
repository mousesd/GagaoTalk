package jbk.homenet.net.gagaotalk.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import jbk.homenet.net.gagaotalk.Fragment.MsgListFragment;
import jbk.homenet.net.gagaotalk.Fragment.SettingFragment;
import jbk.homenet.net.gagaotalk.Fragment.UserListFragment;
import jbk.homenet.net.gagaotalk.R;

public class MainFrameActivity extends BaseActivity {


    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainframe);


        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new UserListFragment(),
                    new MsgListFragment(),
                    new SettingFragment(),
            };
            private final String[] mFragmentNames = new String[] {
                    "친구목록",
                    "대화목록",
                    "환경설정"
            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        return  true;
    }

}
