package com.shizhenqiang.rpc;

import lombok.Data;

import java.io.Serializable;

@Data
public class RpcRequest implements Serializable {

    private String className;

    private String methodName;

    private Class<?>[] paramTypes;

    private Object[] parameters;

    private String version;
}
