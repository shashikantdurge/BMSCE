package com.projects.psps.bmsce;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by ${SHASHIKANt} on 02-08-2017.
 */

public class NotFoundDailog extends DialogFragment implements View.OnClickListener{
    TextView messegeTv;
    Button cancelBtn,mailBtn;
    String branch,sem;
    NotFoundDailog(){
        //Empty
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    static NotFoundDailog newInstance(String branch, String sem){
        NotFoundDailog notFoundDailog=new NotFoundDailog();
        notFoundDailog.branch=branch;
        notFoundDailog.sem=sem;
        return notFoundDailog;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_not_found,container);
        messegeTv=(TextView)view.findViewById(R.id.tv_message);
        cancelBtn=(Button)view.findViewById(R.id.btn_cancel);
        mailBtn=(Button)view.findViewById(R.id.btn_mail);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messegeTv.setText(getContext().getResources().getString(R.string.not_found,branch,sem));
        cancelBtn.setOnClickListener(this);
        mailBtn.setOnClickListener(this);
        getDialog().setTitle("NOT FOUND!!!");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_mail:
                Toast.makeText(getContext(), "naale maadam", Toast.LENGTH_SHORT).show();
        }
    }
}
