package com.ooliuyue.simplRpc.test;

import com.ooliuyue.simplRpc.rpc.SimpleRpc;

/**
 * @Auther: ly
 * @Date: 2019/3/14 11:43
 */

public class Client {
    public static void main(String[] args) throws Exception {
        HelloService remoteService = SimpleRpc.getRemoteService(HelloService.class, "192.168.56.1", 1000);
        String str = remoteService.hello("王大锤");
        System.out.println(str);
    }

}
