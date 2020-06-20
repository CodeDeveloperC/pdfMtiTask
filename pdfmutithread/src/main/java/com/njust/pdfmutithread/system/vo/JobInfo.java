package com.njust.pdfmutithread.system.vo;


import com.njust.pdfmutithread.system.CheckJobProcesser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类说明：提交给框架执行的工作实体类,工作：表示本批次需要处理的同性质任务(Task)的一个集合
 * 注意：返回给用户的不能是引用，防止线程安全事故发生。即不允许用户修改底层框架使用的东西
 */
public class JobInfo<R> {
    //区分唯一的工作
    private final String jobName;
    //工作的任务个数
    private final int jobLength;
    //这个工作的任务处理器
    private final ITaskProcesser<?, ?> taskProcesser;
    //成功处理的任务数
    private final AtomicInteger successCount;
    //已处理的任务数
    private final AtomicInteger taskProcesserCount;
    //拿结果从头拿，放结果从尾部放 保存工作中每个任务的返回结果,只有处理完成成放进去
    private final LinkedBlockingDeque<TaskResult<R>> taskDetailQueue;
    //工作的完成保存的时间，超过这个时间从缓存中清除
    private final long expireTime;

    //阻塞队列不应该由调用者传入，应该内部生成，长度为工作的任务个数
    public JobInfo(String jobName, int jobLength,
                   ITaskProcesser<?, ?> taskProcesser,
                   long expireTime) {
        super();
        this.jobName = jobName;
        this.jobLength = jobLength;
        this.taskProcesser = taskProcesser;
        this.successCount = new AtomicInteger(0);
        this.taskProcesserCount = new AtomicInteger(0);
        this.taskDetailQueue = new LinkedBlockingDeque<>(jobLength);
        this.expireTime = expireTime;
    }

    //返回成功处理的结果数
    //不能直接返回 successCount 因为这是一个引用，会导致线程不安全，以下同理
    public int getSuccessCount() {
        return successCount.get();
    }

    //返回当前已处理的结果数
    public int getTaskProcesserCount() {
        return taskProcesserCount.get();
    }

    //提供工作中失败的次数，为了方便调用者使用
    public int getFailCount() {
        return taskProcesserCount.get() - successCount.get();
    }

    //获得工作中每个任务的处理详情
    //注意由于阻塞队列是加锁的，所以不存在写的时候读，多线程冲突
    public List<TaskResult<R>> getTaskDetail() {
        List<TaskResult<R>> taskList = new LinkedList<>();
        TaskResult<R> taskResult;
        //从阻塞队列中拿任务的结果，反复取，一直取到null为止，说明目前队列中最新的任务结果已经取完，可以不取了
        //既然是任务结果，肯定已经处理完成了，所以全部拿完是对的
        while ((taskResult = taskDetailQueue.pollFirst()) != null) {
            taskList.add(taskResult);
        }
        return taskList;
    }

    //从业务应用角度来说，保证最终一致性即可，不需要对方法加锁.
    //注意 从jvm内存角度，只有传递进来的参数需要操作的时候才需要新建一个变量接受，即栈变量
    public void addTaskResult(TaskResult<R> result, CheckJobProcesser checkJob) {
        if (TaskResultType.Success.equals(result.getResultType())) {
            successCount.incrementAndGet();
        }
        taskDetailQueue.addLast(result);
        taskProcesserCount.incrementAndGet();

        //工作队列满了，放入过期队列
        if (taskProcesserCount.get() == jobLength) {
            checkJob.putJob(jobName, expireTime);
        }

//        if (taskProcesserCount.get() >= jobLength) {
//            if (result.getReturnValue() == null) {
//                System.out.println("chen: " + taskProcesserCount.get());
//            } else {
//                System.out.println("chen: " + taskProcesserCount.get() + " : " + result.getReturnValue());
//            }
//
//        }
    }

    public String getTotalProcess() {
        return "Success[" + successCount.get() + "]/Current["
                + taskProcesserCount.get() + "] Total[" + jobLength + "]";
    }

    public ITaskProcesser<?, ?> getTaskProcesser() {
        return taskProcesser;
    }


}
