package com.github.neefrankie.srv;

import com.github.neefrankie.srv.connector.http.HttpRequest;
import com.github.neefrankie.srv.connector.http.HttpRequestFacade;
import com.github.neefrankie.srv.connector.http.HttpResponse;
import com.github.neefrankie.srv.connector.http.HttpResponseFacade;
import jakarta.servlet.Servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor {

    public void process(HttpRequest req, HttpResponse res) {
        String uri = req.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;

        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            // How classpath works: https://docs.oracle.com/javase/tutorial/java/package/managingfiles.html
            File classPath = new File(Constants.CLASS_PATH);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);

        } catch (IOException e) {
            System.out.println(e.toString());
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass("com.github.neefrankie.srv.servlet." + servletName);
        } catch (ClassNotFoundException e) {
            System.out.println(e.toString());
        }

        Servlet servlet = null;
        HttpRequestFacade requestFacade = new HttpRequestFacade(req);
        HttpResponseFacade responseFacade = new HttpResponseFacade(res);

        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service(requestFacade, responseFacade);
        } catch (Exception e) {
            System.out.println(e.toString());
        } catch (Throwable e) {
            System.out.println(e.toString());
        }
    }
}
