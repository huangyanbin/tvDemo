package com.palmwifi.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.palmwifi.fragmention.R;
import com.palmwifi.mvp.IPresenter;
import com.palmwifi.utils.StatusUtils;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by David on 2016/12/1.
 * 在使用Rxjava会经常性的内存泄漏，
 * 使用RxLifeCycle来跟踪回收内存
 */

public abstract class BaseActivity< P extends  IPresenter> extends SupportActivity implements LifecycleProvider<ActivityEvent> {

    protected P mPresenter;

    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    private Unbinder mUnbinder;

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        if(useEventBus()) {
            EventBus.getDefault().register(this);
        }
        StatusUtils.setBarStatusWhite(this,getStatusColor());
        setContentView(setLayoutID());
        mUnbinder = ButterKnife.bind(this);
        initView(savedInstanceState);
        initData();
    }
    /**
     * 改变状态栏颜色
     * @return
     */
    protected int getStatusColor(){
        return ContextCompat.getColor(this,android.R.color.white);
    }
    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if(useEventBus())
            EventBus.getDefault().unregister(this);
        OkHttpUtils.getInstance().cancelTag(this);
        if (mUnbinder != Unbinder.EMPTY)
            mUnbinder.unbind();
        if(mPresenter != null)
            mPresenter.unSubscribe();
        super.onDestroy();
    }

    @Override


    public void startActivity(Intent intent) {
        super.startActivity(intent);
        animationForNew();
    }


    @Override
    public void finish() {
        super.finish();
        animationForOld();
    }

    public void oldFinish() {
        super.finish();
    }

    protected  boolean useEventBus(){
        return false;
    }

    protected abstract int setLayoutID();

    protected abstract void initView(@Nullable Bundle savedInstanceState);

    protected abstract void initData();


    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
         return new DefaultHorizontalAnimator();
    }
    public void animationForNew() {
        overridePendingTransition(R.anim.main_translatex100to0,
                R.anim.main_translatex0tof100);
    }
    public void animationForOld() {
        overridePendingTransition(R.anim.main_translatexf100to0,
                R.anim.main_translatex0to100);
    }

    public void startActivityOld(Intent intent) {
        super.startActivity(intent);
    }


}
