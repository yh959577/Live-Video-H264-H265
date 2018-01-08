package com.example.hy.liveexampleandroid.View;

import android.content.Context;
import android.media.MediaFormat;
import android.support.constraint.ConstraintLayout;
import android.util.Size;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by Hamik Young on 2018/1/6.
 */

public class SettingPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {
    private Size[] mSupportSizes;
    private SettingPopupWindowView mSettingPopupWindowView;

    public SettingPopupWindow(Context context, Size... supportSize) {
        mSupportSizes = supportSize;
        mSettingPopupWindowView=new SettingPopupWindowView(context,supportSize);
        addContentView();
    }


   public void addContentView(){
        super.setContentView(mSettingPopupWindowView);
        super.setWidth(ConstraintLayout.LayoutParams.MATCH_PARENT);
        super.setHeight(ConstraintLayout.LayoutParams.WRAP_CONTENT);
   }

    @Override
    public void onDismiss() {

    }

    public Size getPreviewSize() {
        return mSettingPopupWindowView.getCheckedPreviewSize();
    }

    public Size getPushSize() {
        return mSettingPopupWindowView.getCheckedPushSize();
    }

    public String getPushType() {
        return mSettingPopupWindowView.getCheckedType();
    }
}
