package com.njust.pdfmutithread.user;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class PDFService {
    private String font = "simhei.ttf";
    private static Configuration freemarkerCfg = null;
    @Value("${DEST}")
    private String dest;

    @Value("${HTML}")
    private String html;

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public static Configuration getFreemarkerCfg() {
        return freemarkerCfg;
    }

    public static void setFreemarkerCfg(Configuration freemarkerCfg) {
        PDFService.freemarkerCfg = freemarkerCfg;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void createPdf(String content, String dest) throws IOException, DocumentException {
        // step 1
        Document document = new Document();
        FileOutputStream fileOutputStream = new FileOutputStream(dest);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, fileOutputStream);
        // step 3
        document.open();
        // step 4
        XMLWorkerFontProvider fontImp = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
        fontImp.register(font);
        XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                new ByteArrayInputStream(content.getBytes("UTF-8")), null, Charset.forName("UTF-8"), fontImp);
        // step 5
        document.close();

    }

    /**
     * freemarker渲染html
     */
    public String freeMarkerRender(Map<String, Object> data, String htmlTmp) {
        Writer out = new StringWriter();

        try {
            // 获取模板,并设置编码方式
            setFreemarkerCfg();
            Template template = freemarkerCfg.getTemplate(htmlTmp);
            template.setEncoding("UTF-8");
            //将合并后的数据和模板写入到流中，这里使用的字符流
            template.process(data, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 设置freemarkerCfg
     */
    private void setFreemarkerCfg() {
        freemarkerCfg = new Configuration();
        //freemarker的模板目录
        try {
            freemarkerCfg.setDirectoryForTemplateLoading(new ClassPathResource("template").getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
