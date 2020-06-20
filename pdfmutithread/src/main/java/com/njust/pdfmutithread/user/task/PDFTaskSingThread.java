package com.njust.pdfmutithread.user.task;


import com.itextpdf.text.DocumentException;
import com.njust.pdfmutithread.user.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen
 * @version 1.0
 * @date 2020/3/26 22:09
 * @description:
 */
@Component
public class PDFTaskSingThread {

    @Autowired
    private PDFService pdfService;

    public void createOne() {
        //需要填充的数据
        Map<String, Object> data = new HashMap<>(16);
        String content = pdfService.freeMarkerRender(data, pdfService.getHtml());
        String temp = pdfService.getDest() + "a.pdf";
        //创建pdf
        try {
            pdfService.createPdf(content, temp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void createManyWithOneThread(int num) {
        //需要填充的数据
        Map<String, Object> data = new HashMap<>(16);
        String content = pdfService.freeMarkerRender(data, pdfService.getHtml());

        if (num <= 0) {
            num = 10;
        }
        String temp = null;
        for (int i = 0; i < num; i++) {
            temp = pdfService.getDest() + i + ".pdf";
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
}
