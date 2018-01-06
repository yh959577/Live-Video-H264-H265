package com.example.hy.liveexampleandroid.View;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Size;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hy.liveexampleandroid.Push.PusherImp;
import com.example.hy.liveexampleandroid.R;

/**
 * Created by UPC on 2018/1/5.
 */

public class SettingPopupWindowView extends ConstraintLayout {
    private RadioGroup mPreviewSettingGroup;
    private RadioGroup mPushSettingGroup;
    private RadioGroup mPushTypeGroup;

    private RadioButton mH264Btn;
    private RadioButton mH265Btn;

    private TextView mPreviewSettingText;
    private TextView mPushSettingText;
    private TextView mPushTypeText;

    private Size[] supportSize;
    private RadioButton[] mPreviewBtns;
    private RadioButton[] mPushBtns;


    public SettingPopupWindowView(Context context) {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.white_color));
        supportSize= PusherImp.supportSize;
        int btnNum=0;
        if (supportSize.length>6){
            btnNum=6;
        }else {
            btnNum=supportSize.length;
        }
        mPreviewBtns=new RadioButton[btnNum];
        mPushBtns=new RadioButton[btnNum];

        for (int i = 0; i <btnNum ; i++) {
            RadioButton radioButton=new RadioButton(context);
            radioButton.setText(String.valueOf(supportSize[i]));
            mPreviewBtns[i]=radioButton;
        }
        for (int i = 0; i <btnNum ; i++) {
            RadioButton radioButton=new RadioButton(context);
            radioButton.setText(String.valueOf(supportSize[i]));
            mPushBtns[i]=radioButton;
        }

        initialRadioGroup(context);
        initialRadioBtn(context);
        initialText(context);

        addRadioBtnToGroup(LinearLayout.VERTICAL, mPreviewSettingGroup,mPreviewBtns);
        addRadioBtnToGroup(LinearLayout.VERTICAL,mPushSettingGroup, mPushBtns);
        addRadioBtnToGroup(LinearLayout.VERTICAL, mPushTypeGroup, mH264Btn, mH265Btn);

        arrangeLayout();

    }


    private void initialRadioGroup(Context context) {
        mPreviewSettingGroup = new RadioGroup(context);
        mPushSettingGroup = new RadioGroup(context);
        mPushTypeGroup = new RadioGroup(context);

        mPreviewSettingGroup.setId(View.generateViewId());
        mPushSettingGroup.setId(View.generateViewId());
        mPushTypeGroup.setId(View.generateViewId());

    }

    private void initialRadioBtn(Context context) {
        mH264Btn = new RadioButton(context);
        mH265Btn = new RadioButton(context);
        mH264Btn.setText(R.string.btn_H264);
        mH265Btn.setText(R.string.btn_H265);
    }

    private void initialText(Context context) {
        mPreviewSettingText = new TextView(context);
        mPreviewSettingText.setText(R.string.preview_resolution);
        mPreviewSettingText.setId(View.generateViewId());

        mPushSettingText = new TextView(context);
        mPushSettingText.setText(R.string.push_resolution);
        mPushSettingText.setId(View.generateViewId());

        mPushTypeText = new TextView(context);
        mPushTypeText.setText(R.string.encode_type);
        mPushTypeText.setId(View.generateViewId());


    }

    private void addRadioBtnToGroup(@LinearLayoutCompat.OrientationMode int orientation
            , RadioGroup radioGroup, RadioButton... radioButtons) {
        radioGroup.setOrientation(orientation);
        for (RadioButton btn : radioButtons) {
            radioGroup.addView(btn);
        }
    }

    private void arrangeRadioGroup(RadioGroup radioGroup, TextView textView,TextView leftView) {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topToBottom = textView.getId();
        if (leftView!=null)
        layoutParams.leftToRight=leftView.getId();
        else layoutParams.leftToLeft=LayoutParams.PARENT_ID;
    //    layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        addView(radioGroup, layoutParams);
    }



    private void arrangeLayout() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams.topToTop = LayoutParams.PARENT_ID;
        layoutParams.leftToLeft = LayoutParams.PARENT_ID;
        addView(mPreviewSettingText, layoutParams);

      //  arrangeRadioGroup(mPreviewSettingGroup,mPreviewSettingText,null);
         arrangePreviewGroup();

        LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams1.topToTop = LayoutParams.PARENT_ID;
        layoutParams1.rightToRight = LayoutParams.PARENT_ID;
        addView(mPushSettingText, layoutParams1);

        arrangePushGroup();
       // arrangeRadioGroup(mPushSettingGroup,mPushSettingText,mPushTypeText);



        LayoutParams layoutParams2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams2.topToTop = LayoutParams.PARENT_ID;
        layoutParams2.rightToRight = LayoutParams.PARENT_ID;
        layoutParams2.leftToLeft=LayoutParams.PARENT_ID;


        addView(mPushTypeText, layoutParams2);
        arrangeTypeGroup();

     //   arrangeRadioGroup(mPushTypeGroup,mPushTypeText,mPreviewSettingText);

    }
    private void arrangePreviewGroup(){
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topToBottom=mPreviewSettingText.getId();
        layoutParams.leftToLeft=LayoutParams.PARENT_ID;
        addView(mPreviewSettingGroup,layoutParams);
    }

    private void arrangePushGroup(){
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topToBottom=mPushSettingText.getId();
        layoutParams.rightToRight=LayoutParams.PARENT_ID;
        addView(mPushSettingGroup,layoutParams);
    }

    private void arrangeTypeGroup(){
        LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topToBottom=mPushTypeText.getId();
        layoutParams.leftToLeft=LayoutParams.PARENT_ID;
        layoutParams.rightToRight=LayoutParams.PARENT_ID;
        addView(mPushTypeGroup,layoutParams);
    }
}
