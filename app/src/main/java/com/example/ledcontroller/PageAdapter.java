package com.example.ledcontroller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private int numTabs;

    PageAdapter (FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int pos) {
        switch (pos) {
            case 0:
                return new FavoriteColorsFragment();
            case 1:
                return new PresetColorsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }
}
