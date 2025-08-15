package com.gkmonk.pos.utils;

import com.gkmonk.pos.model.BarcodeRequest;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.counter.event.IMetaInfo;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.text.DocumentException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrintUtilsBarcode {

    public static String getContent(BarcodeRequest barcodeRequest){
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.init();

        Template template = velocity.getTemplate("templates/barcode.vm");


        VelocityContext context = new VelocityContext();
        context.put("productId", barcodeRequest.getProductId());
        context.put("productName", barcodeRequest.getProductName().length() > 50 ? barcodeRequest.getProductName().substring(0,49): barcodeRequest.getProductName());
        context.put("variant", "");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();

    }

    public static void getPrintableContent(HttpServletResponse responseHTTP, String content, String fileName) throws FileNotFoundException, IOException {
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(fileName));
        PdfDocument pdfDocument =  new PdfDocument(pdfWriter, new DocumentProperties().setEventCountingMetaInfo(new HtmlMetaInfo()));
        PdfFont pdfFont = PdfFontFactory.createFont("Helvetica");
        pdfDocument.addFont(pdfFont);
        PageSize pageSize = new PageSize(384, 96);
        pdfDocument.setDefaultPageSize(pageSize);
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setImmediateFlush(false);
        Document document = HtmlConverter.convertToDocument(content, pdfDocument,converterProperties);
        document.setMargins(0, 0, 0, 0);
        document.relayout();
        document.close();


       // HtmlConverter.convertToPdf(content, pdfDocument,null);
        Path file = Paths.get("", fileName);
        
		if (Files.exists(file)) {
    			responseHTTP.setContentType("application/pdf");
			responseHTTP.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			try {
				OutputStream out = responseHTTP.getOutputStream();
                
				Files.copy(file, out);
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
    }


    public static ByteArrayOutputStream createPdf(String htmlSrc) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    ConverterProperties converterProperties = new ConverterProperties();
    converterProperties.setBaseUri(new File(htmlSrc).getParent());
    PdfWriter writer = new PdfWriter(output);
    PdfDocument pdfDocument = new PdfDocument(writer);
    PdfMerger merger = new PdfMerger(pdfDocument);
    for(int x=0; x < 3; x++){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument temp = new PdfDocument(new PdfWriter(baos));
        temp.setDefaultPageSize(PageSize.A10);
        HtmlConverter.convertToPdf(new FileInputStream(htmlSrc), temp, converterProperties);
        temp = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
        merger.merge(temp, 1, temp.getNumberOfPages());
        temp.close();
    }
    pdfDocument.close();

    return output;
}

    public static String generatePDF(String content, ByteArrayOutputStream pdfOutputStream) throws DocumentException, IOException {
       String fileName = "temp" + Math.random() + ".pdf";
        PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(fileName));
        PdfDocument pdfDocument =  new PdfDocument(pdfWriter, new DocumentProperties().setEventCountingMetaInfo(new HtmlMetaInfo()));
        PdfFont pdfFont = PdfFontFactory.createFont("Helvetica");
        pdfDocument.addFont(pdfFont);

        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setImmediateFlush(false);
        Document document = HtmlConverter.convertToDocument(content, pdfDocument,converterProperties);
        PageSize pageSize = new PageSize(384, 96);
        pdfDocument.setDefaultPageSize(pageSize);


        document.setMargins(0, 0, 0, 0);
        document.relayout();

        document.close();
        return fileName;
    }


    public void scalePdf(String dest, ByteArrayInputStream input, float scale) throws IOException {
    // Create the source document
    PdfDocument srcDoc = new PdfDocument(new PdfReader(input));
    PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
    ScaleDownEventHandler eventHandler = new ScaleDownEventHandler(scale);
    int n = srcDoc.getNumberOfPages();
    pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, eventHandler);

    PdfCanvas canvas;
    PdfFormXObject page;
    for (int p = 1; p <= n; p++) {
        eventHandler.setPageDict(srcDoc.getPage(p).getPdfObject());
        canvas = new PdfCanvas(pdfDoc.addNewPage());
        page = srcDoc.getPage(p).copyAsFormXObject(pdfDoc);
        canvas.addXObject(page, scale, 0f, 0f, scale, 0f, 0f);
    }

    pdfDoc.close();
    srcDoc.close();
}
class ScaleDownEventHandler implements IEventHandler {
    protected float scale = 1;
    protected PdfDictionary pageDict;

    public ScaleDownEventHandler(float scale) {
        this.scale = scale;
    }

    public void setPageDict(PdfDictionary pageDict) {
        this.pageDict = pageDict;
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfPage page = docEvent.getPage();
        page.put(PdfName.Rotate, pageDict.getAsNumber(PdfName.Rotate));

        scaleDown(page, pageDict, PdfName.MediaBox, scale);
        scaleDown(page, pageDict, PdfName.CropBox, scale);
    }

    protected void scaleDown(PdfPage destPage, PdfDictionary pageDictSrc, PdfName box, float scale) {
        PdfArray original = pageDictSrc.getAsArray(box);
        if (original != null) {
            float width = original.getAsNumber(2).floatValue() - original.getAsNumber(0).floatValue();
            float height = original.getAsNumber(3).floatValue() - original.getAsNumber(1).floatValue();
            PdfArray result = new PdfArray();
            result.add(new PdfNumber(0));
            result.add(new PdfNumber(0));
            result.add(new PdfNumber(width * scale));
            result.add(new PdfNumber(height * scale));
            destPage.put(box, result);
        }
    }

  
}
    private static class HtmlMetaInfo implements IMetaInfo {

        private static final long serialVersionUID = -295587336698550627L;
    }

}
