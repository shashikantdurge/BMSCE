package com.projects.psps.bmsce.syllabus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projects.psps.bmsce.R;

/*
 Created by vasan on 22-07-2017.
 */

public class SyllabusFragment extends Fragment {


    public SyllabusFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_syllabus_1, container, false);
        ViewPager vpPager = (ViewPager) rootView.findViewById(R.id.vpPager);
        SyllabusPagerAdapter syllabusPagerAdapter = new SyllabusPagerAdapter(getChildFragmentManager());
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



    private class SyllabusPagerAdapter extends FragmentPagerAdapter{

        SyllabusPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                /*case 0: // Fragment # 0 - This will show FirstFragment
                    return new PortionFragment();*/
                case 0: // Fragment # 0 - This will show FirstFragment different title
                    return new MyCourseFragment();
                case 1:
                    return new AllCourseFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                /*case 0:
                    return "PORTION";*/
                case 0:
                    return "MY COURSES";
                case 1:
                    return "ALL COURSES";
                default:
                    return "NULL";
            }
        }
    }

}
