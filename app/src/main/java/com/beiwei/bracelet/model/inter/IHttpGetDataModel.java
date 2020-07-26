package com.beiwei.bracelet.model.inter;


import com.beiwei.bracelet.model.callback.CallBack;

import java.util.Map;

public interface IHttpGetDataModel {
    void submitPost(String url, Map<String, String> para, CallBack callBack);
}
