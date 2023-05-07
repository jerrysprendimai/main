package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyViewPagerAdapterOrder extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    public MyViewPagerAdapterOrder(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {     return fragmentArrayList.get(position);    }

    @Override
    public int getCount() {        return fragmentArrayList.size();    }

    public void addFragment(Fragment frgament, String title){
        fragmentArrayList.add(frgament);
        fragmentTitle.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        return fragmentTitle.get(position);
    }
}
