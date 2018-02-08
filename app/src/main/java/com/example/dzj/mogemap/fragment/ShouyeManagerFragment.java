package com.example.dzj.mogemap.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.RunActivity;
import com.example.dzj.mogemap.dialog.picker.MovingTargetPickerDialog;
import com.example.dzj.mogemap.modle.RunRecord;
import com.example.dzj.mogemap.view.RainbowView;
import com.example.dzj.mogemap.view.RunRecordView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dzj on 2017/12/14.
 */

public class ShouyeManagerFragment extends Fragment {

    private View rootView;
    private RainbowView rainbowView;
    private RunRecordView runRecordView;
    private List<RunRecord> records;
    private Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView=inflater.inflate(R.layout.shouye_manager,null);
        //rainbowView = (RainbowView)rootView.findViewById(R.id.rainbow);
        //rainbowView.setInstensityTime(5);
        //rainbowView.setStepCount(1002);
        runRecordView = (RunRecordView)rootView.findViewById(R.id.runview);
        initdata();
        button = (Button)rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RunActivity.class));
                //showDialogRegion();
            }
        });

        return rootView;
    }
    private void initdata(){
        records = new ArrayList<>();
        for (int i=0;i<4;i++){
            RunRecord runRecord = new RunRecord("10/21",1.5);
            records.add(runRecord);
        }
        runRecordView.setRecords(records);
    }
    private final void showDialogRegion() {
        MovingTargetPickerDialog.Builder builder = new MovingTargetPickerDialog.Builder(getActivity());

        MovingTargetPickerDialog dialog = builder.setOnMovingTargetSelectedListener(new MovingTargetPickerDialog.OnMovingTargetSelectedListener() {
            @Override
            public void onMovingTargetSelected(String[] cityAndArea) {
                Toast.makeText(getActivity().getApplicationContext(), cityAndArea[0] + "#" + cityAndArea[1], Toast.LENGTH_SHORT).show();
            }

        }).create();
        dialog.show();
    }
}
