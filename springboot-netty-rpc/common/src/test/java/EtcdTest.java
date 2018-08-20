import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.EtcdClient;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

/**
 * @author wenzhihuai
 * @since 2018/8/17 18:37
 */
@Slf4j
public class EtcdTest {

    @Test
    public void etcd() {
        try (EtcdClient etcd = new EtcdClient(
                URI.create("http://47.95.10.139:2379"))) {
            // Logs etcd version
            System.out.println(etcd.getVersion());
        } catch (IOException e) {
            log.error("", e);

        }
    }
}
