package com.chao.utils;

public interface HttpCallbackListener {
void onfinish(String response);
void onError(Exception e);
}
