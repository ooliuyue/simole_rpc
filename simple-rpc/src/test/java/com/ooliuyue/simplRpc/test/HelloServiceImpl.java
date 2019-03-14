package com.ooliuyue.simplRpc.test;

/**
 * @Auther: ly
 * @Date: 2019/3/14 11:45
 */

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
