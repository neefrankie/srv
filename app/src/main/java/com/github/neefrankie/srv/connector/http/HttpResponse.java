package com.github.neefrankie.srv.connector.http;

import com.github.neefrankie.srv.Constants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class HttpResponse implements ServletResponse {
    private static final int BUFFER_SIZE = 1024;
    HttpRequest request;
    OutputStream output;
    PrintWriter writer;
    protected byte[] buffer = new byte[BUFFER_SIZE];
    protected int bufferCount = 0;

    // Has this response been committed yet?
    protected boolean committed = false;
    // The actual number of bytes written to this Response
    protected int contentCount = 0;
    // The content length associated with this Response
    protected int contentLength = -1;
    // The content type associated with this Response
    protected String contentType = null;

    protected HashMap<String, ArrayList<String>> headers = new HashMap<>();

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public void setHeader(String name, String value) {
        if (isCommitted()) {
            return;
        }

        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        synchronized (headers) {
            headers.put(name, values);
        }

        String match = name.toLowerCase();
        if (match.equals("content-length")) {
            int contentLength = -1;
            try {
                contentLength = Integer.parseInt(value);
            } catch (NumberFormatException e) {

            }
            if (contentLength >= 0) {
                setContentLength(contentLength);
            }
        } else if (match.equals("content-type")) {
            setContentType(value);
        }
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;

        try {

            File file = new File(Constants.WEB_ROOT, request.getUri());
            System.out.println("Reading file " + file.getPath());

            if (file.exists()) {
                System.out.println("Sending file");
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                String errorMessage = """
                        HTTP/1.1 404 File Not Found
                        Content-Type: text/html
                        Content-Length: 23
                        
                        <h1>File Not Found</h1>
                        """;

                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }


    /**
     * Implementation of ServletResponse
     */
    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        // Passing true as the second argument will make any call to
        // println method flush the output. However, a print method
        // does not flush the output.
        writer = new PrintWriter(output, true);
        return writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {
        if (isCommitted()) {
            return;
        }

        this.contentLength = len;
    }

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }
}
