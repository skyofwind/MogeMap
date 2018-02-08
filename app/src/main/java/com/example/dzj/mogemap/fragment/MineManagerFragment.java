package com.example.dzj.mogemap.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dzj.mogemap.R;

/**
 * Created by dzj on 2017/12/14.
 */

public class MineManagerFragment extends Fragment {
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        rootView=inflater.inflate(R.layout.mine_manager,null);
        return rootView;
    }
}
