package com.andy.demo;

import java.util.concurrent.*;

public class FileCopyThreadPoolExecutor {

    public static ThreadPoolExecutor executor;

    static {
        executor = new ThreadPoolExecutor(5,10,5,
                TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

}
