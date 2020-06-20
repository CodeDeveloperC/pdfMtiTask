package com.njust.pdfmutithread.system;


import com.njust.pdfmutithread.system.vo.ItemVo;

import java.util.concurrent.DelayQueue;

/**
 * 类说明：任务完成后,在一定的时间供查询，之后为释放资源节约内存，需要定期处理过期的任务
 */
public class CheckJobProcesser {
    //DelayQueue是一个无界阻塞队列，只有在延迟期满时才能从中提取元素
    private static DelayQueue<ItemVo<String>> queue
            = new DelayQueue<>();//存放已完成任务等待过期的队列

    //单例模式------
    private CheckJobProcesser() {
    }

    private static class ProcesserHolder {
        public static CheckJobProcesser processer = new CheckJobProcesser();
    }

    public static CheckJobProcesser getInstance() {
        return ProcesserHolder.processer;
    }
    //单例模式------

    //处理队列中到期任务的实行
    private static class FetchJob implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    //拿到已经过期的任务 take()是阻塞方法
                    ItemVo<String> item = queue.take();
                    String jobName = item.getDate();
                    PendingJobPool.getMap().remove(jobName);
                    System.out.println(jobName + " is out of date,remove from map!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*任务完成后，放入队列，经过expireTime时间后，从整个框架中移除*/
    public void putJob(String jobName, long expireTime) {
        //实现名称和到期时间绑定
        ItemVo<String> item = new ItemVo<String>(expireTime, jobName);
        queue.offer(item);
        System.out.println("Job[" + jobName + "已经放入了过期检查缓存，过期时长：" + expireTime);
    }

    static {
        Thread thread = new Thread(new FetchJob());
        thread.setDaemon(true);
        thread.start();
        System.out.println("开启任务过期检查守护线程................");
    }


}

