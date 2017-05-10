package com.palmwifi.ktv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.open.androidtvwidget.view.RelativeMainLayout;
import com.palmwifi.base.BaseActivity;
import com.palmwifi.ktv.bean.Song;
import com.palmwifi.ktv.constact.PayContract;
import com.palmwifi.ktv.comm.UserManager;
import com.palmwifi.ktv.event.FavEvent;
import com.palmwifi.ktv.helper.FavHelper;
import com.palmwifi.ktv.helper.ViewFocusHelper;
import com.palmwifi.ktv.presenter.PayPresenter;
import com.palmwifi.ktv.views.CommDialog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by David on 2017/3/27.
 */

public class PersonalActivity extends BaseActivity<PayContract.Presenter> implements PayContract.View {
    @BindView(R.id.rl_home_content)
    RelativeMainLayout rlHomeContent;
    @BindView(R.id.btn_buy)
    ImageView btnBuy;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.img_blur)
    ImageView imgBlur;
    CommDialog dialog;


    public static void startActivity(Context context) {

        Intent i = new Intent(context, PersonalActivity.class);
        context.startActivity(i);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.activity_personal;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
       // mSharkHelper = new ViewFocusHelper();
        rlHomeContent.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(final View oldFocus, final View newFocus) {

                   // mSharkHelper.onFocusView(newFocus);


            }
        });

        for (int i = 0; i < rlHomeContent.getChildCount(); i++) {
            rlHomeContent.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.requestFocus();
                    }
                    return false;
                }
            });
        }

        if(UserManager.getInstance().isVip()){
            btnBuy.setSelected(true);
        }else{
            btnBuy.setSelected(false);
        }
    }

    @Override
    protected void initData() {
        new PayPresenter(this,this,this);
        mPresenter.authPay();

    }

    @OnClick(R.id.btn_buy)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_buy:
                showPayDialog();
                break;
        }
    }

    private void showPayDialog() {
        imgBlur.setVisibility(View.VISIBLE);
        tvAccount.setVisibility(View.INVISIBLE);
        btnBuy.setVisibility(View.INVISIBLE);
        dialog = new CommDialog.Builder(this).setContentText(UserManager.getInstance().isVip() ? getString(R.string.cancel_tip)
                :getString(R.string.pay_tip))
                .setOnConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserManager.getInstance().isVip()) {
                    Toast.makeText(PersonalActivity.this, R.string.unsub,Toast.LENGTH_SHORT).show();
                    mPresenter.cancelPay();
                }else{
                    mPresenter.pay();
                    Toast.makeText(PersonalActivity.this, R.string.ording,Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).build();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imgBlur.setVisibility(View.INVISIBLE);
                tvAccount.setVisibility(View.VISIBLE);
                btnBuy.setVisibility(View.VISIBLE);
            }
        });
        dialog.show();

    }

    @Override
    public void setPresenter(PayContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initSuccess() {
        Toast.makeText(this, R.string.init_suc,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void initFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isHasPay(boolean isPay) {
        if(UserManager.getInstance().isVip()){
            btnBuy.setSelected(true);
        }else{
            btnBuy.setSelected(false);
        }
    }

    @Override
    public void paySuccess() {
        Toast.makeText(this,getString(R.string.pay_suc),Toast.LENGTH_SHORT).show();
        btnBuy.setSelected(true);
    }

    @Override
    public void payFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelPaySuccess() {
        btnBuy.setSelected(false);
        Toast.makeText(this, R.string.cancel_orider,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void cancelPayFailure(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }
}
