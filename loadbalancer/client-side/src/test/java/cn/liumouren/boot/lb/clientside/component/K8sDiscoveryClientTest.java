package cn.liumouren.boot.lb.clientside.component;

import org.junit.jupiter.api.Test;

/**
 *
 *
 * @author freeman
 * @date 2021/12/20 15:43
 */
public class K8sDiscoveryClientTest {
    @Test
    public void test_k8sApi(){
        K8sDiscoveryClient client = new K8sDiscoveryClient();
        System.out.println(client.getInstances("kube-dns"));
    }

    @Test
    public void test_k8sApi_getServices(){
        K8sDiscoveryClient client = new K8sDiscoveryClient();
        System.out.println(client.getServices());
    }
}
