package com.beiwei.bracelet.activity;

public interface IWebView extends IBaseView{

    void loadUrl(String url);

    void setBackUrl(String backUrl);

    void scan();

    void readimei();
}
