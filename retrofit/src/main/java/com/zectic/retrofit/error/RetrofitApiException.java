package com.zectic.retrofit.error;

public class RetrofitApiException extends RetrofitException {

    private final String result;

    public RetrofitApiException(String result, String message, Throwable cause) {
        super(message, cause);
        this.result = result;
    }

    public static RetrofitApiException newInstanceWithMsg(String message) {
        return new RetrofitApiException(null, message, null);
    }

    public static RetrofitApiException newInstanceWithCode(String result) {
        return new RetrofitApiException(result, null, null);
    }

    public static RetrofitApiException newInstanceWithMsg(String message, Throwable cause) {
        return new RetrofitApiException(null, message, cause);
    }

    public static RetrofitApiException newInstanceWithCode(String result, Throwable cause) {
        return new RetrofitApiException(result, null, cause);
    }

    public static RetrofitApiException newInstance(String result, String message) {
        return new RetrofitApiException(result, message, null);
    }

    public static RetrofitApiException newInstance(String result, String message, Throwable cause) {
        return new RetrofitApiException(result, message, cause);
    }

    public String getResult() {
        return result;
    }
}
