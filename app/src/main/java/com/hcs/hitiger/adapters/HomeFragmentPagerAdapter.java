package com.hcs.hitiger.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.hcs.hitiger.fragments.EventFragment;
import com.hcs.hitiger.fragments.OpportunityFragment;

/**
 * Created by anuj gupta on 4/28/16.
 */
public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private Fragment mEventFragment;
    private Fragment mOpportunityFragment;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(mOpportunityFragment == null) {
                    mOpportunityFragment = OpportunityFragment.getInstance();
                }
                return mOpportunityFragment;
            case 1:
                if(mEventFragment == null) {
                    mEventFragment = EventFragment.getInstance();
                }
                return mEventFragment;
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] tabTitles = new String[]{"Opportunity", "Events"};
        return tabTitles[position];
    }
}
