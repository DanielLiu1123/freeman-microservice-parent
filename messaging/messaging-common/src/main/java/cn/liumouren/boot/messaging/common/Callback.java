package cn.liumouren.boot.messaging.common;


/**
 * 异步发送消息回调
 *
 * @author freeman
 * @date 2021/12/28 22:27
 */
public interface Callback {
    /**
     * 成功回调
     * @param response response
     */
    public void onSuccess(Object response);

    /**
     * 异常回调
     * @param e e
     */
    public void onException(Throwable e);
}
