package com.example.hy.liveexampleandroid.View;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hy.liveexampleandroid.R;

/**
 * Created by UPC on 2018/1/5.
 */

public class SettingPopupWindowView extends ConstraintLayout {
    private RadioGroup mPreviewSettingGroup;
    private RadioGroup mPushSettingGroup;

    private RadioButton m1080pBtn;
    private RadioButton m720pBtn;
    private RadioButton m480pBtn;


    private RadioGroup mPushTypeGroup;
    private RadioButton mH264Btn;
    private RadioButton mH265Btn;



    public SettingPopupWindowView(Context context) {
        super(context);
        initialRadioGroup(context);
        initialRadioBtn(context);
        arrangeLayout();
    }

    private void initialRadioGroup(Context context){
        mPreviewSettingGroup=new RadioGroup(context);
        mPushSettingGroup=new RadioGroup(context);
        mPushTypeGroup=new RadioGroup(context);
    }
    private void initialRadioBtn(Context context){
        m480pBtn=new RadioButton(context);
        m720pBtn=new RadioButton(context);
        m1080pBtn=new RadioButton(context);

        m480pBtn.setText(R.string.btn_480p);
        m720pBtn.setText(R.string.btn_720p);
        m1080pBtn.setText(R.string.btn_1080p);

        mH264Btn=new RadioButton(context);
        mH265Btn=new RadioButton(context);

        mH264Btn.setText(R.string.btn_H264);
        mH265Btn.setText(R.string.btn_H265);
    }

    private void arrangeLayout(){
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        mPreviewSettingGroup.addView(m1080pBtn);
        addView(mPreviewSettingGroup,layoutParams);
    }


}
