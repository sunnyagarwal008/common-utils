/*
 *  @version     1.0, Jan 20, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import in.bucheeng.common.utils.fileparser.DelimitedFileParser;
import in.bucheeng.common.utils.fileparser.Row;

public class FileUtils {

    /**
     *
     */
    private static final String CLASSPATH_FILE_PREFIX = "classpath:";

    /**
     * @param filePath
     * @return
     * @throws java.io.IOException
     */
    public static String getFileAsString(String filePath) throws IOException {
        InputStream inputStream = null;
        try {
            if (filePath.startsWith(CLASSPATH_FILE_PREFIX)) {
                inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath.substring(CLASSPATH_FILE_PREFIX.length()));
            } else {
                inputStream = new FileInputStream(filePath);
            }
            return getFileAsString(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public static void safelyCloseInputStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void safelyCloseOutputStream(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static File downloadFile(String sourcePath, String destinationPath) {
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            URL Url = new URL(sourcePath);
            byte[] buf;
            outStream = new BufferedOutputStream(new FileOutputStream(destinationPath));
            URLConnection uCon = Url.openConnection();
            inStream = uCon.getInputStream();
            int bytesRead = 0;
            buf = new byte[1000];
            while ((bytesRead = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, bytesRead);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(destinationPath);
    }

    public static File[] getFiles(String directoryName, final String pattern) {
        File dir = new File(directoryName);
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.matches(pattern);
            }
        });
    }

    public static String getFileAsString(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void writeToFile(String path, String data, boolean overwrite) throws IOException {
        File file = new File(path);
        if (!file.exists() || overwrite) {
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data.getBytes("UTF-8"));
        }
    }

    public static void appendToFile(String path, String data) throws IOException {
        File file = new File(path);
        org.apache.commons.io.FileUtils.writeByteArrayToFile(file, data.getBytes("UTF-8"), true);
    }

    public static void concatFiles(String outFilePath, List<String> inputFiles, String separator) throws IOException {
        OutputStream outStream = null;
        try {
            outStream = new BufferedOutputStream(new FileOutputStream(outFilePath));
            for (int i = 0; i < inputFiles.size(); i++) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(inputFiles.get(i));
                    copyStream(inputStream, outStream);
                } finally {
                    safelyCloseInputStream(inputStream);
                }
                if (i < inputFiles.size() - 1) {
                    outStream.write(separator.getBytes("UTF-8"));
                }
            }
        } finally {
            safelyCloseOutputStream(outStream);
        }
    }

    public static void writeToFile(String path, String data) throws IOException {
        writeToFile(path, data, false);
    }

    public static byte[] toByteArray(InputStream stream) throws IOException {
        return IOUtils.toByteArray(stream);
    }

    public static void write(byte[] data, OutputStream output) throws IOException {
        IOUtils.write(data, output);
    }

    public static void copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        WritableByteChannel outputChannel = Channels.newChannel(outputStream);
        copyChannel(inputChannel, outputChannel);
    }

    public static void copyChannel(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    public static String mergeCSVs(List<String> csvs, boolean containsHeader) {
        StringBuilder consolidatedCSV = new StringBuilder();
        boolean headerIncluded = false;
        for (String csv : csvs) {
            if (StringUtils.isNotBlank(csv)) {
                String[] rows = csv.split("\n");
                int startIndex = 0;
                if (containsHeader && !headerIncluded) {
                    headerIncluded = true;
                } else if (headerIncluded) {
                    startIndex = 1;
                }
                for (int i = startIndex; i < rows.length; i++) {
                    consolidatedCSV.append(rows[i]);
                    consolidatedCSV.append("\n");
                }
            }
        }
        return consolidatedCSV.toString();
    }

    public static void main(String[] args) throws IOException {
        String inputFile = "/tmp/1431062120397-buildabazaar.csv";
        String outFile = "/Users/sunny/Desktop/buildabazaar.csv";
        unzip(inputFile, outFile);
    }

    public static void csvToXls(String csvFilePath, String xlsFilePath) throws IOException {
        FileOutputStream out = null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            DelimitedFileParser.RowIterator iterator = new DelimitedFileParser(csvFilePath, DelimitedFileParser.DEFAULT_DELIMITER, '\"', false).parse();
            int rowNum = 0;
            while (iterator.hasNext()) {
                Row row = iterator.next();
                org.apache.poi.ss.usermodel.Row xlsRow = sheet.createRow(rowNum++);
                int cellnum = 0;
                for (String columnValue : row.getColumnValues()) {
                    Cell cell = xlsRow.createCell(cellnum++);
                    cell.setCellValue(columnValue);
                }
            }
            out = new FileOutputStream(new File(xlsFilePath));
            workbook.write(out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void unzip(String zipFile, String outputfile) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        File newFile = new File(outputfile);
        FileOutputStream fos = new FileOutputStream(newFile);
        try {
            zis.getNextEntry();
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } finally {
            FileUtils.safelyCloseOutputStream(fos);
            zis.closeEntry();
            zis.close();
        }
    }

    public static void zip(String fileOrDirectory, String outputfile) throws IOException {
        File destinationFile = new File(outputfile);
        File sourceFile = new File(fileOrDirectory);
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(new FileOutputStream(destinationFile));
            if (sourceFile.isDirectory()) {
                addDirectoryToZipStream(sourceFile, zipOutputStream, "");
            } else if (sourceFile.isFile()) {
                addFileToZipStream(sourceFile, zipOutputStream, ".");
            } else {
                throw new IllegalArgumentException("Invalid file path");
            }
        } finally {
            FileUtils.safelyCloseOutputStream(zipOutputStream);
        }
    }

    /**
     * @param sourceFile
     * @param zipOutputStream
     * @param parentFolderPath
     * @throws java.io.IOException
     */
    private static void addDirectoryToZipStream(File sourceFile, ZipOutputStream zipOutputStream, String parentFolderPath) throws IOException {
        zipOutputStream.putNextEntry(new ZipEntry(parentFolderPath + "/" + sourceFile.getName() + "/"));
        for (File file : sourceFile.listFiles()) {
            if (file.isDirectory()) {
                addDirectoryToZipStream(file, zipOutputStream, parentFolderPath + "/" + sourceFile.getName());
            } else {
                addFileToZipStream(file, zipOutputStream, parentFolderPath + "/" + sourceFile.getName());
            }
        }
        zipOutputStream.closeEntry();
    }

    /**
     * @param sourceFile
     * @param zipOutputStream
     * @param parentFolderPath
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private static void addFileToZipStream(File sourceFile, ZipOutputStream zipOutputStream, String parentFolderPath) throws FileNotFoundException, IOException {
        FileInputStream sourceStream = null;
        try {
            zipOutputStream.putNextEntry(new ZipEntry(parentFolderPath + "/" + sourceFile.getName()));
            sourceStream = new FileInputStream(sourceFile);
            FileUtils.copyStream(sourceStream, zipOutputStream);
            zipOutputStream.closeEntry();
        } finally {
            FileUtils.safelyCloseInputStream(sourceStream);
        }
    }

    public static void deleteFileOrDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File file : fileOrDirectory.listFiles()) {
                FileUtils.deleteFileOrDirectory(file);
            }
            if (fileOrDirectory.list().length == 0) {
                fileOrDirectory.delete();
            }
        } else {
            fileOrDirectory.delete();
        }
    }

    public static String normalizeFilePath(String directoryPath, String fileName) {
        return (directoryPath.endsWith("/") ? directoryPath : directoryPath + "/") + fileName;
    }

}
