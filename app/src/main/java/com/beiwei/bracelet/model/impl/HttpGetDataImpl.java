package com.beiwei.bracelet.model.impl;



import com.beiwei.bracelet.model.callback.CallBack;
import com.beiwei.bracelet.model.inter.IHttpGetDataModel;
import com.beiwei.bracelet.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class HttpGetDataImpl implements IHttpGetDataModel {
    @Override
    public void submitPost(String url, Map<String, String> para, final CallBack callBack) {
        HttpUtils.submitPost(url, para, new CallBack() {
            @Override
            public <T> void success(T response) {
                String result = (String) response;
                try {
                    JSONObject job = new JSONObject(result);
                    int status = job.getInt("status");
                    if(status == 200){
                        callBack.success(job);
                    }else{
                        String msg = job.getString("msg");
                        callBack.fail(msg);
                    }
                }catch (JSONException e){
                    callBack.fail("数据解析错误");
                }
            }
            @Override
            public void fail(String msg) {
                callBack.fail(msg);
            }
        });
    }

}
