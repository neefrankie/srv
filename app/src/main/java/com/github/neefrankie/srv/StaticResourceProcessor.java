package com.github.neefrankie.srv;

import java.io.IOException;

public class StaticResourceProcessor {
    public void process(Request req, Response res) {
        try {
            res.sendStaticResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
