package com.njust.pdfmutithread.user;


import com.njust.pdfmutithread.system.PendingJobPool;
import com.njust.pdfmutithread.system.vo.TaskResult;
import com.njust.pdfmutithread.user.task.PDFTaskMutiThread;
import com.njust.pdfmutithread.user.task.PDFTaskSingThread;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Chen
 * @version 1.0
 * @date 2020/3/26 21:55
 * @description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PDFServiceTest {

    @Autowired
    private PDFTaskSingThread pdfTaskSingThread;


    public static void printTime(long start, long end) {
        long spendTime = end - start;
        System.out.println("spendTime: " + spendTime);
    }

    @Test
    public void createOne() {
        long start = System.currentTimeMillis();
        pdfTaskSingThread.createOne();
        long end = System.currentTimeMillis();
        printTime(start, end);
    }

    //  JOB_LENGTH：260      spendTime: 17310
    //  JOB_LENGTH：512      spendTime: 30660
    //  JOB_LENGTH：1024      spendTime: 59573
    @Test
    public void createManyWithOneThread() {
        long start = System.currentTimeMillis();
        pdfTaskSingThread.createManyWithOneThread(JOB_LENGTH);
        long end = System.currentTimeMillis();
        printTime(start, end);

    }


    private final static String JOB_NAME = "生成PDF";
    private final static int JOB_LENGTH = 1024;

    @Autowired
    private PDFTaskMutiThread pdfTaskMutiThread;

    //查询任务进度的线程
    private static class QueryResult implements Runnable {

        private PendingJobPool pool;

        public QueryResult(PendingJobPool pool) {
            super();
            this.pool = pool;
        }

        @Override
        public void run() {
            int num = 0;
            List<TaskResult<String>> taskDetail;
            try {
                while (num < JOB_LENGTH) {
                    taskDetail = pool.getTaskDetail(JOB_NAME);
                    if (!taskDetail.isEmpty()) {
                        num += taskDetail.size();
                    }
                }
            } catch (Exception e) {

            }
            end_chen = System.currentTimeMillis();
            PDFServiceTest.printTime(start_chen, end_chen);
        }

    }


    private static long start_chen;
    private static long end_chen;

    //    JOB_LENGTH：260 spendTime: 17599
    //    JOB_LENGTH：512 spendTime: 33531
    //    JOB_LENGTH：1024 spendTime: 56144
    @Test
    public void testPDFTaskMutiThread() throws InterruptedException {
        start_chen = System.currentTimeMillis();
        //拿到框架的实例
        PendingJobPool pool = PendingJobPool.getInstance();
        //注册job
        pool.registerJob(JOB_NAME, JOB_LENGTH, pdfTaskMutiThread, 1000 * 50);
        // 假定用户不按套路出牌
        for (int i = 0; i < JOB_LENGTH; i++) {
            //依次推入Task
            pool.putTask(JOB_NAME, String.valueOf(i));
        }
        Thread t = new Thread(new QueryResult(pool));
        t.start();
        t.join();
    }

}