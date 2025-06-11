package com.karrycode.cfpolsbackend;

import io.milvus.client.MilvusClient;

/**
 * @Author KarryLiu
 * @Creed may all the beauty be blessed
 * @Date 2025/2/14 10:41
 * @PackageName com.karrycode.cfpolsbackend
 * @ClassName MilvusTest
 * @Description
 * @Version 1.0
 */

public class MilvusTest extends CfPolsBackendApplicationTests {
    String CLUSTER_ENDPOINT = "http://localhost:19530";
    String TOKEN = "root:Milvus";
    private MilvusClient milvusClient;

}
