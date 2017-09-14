package com.mrsgx.campustalk.mvp;

/**
 * Created by David on 2017/3/29.
 */

public interface BaseView<T> {

    void initViews();
    void Close();
    void showMessage(String msg);
    void startNewPage(Class target);
    void setPresenter(T presenter);
}
