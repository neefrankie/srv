package com.github.neefrankie.srv.connector.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {
    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte HT = (byte) '\t';
    private static final byte COLON = (byte) ':';
    private static final int LC_OFFSET = 'A' - 'a';

    // Internal buffer.
    protected byte buf[];
    // Last valid byte.
    protected int count;
    // Position in hte buffer.
    protected int pos;

    protected InputStream is;

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        buf = new byte[bufferSize];
    }

    public int available() throws IOException {
        return (count - pos) + is.available();
    }

    public void close() throws IOException {
        if (is == null) {
            return;
        }

        is.close();
        is = null;
        buf = null;
    }

    // Fill the internal buffer using the data
    // from the underlying input stream.
    protected void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = is.read(buf, 0, buf.length);
        if (nRead > 0) {
            count = nRead;
        }
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }

        return buf[pos++] & 0xff;
    }

    public void readRequestLine(HttpRequestLine requestLine) throws IOException {
        if (requestLine.methodEnd != 0) {
            requestLine.recycle();
        }

        // Checking for a blank line.
        int chr = 0;
        // Skipping CR or LF
        do {
            try {
                chr = read();
            } catch (IOException e) {
                chr = -1;
            }
        } while ((chr == CR || chr == LF));

        if (chr == -1) {
            throw new EOFException("Couldn't read line");
        }

        pos--;

        // Reading the method name.
        int maxRead = requestLine.method.length;
        int readStart = pos;
        int readCount = 0;

        boolean space = false;

        while (!space) {
            // If the buffer is full, extend it.
            if (readCount >= maxRead) {
                if ((2*maxRead) <= HttpRequestLine.MAX_METHOD_SIZE) {
                    char[] newBuffer = new char[2 * maxRead];
                    System.arraycopy(requestLine.method, 0, newBuffer, 0, maxRead);
                    requestLine.method = newBuffer;
                    maxRead = requestLine.method.length;
                } else {
                    throw new IOException("Line too long");
                }
            }

            // We're at the end of the internal buffer.
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException("Couldn't read line");
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.method[readCount] = (char) buf[pos];
            readCount++;
            pos++;
        }
    }
}
