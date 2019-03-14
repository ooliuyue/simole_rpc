package com.ooliuyue.simplRpc.test;

/**
 * @Auther: ly
 * @Date: 2019/3/14 11:43
 */

import com.ooliuyue.simplRpc.rpc.SimpleRpc;

/**
 * 服务提供者
 */
public class Server {
    public static void main(String[] args) throws Exception {
        HelloService helloService = new  HelloServiceImpl();
        SimpleRpc.export(helloService,1000);
    }

}
