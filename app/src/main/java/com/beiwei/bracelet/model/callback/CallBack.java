package com.beiwei.bracelet.model.callback;

public interface CallBack {
    <T> void success(T response);

    void fail(String msg);
}
