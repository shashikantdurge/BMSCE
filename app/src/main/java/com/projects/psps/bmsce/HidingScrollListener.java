package com.projects.psps.bmsce;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import io.realm.internal.Util;

/**
 * Created by ${SHASHIKANt} on 30-07-2017.
 */

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {



    private int mToolbarOffset = 0;

    private int mToolbarHeight;



    public HidingScrollListener(Context context) {

        mToolbarHeight =100;

    }



    @Override

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        super.onScrolled(recyclerView, dx, dy);



        clipToolbarOffset();

        onMoved(mToolbarOffset);



        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {

            mToolbarOffset += dy;

        }

    }



    private void clipToolbarOffset() {

        if(mToolbarOffset > mToolbarHeight) {

            mToolbarOffset = mToolbarHeight;

        } else if(mToolbarOffset < 0) {

            mToolbarOffset = 0;

        }

    }



    public abstract void onMoved(int distance);

}