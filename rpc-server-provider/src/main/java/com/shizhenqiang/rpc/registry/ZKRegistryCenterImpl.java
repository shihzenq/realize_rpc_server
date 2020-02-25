package com.shizhenqiang.rpc.registry;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * 服务注册在zookeeper中
 */
public class ZKRegistryCenterImpl implements RegistryCenter{

    CuratorFramework curatorFramework = null;

    {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZKConfig.SERVICE_ADDRESS)
                .connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace("registry")
                .build();
        curatorFramework.start();
    }

    @Override
    public void registryService(String serviceName, String serviceAddress) {
        String path = "/" + serviceName;
        try {
            if (curatorFramework.checkExists().forPath(path) == null) {
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
            String serviceAddressPath = path + "/" + serviceAddress;
            curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(serviceAddressPath);
            System.out.println("注册成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
