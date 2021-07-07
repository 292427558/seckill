package com.github.lyrric.util;

public abstract class RetryTemplate<T> {

    private static final int DEFAULT_RETRY_TIME = 5;

    private int retryTime = DEFAULT_RETRY_TIME;

    // 重试的睡眠时间
    private int sleepTime = 100;

    public int getSleepTime() {
        return sleepTime;
    }

    public RetryTemplate setSleepTime(int sleepTime) {
        if(sleepTime < 0) {
            throw new IllegalArgumentException("sleepTime should equal or bigger than 0");
        }

        this.sleepTime = sleepTime;
        return this;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public RetryTemplate setRetryTime(int retryTime) {
        if (retryTime <= 0) {
            throw new IllegalArgumentException("retryTime should bigger than 0");
        }

        this.retryTime = retryTime;
        return this;
    }

    /**
     * 重试的业务执行代码
     * 失败时请抛出一个异常
     *
     * @return
     */
    protected abstract T doBiz() throws Exception;


    public T execute() {
        for (int i = 0; i < retryTime; i++) {
            try {
                return doBiz();
            } catch (Exception e) {
                System.out.println("业务执行出现异常，重试"+ e);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }


}