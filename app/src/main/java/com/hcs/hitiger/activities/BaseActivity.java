package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by hot cocoa on 12/05/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    protected abstract void setTextViewTypeFaces();

    protected void showProgressDialog(String message) {
        dismissProgressDialog();
        mProgressDialog = ProgressDialog.show(this, "", message, true, false);
    }

    protected void dismissProgressDialog(){
        if(mProgressDialog!=null&&mProgressDialog.isShowing()&&!isFinishing()){
            mProgressDialog.dismiss();
        }
    }

}
