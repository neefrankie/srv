package com.github.neefrankie.srv.connector.http;

final public class HttpHeader {

    public static final int INITIAL_NAME_SIZE = 32;
    public static final int INITIAL_VALUE_SIZE = 64;
    public static final int MAX_NAME_SIZE = 128;
    public static final int MAX_VALUE_SIZE = 4096;

    public char[] name;
    public int nameEnd;
    public char[] value;
    public int valueEnd;

    protected int hashCode = 0;

    public HttpHeader(
        char[] name,
        int nameEnd,
        char[] value,
        int valueEnd
    ) {
        this.name = name;
        this.nameEnd = nameEnd;
        this.value = value;
        this.valueEnd = valueEnd;
    }

    public HttpHeader(String name, String value) {
        this.name = name.toLowerCase().toCharArray();
        this.nameEnd = name.length();
        this.value = value.toCharArray();
        this.valueEnd = value.length();
    }

    public HttpHeader() {
        this(
            new char[INITIAL_NAME_SIZE],
            0,
            new char[INITIAL_VALUE_SIZE],
            0
        );
    }

    public void recycle() {
        nameEnd = 0;
        valueEnd = 0;
        hashCode = 0;
    }

    public boolean equals(char[] buf, int end) {
        if (end != nameEnd) {
            return false;
        }

        for (int i = 0; i < end; i++) {
            if (buf[i] != name[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(char[] buf) {
        return equals(buf, buf.length);
    }

    public boolean equals(String str) {
        return equals(str.toCharArray(), str.length());
    }

    public boolean equals(HttpHeader header) {
        return equals(header.name, header.nameEnd);
    }

    public boolean headerEquals(HttpHeader header) {
        return equals(header.name, header.nameEnd) && valueEquals(header.value, header.valueEnd);
    }

    public boolean valueEquals(char[] buf, int end) {
        if (end != valueEnd) {
            return false;
        }
        for (int i = 0; i < end; i++) {
            if (buf[i] != value[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean valueEquals(char[] buf) {
        return valueEquals(buf, buf.length);
    }

    public boolean valueEquals(String str) {
        return valueEquals(str.toCharArray(), str.length());
    }

    public boolean valueIncludes(char[] buf, int end) {
        for (int i = 0; i < valueEnd; i++) {
            if ((i + end) > valueEnd) {
                return false;
            }

            for (int j = 0; j < end; j++) {
                if (value[i+j] != buf[j]) {
                    break;
                }

                if (j == (end - 1)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean valueIncludes(String str) {
        return valueIncludes(str.toCharArray(), str.length());
    }
}
