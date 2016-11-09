package com.metty.rpc.handler;

/**
 * Created by j_zhan on 2016/11/7.
 */
public class SearchHandler extends SimpleHttpRequestHandler<byte[]> {

    @Override
    public byte[] doRun() throws Exception {
        return data;
    }
}
