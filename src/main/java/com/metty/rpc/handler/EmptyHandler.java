package com.metty.rpc.handler;

/**
 * Created by j_zhan on 2016/11/7.
 */
public class EmptyHandler extends SimpleHttpRequestHandler<String> {

    @Override
    public String doRun() throws Exception {
        return null;
    }
}
