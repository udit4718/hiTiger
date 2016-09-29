package com.hcs.hitiger.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hcs.hitiger.fragments.MainFragment;

/**
 * Created by anuj gupta on 4/26/16.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT;

    public HomePagerAdapter(FragmentManager fm, int PAGE_COUNT) {
        super(fm);
        this.PAGE_COUNT = PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainFragment.getInstance(position);
            case 1:
                return MainFragment.getInstance(position);
            case 2:
                return MainFragment.getInstance(position);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
