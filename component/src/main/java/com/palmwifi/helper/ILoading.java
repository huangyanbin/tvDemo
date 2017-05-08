package com.palmwifi.helper;

/**
 * Created by David on 2016/12/1.
 * 加载状态接口
 */

public interface ILoading {

    /**
     * 开始加载
     */
    void startLoading();

    /**
     * 完成加载
     */
    void stopLoading();

    /**
     * 加载进度
     * @param progress
     */

     void onProgress(float progress);

    /**
     * 加载失败
     */

    void showFailure();

    /**
     * 暂无数据
     */
    void showNoData();
}
