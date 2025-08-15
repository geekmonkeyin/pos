package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.BarcodeRequest;
import com.gkmonk.pos.model.logs.TaskStatusType;
import com.gkmonk.pos.model.logs.TaskType;
import com.gkmonk.pos.services.logs.TaskLogsServiceImpl;
import com.gkmonk.pos.utils.PrintUtilsBarcode;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@RestController
@RequestMapping("/v1/barcode")
public class BarcodeController {

    @Autowired
    private TaskLogsServiceImpl taskLogsService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateBarcodePdf(@RequestBody BarcodeRequest request) {
        try {
            // Create a PDF document
            String metaData = "Barcode Generation for Product ID: " + request.getProductId() +
                    ", Quantity: " + request.getQuantity() + ", Date: " + LocalDate.now() + ", Label Quantity:"+request.getLabelCount();
            taskLogsService.addLogs(TaskType.BARCODE_GENERATION.name(), TaskStatusType.START.name(),metaData, LocalDate.now().toString());
            com.itextpdf.io.source.ByteArrayOutputStream pdfOutputStream = new com.itextpdf.io.source.ByteArrayOutputStream();

            //add content
            String content = PrintUtilsBarcode.getContent(request);
            String fileName = PrintUtilsBarcode.generatePDF(content,pdfOutputStream);
            // Return the PDF as a response

            // Return the PDF as a response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "barcodes.pdf"); // Set to "inline" to open in a new tab

            try {
                Path file = Paths.get("", fileName);
			  OutputStream out = pdfOutputStream;
			  Files.copy(file, out);
			  out.flush();
			  out.close();
			  } catch (IOException ex) {
			     ex.printStackTrace();
                taskLogsService.addLogs(TaskType.BARCODE_GENERATION.name(), TaskStatusType.FAILED.name(),metaData, LocalDate.now().toString());

            }
            taskLogsService.addLogs(TaskType.BARCODE_GENERATION.name(), TaskStatusType.COMPLETED.name(),metaData, LocalDate.now().toString());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfOutputStream.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private BufferedImage generateBarcodeImage(String productId) {
        try {
            Code128Bean barcodeGenerator = new Code128Bean();
            final int dpi = 150;

            // Configure the barcode generator
            barcodeGenerator.setModuleWidth(0.2);
            barcodeGenerator.doQuietZone(false);

            // Create the barcode image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    outputStream, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            barcodeGenerator.generateBarcode(canvas, productId);
            canvas.finish();

            return javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(outputStream.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}