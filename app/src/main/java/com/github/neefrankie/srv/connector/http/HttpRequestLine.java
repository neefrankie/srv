package com.github.neefrankie.srv.connector.http;

final class HttpRequestLine {
    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 64;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 1024;
    public static final int MAX_URI_SIZE = 32768;
    public static final int MAX_PROTOCOL_SIZE = 1024;

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine(
        char[] method,
        int methodEnd,
        char[] uri,
        int uriEnd,
        char[] protocol,
        int protocolEnd
    ) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public HttpRequestLine() {
        this(
            new char[INITIAL_METHOD_SIZE],
            0,
            new char[INITIAL_URI_SIZE],
            0,
            new char[INITIAL_PROTOCOL_SIZE],
            0
        );
    }

    public void recycle() {
        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    // Test if the uri includes the given char array.
    public int indexOf(char[] buf) {
        int len = buf.length;

        for (int i = 0; i < uriEnd; i++) {
            // If the sub-array exceeds the outer loop.
            if ((i + len) > uriEnd) {
                return -1;
            }

            // Start comparing from the outer loop's position.
            for (int j = 0; j < len; j++) {
                if (uri[i+j] != buf[j]) {
                    break;
                }

                // Reached the end of sub char array
                // and no mismatch found.
                if (j == (len-1)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public int indexOf(String str) {
        return indexOf(str.toCharArray());
    }
}
