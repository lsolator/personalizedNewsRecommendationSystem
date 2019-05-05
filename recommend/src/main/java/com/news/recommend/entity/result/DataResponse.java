package com.news.recommend.entity.result;

public class DataResponse {
    private String code;
    private String message;
    private Object data;

    public DataResponse(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
