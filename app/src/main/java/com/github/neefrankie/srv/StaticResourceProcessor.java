package com.github.neefrankie.srv;

import com.github.neefrankie.srv.connector.http.HttpRequest;
import com.github.neefrankie.srv.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor {
    public void process(HttpRequest req, HttpResponse res) {
        try {
            res.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
