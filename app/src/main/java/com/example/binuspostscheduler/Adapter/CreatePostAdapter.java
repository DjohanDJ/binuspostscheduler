package com.example.binuspostscheduler.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.binuspostscheduler.fragments.BaseFragment;
import com.example.binuspostscheduler.fragments.CreatePostInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreatePostAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments;

    public CreatePostAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
    }

    public List<BaseFragment> getFragments() {
        return fragments;
    }

    public void setFragments(List<BaseFragment> fragments) {
        this.fragments = fragments;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        Log.d("A","A");
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
