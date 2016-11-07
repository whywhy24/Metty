package com.metty.rpc.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by j_zhan on 2016/11/1.
 */
public class HttpClientCase {

    public static void main(String[] args) throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        httpParams.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
        httpParams.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);
        httpParams.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true);
        httpParams.setBooleanParameter(CoreConnectionPNames.SO_KEEPALIVE, true);
        httpParams.setLongParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 500L);

        PoolingClientConnectionManager pool = new PoolingClientConnectionManager();
        pool.setMaxTotal(100);
        pool.setDefaultMaxPerRoute(50);

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        int i = 1;
        while(true && i < 1000) {
            executorService.execute(new Task(pool, httpParams, i++));
        }
    }

    public static class Task implements Runnable {

        PoolingClientConnectionManager pool;
        HttpParams httpParams;
        int i;

        public Task(PoolingClientConnectionManager pool, HttpParams httpParams, int i) {
            this.pool = pool;
            this.httpParams = httpParams;
            this.i = i;
        }

        @Override
        public void run() {
            HttpResponse response = null;
            try {
                System.out.println(i);
                HttpClient client = new DefaultHttpClient(pool, httpParams);
                HttpGet httpGet = new HttpGet("http://www.baidu.com");
                response = client.execute(httpGet);
                // handle response
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    try {
                        EntityUtils.consume(response.getEntity());
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }
}
