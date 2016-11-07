package com.metty.rpc.netty;

import com.metty.rpc.handler.RequestHandler;

/**
 * Created by j_zhan on 2016/11/7.
 */
public interface RequestParser<Request> {
    RequestHandler parse(Request request) throws Exception;
}
