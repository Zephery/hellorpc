package com.hellorpc.common;

import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

/**
 * @author wenzhihuai
 * @since 2018/8/17 15:51
 */
@Slf4j
@Service
public class EtcdServiceRegistry implements ServiceRegistry {

    @Value("${etcd.endpoint}")
    private String endpoint;

    @Override
    public void register(String serviceName, String serviceAddress) {
        try (EtcdClient client = new EtcdClient(
                URI.create(endpoint))) {
            EtcdKeysResponse response = client.put(serviceName, serviceAddress).send().get();

            // Prints out: bar
            System.out.println(response.node.value);
        } catch (IOException e) {
            log.error("", e);

        } catch (EtcdException | TimeoutException | EtcdAuthenticationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String discover(String name) {
        try (EtcdClient etcd = new EtcdClient(
                URI.create(endpoint))) {
            // Logs etcd version
            log.info(etcd.getVersion());
            EtcdResponsePromise<EtcdKeysResponse> send = etcd.get(name).send();
            String value = send.get().node.value;
            log.info("key is {}, value is {}", name, value);
            return value;
        } catch (IOException e) {
            log.error("", e);
            return null;
        } catch (EtcdException | TimeoutException | EtcdAuthenticationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
