package main.com.iglobuser.Adapters;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import main.com.iglobuser.Models.ModelFragmentPager;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    List<ModelFragmentPager> layout;

    public MyFragmentPagerAdapter(@NonNull FragmentManager fm, List<ModelFragmentPager> layout) {
        super(fm);
        this.layout = layout;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return layout.get(position).getFragment();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return layout.get(position).getTitle();
    }


    @Override
    public int getCount() {
        return layout.size();
    }

}
