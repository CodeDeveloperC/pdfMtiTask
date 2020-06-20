package com.njust.pdfmutithread.user.task;


import com.itextpdf.text.DocumentException;
import com.njust.pdfmutithread.system.vo.ITaskProcesser;
import com.njust.pdfmutithread.system.vo.TaskResult;
import com.njust.pdfmutithread.system.vo.TaskResultType;
import com.njust.pdfmutithread.user.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen
 * @version 1.0
 * @date 2020/3/26 22:06
 * @description: 多线程版生成PDF
 */
@Component
public class PDFTaskMutiThread implements ITaskProcesser<String, Integer> {

    @Autowired
    private PDFService pdfService;

    @Override
    public TaskResult<Integer> taskExecute(String data) {
        createOne(data + ".pdf");
        return new TaskResult<>(TaskResultType.Success, Integer.valueOf(data));
    }

    public void createOne(String fileName) {
        //需要填充的数据
        Map<String, Object> data = new HashMap<>(16);
        String content = pdfService.freeMarkerRender(data, pdfService.getHtml());
        // 当content 为null时 ，不停尝试
        while (content == null) {
            content = pdfService.freeMarkerRender(data, pdfService.getHtml());
        }
        String temp = pdfService.getDest() + fileName;
        //创建pdf
        try {
            pdfService.createPdf(content, temp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
}
