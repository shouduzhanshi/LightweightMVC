package com.yzk.lightweightmvc;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public abstract class BaseFragmentView<T extends BaseFragmentController> {

    protected T mController;

    private Unbinder bind;

    protected Context mContext;

    protected View fragmentView;

    public void setFragmentView(T controller, Context context, View view) {
        bind = ButterKnife.bind(this, view);
        mContext = context;
        mController = controller;
        initView();
    }

    protected abstract View setContentLayout(LayoutInflater inflater, @Nullable ViewGroup container);

    protected abstract void initView();

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void destroyView() {
        bind.unbind();
        mContext = null;
        LoadingConfigMode.releaseCreateDialog(mController.getActivity());
    }

    public void showMessage(String message) {
        MessageUtils.showMessage(message);
    }

    public void showErrorMessage(String message) {
        MessageUtils.showErrorMessage(message);
    }

    public void showWarningMessage(String message) {
        MessageUtils.showWarningMessage(message);
    }

    public static void showToastShort(String message) {
        MessageUtils.showToastShort(message);
    }

    public static void showToastLong(String message) {
        MessageUtils.showToastLong(message);
    }

    public BaseFragmentView() {

    }

    public void showLoading(String message) {
        LoadingConfigMode.showLoading(message, mController.getActivity());
    }

    Disposable closeTimer;

    public void showLoadingWithTimeOut(String message, int second) {
        Dialog dialog = LoadingConfigMode.showLoading(message, mController.getActivity());
        Observable.timer(second, TimeUnit.SECONDS)
                .compose(mController.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        closeTimer = d;
                    }

                    @Override
                    public void onNext(Long value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (dialog.isShowing()) {
                            hideLoding();
                        }
                    }
                });
        dialog.setOnDismissListener((dialogInterface) -> {
            if (closeTimer != null) {
                closeTimer.dispose();
                closeTimer = null;
            }
            dialog.setOnDismissListener(null);
        });
    }

    public void hideLoding() {
        LoadingConfigMode.hideLoding(mController.getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = this.setContentLayout(inflater, container);
            if (isConfigToolbar()) {
                ToolbarConfigMode.configToolbar(fragmentView);
            }
            setFragmentView(mController, mContext, fragmentView);
            mController.initData();
            return fragmentView;
        } else {
            container.removeView(fragmentView);
            return fragmentView;
        }
    }

    /***
     * 重写此方法决定是不是执行默认toolbar设置
     * @return
     */
    public boolean isConfigToolbar() {
        return true;
    }


}
