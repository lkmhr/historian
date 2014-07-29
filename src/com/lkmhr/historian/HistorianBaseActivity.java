package com.lkmhr.historian;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class HistorianBaseActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new SimpleFragmentPagerAdapter());
    }
    public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 4;
        public SimpleFragmentPagerAdapter() {
            super(getSupportFragmentManager());
        }
        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
        @Override
		public CharSequence getPageTitle(int position) {
        	String title = "Title";
        	switch (position) {
				case 0:
					title = "Events";
					break;
				case 1:
					title = "Births";
					break;
				case 2:
					title = "Deaths";
					break;
				case 3:
					title = "Holidays";
					break;
				default:
					
					break;
			}
        	
        	return title;
		}
		@Override
        public Fragment getItem(int position) {
			
			Bundle bundle = new Bundle();
			bundle.putInt("INDEX", position+1);
			Fragment fragment = new EventsFragment();
			fragment.setArguments(bundle);
			
            return fragment;
        }
    }    
}
