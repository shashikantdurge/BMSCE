package com.projects.psps.bmsce;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by vasan on 22-07-2017.
 */

public class SyllabusFragment1 extends Fragment {



    SyllabusPagerAdapter syllabusPagerAdapter;

    public SyllabusFragment1(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_syllabus_1, container, false);
        ViewPager vpPager = (ViewPager) rootView.findViewById(R.id.vpPager);
        syllabusPagerAdapter = new SyllabusPagerAdapter(getChildFragmentManager());
        vpPager.setAdapter(syllabusPagerAdapter);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    private class SyllabusPagerAdapter extends FragmentPagerAdapter{

        SyllabusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return new SPortionFragment();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return new SMyCourseFragment();
                case 2:
                    return new SAllCourseFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "PORTION";
                case 1:
                    return "MY COURSES";
                case 2:
                    return "ALL COURSES";
                default:
                    return "NULL";
            }
        }
    }

}
