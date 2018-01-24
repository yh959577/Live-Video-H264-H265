package com.example.hy.liveexampleandroid.View;

import android.content.Context;
import android.media.MediaFormat;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Size;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hy.liveexampleandroid.R;
import com.example.livelib.Push.PusherImp;


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

    private Size[] mSupportSize;
    private RadioButton[] mPreviewBtns;
    private RadioButton[] mPushBtns;
    private SparseArray<Size> mPreviewSizeMap;
    private SparseArray<Size> mPushSizeMap;
    private SparseArray<String> mPushTypeMap;

    public SettingPopupWindowView(Context context,Size...supportSize) {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.white_color));
        mSupportSize= PusherImp.supportSize;
        mPreviewSizeMap =new SparseArray<>();
        mPushSizeMap =new SparseArray<>();
        mPushTypeMap =new SparseArray<>();

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
        int btnNum=0;
//        if (mSupportSize.length>6){
//            btnNum=6;
//        }else {
//            btnNum= mSupportSize.length;
//        }
        btnNum=mSupportSize.length;
        mPreviewBtns=new RadioButton[btnNum];
        mPushBtns=new RadioButton[btnNum];

        for (int i = 0; i <btnNum ; i++) {
            RadioButton radioButton=new RadioButton(context);
            initialSingleRadioBtn(radioButton,String.valueOf(mSupportSize[i]));
            mPreviewBtns[i]=radioButton;
            mPreviewSizeMap.put(radioButton.getId(),mSupportSize[i]);
        }
        for (int i = 0; i <btnNum ; i++) {
            RadioButton radioButton=new RadioButton(context);
            initialSingleRadioBtn(radioButton,String.valueOf(mSupportSize[i]));
            mPushBtns[i]=radioButton;
            mPushSizeMap.put(radioButton.getId(),mSupportSize[i]);
        }

        mH264Btn = new RadioButton(context);
        mH265Btn = new RadioButton(context);
        initialSingleRadioBtn(mH264Btn,R.string.btn_H264);
        initialSingleRadioBtn(mH265Btn,R.string.btn_H265);
        mPushTypeMap.put(mH264Btn.getId(), MediaFormat.MIMETYPE_VIDEO_AVC);
        mPushTypeMap.put(mH265Btn.getId(),MediaFormat.MIMETYPE_VIDEO_HEVC);
    }


    private void initialSingleRadioBtn(RadioButton radioButton,int resId){
        radioButton.setText(resId);
        radioButton.setId(View.generateViewId());
    }

    private void initialSingleRadioBtn(RadioButton radioButton,CharSequence text){
        radioButton.setText(text);
        radioButton.setId(View.generateViewId());
    }

    private void initialText(Context context) {
        mPreviewSettingText = new TextView(context);
        initialSingleText(mPreviewSettingText,R.string.preview_resolution);

        mPushSettingText = new TextView(context);
        initialSingleText(mPushSettingText,R.string.push_resolution);

        mPushTypeText = new TextView(context);
        initialSingleText(mPushTypeText,R.string.encode_type);
    }

    private void initialSingleText(TextView textView,@StringRes int ResId){
          textView.setText(ResId);
          textView.setId(View.generateViewId());
    }

    private void addRadioBtnToGroup(@LinearLayoutCompat.OrientationMode int orientation
            , RadioGroup radioGroup, RadioButton... radioButtons) {
        radioGroup.setOrientation(orientation);
        for (RadioButton btn : radioButtons) {
            radioGroup.addView(btn);
        }
        radioGroup.check(radioButtons[0].getId());
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

    public Size getCheckedPreviewSize(){
         return mPreviewSizeMap.get(mPreviewSettingGroup.getCheckedRadioButtonId());
    }

    public Size getCheckedPushSize(){
        return mPushSizeMap.get(mPushSettingGroup.getCheckedRadioButtonId());
    }

    public String getCheckedType(){
        return mPushTypeMap.get(mPushTypeGroup.getCheckedRadioButtonId());
    }
}
