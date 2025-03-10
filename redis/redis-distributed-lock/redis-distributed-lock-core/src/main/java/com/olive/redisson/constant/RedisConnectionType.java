package com.olive.redisson.constant;

/**
 * @description: Redis连接方式
 * 包含:standalone-单节点部署方式
 * sentinel-哨兵部署方式
 * cluster-集群方式
 * masterslave-主从部署方式
 * @program: dtq
 * @author: dtq
 * @create: 2021/8/12 16:03
 */
public enum RedisConnectionType {
    STANDALONE("standalone", "单节点部署方式"),
    SENTINEL("sentinel", "哨兵部署方式"),
    CLUSTER("cluster", "集群方式"),
    MASTER_SLAVE("master_slave", "主从部署方式");

    private final String connectionType;
    private final String connectionDesc;

    RedisConnectionType(String connectionType, String connectionDesc) {
        this.connectionType = connectionType;
        this.connectionDesc = connectionDesc;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getConnectionDesc() {
        return connectionDesc;
    }
}
