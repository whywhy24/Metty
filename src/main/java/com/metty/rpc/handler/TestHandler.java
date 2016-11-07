package com.metty.rpc.handler;

/**
 * Created by j_zhan on 2016/11/7.
 */
public class TestHandler extends SimpleHttpRequestHandler<String> {
    @Override
    public String doRun() throws Exception {
        return "11111";
    }
}
