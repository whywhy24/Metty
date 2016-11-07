package com.metty.rpc.netty;

import com.metty.rpc.handler.EmptyHandler;
import com.metty.rpc.handler.RequestHandler;
import com.metty.rpc.handler.SearchHandler;
import com.metty.rpc.handler.TestHandler;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by j_zhan on 2016/11/7.
 */
public class HttpRequestParser implements RequestParser<HttpRequest> {

    private final Map<HttpMethod, Map<String, Class<? extends RequestHandler>>> handlerClasses = new HashMap<HttpMethod, Map<String, Class<? extends RequestHandler>>>();

    public HttpRequestParser() {
        addHandler(HttpMethod.GET, "/favicon.ico", EmptyHandler.class);
        addHandler(HttpMethod.GET, "/test", TestHandler.class);
        addHandler(HttpMethod.GET, "/search", SearchHandler.class);
        addHandler(HttpMethod.POST, "/search", SearchHandler.class);
    }

    @Override
    public RequestHandler parse(HttpRequest httpRequest) throws Exception {
        Map<String, Class<? extends RequestHandler>> subHandlerClasses = handlerClasses.get(httpRequest.method());
        if (subHandlerClasses == null) {
            throw new IllegalAccessException("Illegal http method: " + httpRequest.method());
        }
        String uri = getUri(httpRequest.uri().toLowerCase());
        Class<? extends RequestHandler> handlerClass = subHandlerClasses.get(uri);
        if (handlerClass == null) {
            throw new IllegalAccessException("Illegal http request:[method]" + httpRequest.method() + ",[uri]" + httpRequest.uri());
        }
        return handlerClass.newInstance();
    }

    private static String getUri(String url) {
        int pos = url.indexOf('?');
        if(pos >= 0){
            url = url.substring(0, pos);
        }

        if(url.endsWith("/")){
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    protected void addHandler(HttpMethod httpMethod, String uri,  Class<? extends RequestHandler> handler) {
        uri = uri.toLowerCase();
        Map<String, Class<? extends RequestHandler>> subHandlerClass = handlerClasses.get(httpMethod);
        if (subHandlerClass == null) {
            subHandlerClass = new HashMap<String, Class<? extends RequestHandler>>();
            handlerClasses.put(httpMethod, subHandlerClass);
        }
        if (uri.endsWith("/")) {
            uri = uri.substring(0, uri.length() - 1);
        }

        Class handlerClass = subHandlerClass.get(uri);
        if (handlerClass != null) {
            throw new IllegalArgumentException("handler:[" + httpMethod + " " + uri + " " + handlerClass + "] has already existed");
        }
        subHandlerClass.put(uri, handler);
    }
}
