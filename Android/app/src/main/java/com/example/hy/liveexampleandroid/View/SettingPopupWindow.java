package com.example.hy.liveexampleandroid.View;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by Hamik Young on 2018/1/6.
 */

public class SettingPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener {

    public SettingPopupWindow(Context context) {
        super(new SettingPopupWindowView(context), ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        setOnDismissListener(this);
    }


    @Override
    public void onDismiss() {

    }
}
