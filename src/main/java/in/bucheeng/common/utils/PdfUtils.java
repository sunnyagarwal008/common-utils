/*
 *  @version     1.0, 19-Jun-2013
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.XMLResource;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

@SuppressWarnings("deprecation")
public class PdfUtils {

    public static String parsePdf(String filePath) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        try {
            int numberOfPages = reader.getNumberOfPages();
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i <= numberOfPages; i++) {
                PdfReaderContentParser parser = new PdfReaderContentParser(reader);
                TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                strategy = parser.processContent(i, strategy);
                builder.append(strategy.getResultantText());
            }
            return builder.toString();
        } finally {
            reader.close();
        }
    }

    public static int getNumberOfPages(String filePath) throws IOException {
        PdfReader reader = new PdfReader(filePath);
        try {
            return reader.getNumberOfPages();
        } finally {
            reader.close();
        }
    }

    public static void splitPDF(String inputFilePath, String outputFilePath, int fromPage, int toPage) throws DocumentException, IOException {
        Document document = new Document();
        OutputStream outputStream = new FileOutputStream(outputFilePath);
        PdfReader inputPDF = new PdfReader(inputFilePath);
        PdfWriter writer = null;
        try {
            int totalPages = inputPDF.getNumberOfPages();

            // make fromPage equals to toPage if it is greater
            if (fromPage > toPage) {
                fromPage = toPage;
            }
            if (toPage > totalPages) {
                toPage = totalPages;
            }

            // Create a writer for the outputstream
            writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cb = writer.getDirectContent(); // Holds the PDF data
            PdfImportedPage page;

            while (fromPage <= toPage) {
                document.newPage();
                page = writer.getImportedPage(inputPDF, fromPage);
                cb.addTemplate(page, 0, 0);
                fromPage++;
            }
            outputStream.flush();
            document.close();
        } finally {
            inputPDF.close();
            if (document.isOpen()) {
                document.close();
            }
            if (writer != null) {
                writer.close();
            }
            FileUtils.safelyCloseOutputStream(outputStream);
        }
    }

    public static void convertXMLFileToPDF(String xmlFilePath, String pdfFileName) throws Exception {
        try (OutputStream fos = new FileOutputStream(pdfFileName);) {
            convertXMLToPDF(xmlFilePath, fos);
        }
    }

    public static void convertXMLFileToPDF(String xmlFilePath, OutputStream outputStream) throws Exception {
        convertXMLToPDF(FileUtils.getFileAsString(xmlFilePath), outputStream);
    }

    public static void convertXMLToPDF(String xml, OutputStream outputStream) throws Exception {
        org.w3c.dom.Document document = XMLResource.load(new ByteArrayInputStream(xml.getBytes())).getDocument();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(document, null);
        renderer.layout();
        try {
            renderer.createPDF(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void imageToPdf(String imageFilePath, String fileOutputPath) throws DocumentException, MalformedURLException, IOException {
        FileOutputStream stream = null;
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        PdfWriter writer = null;
        try {
            stream = new FileOutputStream(fileOutputPath);
            writer = PdfWriter.getInstance(document, stream);
            writer.open();
            document.open();
            Image image = Image.getInstance(imageFilePath);
            image.scalePercent(40f);
            document.add(image);
        } finally {
            document.close();
            if (writer != null) {
                writer.close();
            }
            FileUtils.safelyCloseOutputStream(stream);
        }
    }

    public static void concatPDF(List<String> filePaths, String pdfOutputPath) throws IOException, DocumentException {
        FileOutputStream fis = null;
        try {
            fis = new FileOutputStream(pdfOutputPath);
            merge(filePaths, fis);
        } finally {
            FileUtils.safelyCloseOutputStream(fis);
        }
    }

    public static void merge(List<String> filePaths, OutputStream outputStream) {
        Document document = new Document();
        PdfCopy copy = null;
        try {
            copy = new PdfCopy(document, outputStream);
            document.open();
            for (String filePath : filePaths) {
                PdfReader reader = new PdfReader(filePath);
                try {
                    for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                        copy.addPage(copy.getImportedPage(reader, i));
                    }
                } finally {
                    reader.close();
                }
                outputStream.flush();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            document.close();
            if (copy != null) {
                copy.close();
            }
            FileUtils.safelyCloseOutputStream(outputStream);
        }
    }

    public static void main(String[] args) throws IOException, DocumentException {
        String path = "/Users/sunny/Downloads/a.pdf";
        List<String> filePaths = Arrays.asList(path, path);
        concatPDF(filePaths, "/Users/sunny/Downloads/aa.pdf");
    }

    public static void aggregatePdfsToA4(List<String> inputFilePaths, String size, float xOffset, float yOffset, String pdfOutputPath) {
        aggregatePdfsToA4(inputFilePaths, false, size, xOffset, yOffset, pdfOutputPath);
    }

    public static void aggregatePdfsToA4(List<String> inputFilePaths, boolean landscape, String size, float xOffset, float yOffset, String pdfOutputPath) {
        try {
            if ("A6".equals(size)) {
                aggregateA6Pdfs(inputFilePaths, landscape, xOffset, yOffset, pdfOutputPath);
            } else if ("A5".equals(size)) {
                aggregateA5Pdfs(inputFilePaths, landscape, xOffset, yOffset, pdfOutputPath);
            } else {
                throw new IllegalArgumentException("invalid size");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void aggregateA5Pdfs(List<String> inputFilePaths, boolean landscape, float xOffset, float yOffset, String pdfOutputPath) throws DocumentException, IOException {
        Document document = landscape ? new Document(PageSize.A4.rotate(), 0, 0, 0, 0) : new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(pdfOutputPath));
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            int index = 0;
            for (String inputLabel : inputFilePaths) {
                if (index == 2) {
                    index = 0;
                    document.newPage();
                }
                PdfReader reader = null;
                try {
                    reader = new PdfReader(inputLabel);
                    PdfImportedPage page = writer.getImportedPage(reader, 1);
                    canvas.addTemplate(page, 0, ((landscape ? -PageSize.A6.getWidth() : -PageSize.A6.getHeight()) + yOffset) * (index % 2));
                    index++;
                    writer.freeReader(reader);
                } finally {
                    reader.close();
                }
            }
        } finally {
            document.close();
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static void aggregateA6Pdfs(List<String> inputFilePaths, boolean landscape, float xOffset, float yOffset, String pdfOutputPath) throws DocumentException, IOException {
        Document document = landscape ? new Document(PageSize.A4.rotate(), 0, 0, 0, 0) : new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(pdfOutputPath));
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            int index = 0;
            for (String inputLabel : inputFilePaths) {
                if (index == 4) {
                    index = 0;
                    document.newPage();
                }
                PdfReader reader = null;
                try {
                    reader = new PdfReader(inputLabel);
                    PdfImportedPage page = writer.getImportedPage(reader, 1);
                    canvas.addTemplate(page, ((landscape ? PageSize.A6.getHeight() : PageSize.A6.getWidth()) - xOffset) * (index % 2), ((landscape ? -PageSize.A6.getWidth()
                            : -PageSize.A6.getHeight()) + yOffset) * (index / 2));
                    index++;
                    writer.freeReader(reader);
                } finally {
                    reader.close();
                }
            }
        } finally {
            document.close();
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static void convertHtmlToPdf(String htmlFilePath, String outputPdfPath) {
        Rectangle pageSize = PageSize.getRectangle("A4");
        Document document = new Document(pageSize);
        BufferedReader reader = null;
        OutputStream outputStream = null;
        HTMLWorker worker = null;
        try {
            reader = new BufferedReader(new FileReader(htmlFilePath));
            outputStream = new FileOutputStream(outputPdfPath);
            PdfWriter.getInstance(document, outputStream);
            document.open();
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line);
            }
            String htmlString = html.toString();
            worker = new HTMLWorker(document);
            worker.parse(new StringReader(htmlString));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            document.close();
            try {
                if (worker != null) {
                    worker.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
