package com.shizhenqiang.rpc;


@RpcService(value = HelloService.class, version = "v1.0")
public class HelloServiceImpl implements HelloService {
    @Override
    public String meetToSayHello(String name) {
        System.out.println("见到【name】" + name + ", 你好，hello啊");
        return "【v 1.0】NAME" + name;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("保存【user】信息：" + user);
        return "【v 1.0】SUCCESS";
    }
}
