package com.github.tyang513.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 主要用来发送消息到Kafka队列
 *
 * @author guoping.zhou@tendcloud.com
 */
public class KafkaService {

    private static Logger logger = LoggerFactory.getLogger(KafkaService.class);
    private static Logger errorLogger = LoggerFactory.getLogger("error");

    private KafkaClient wifiKafkaClient;

    private boolean isWifiKafkaUseable = true;

    private static KafkaService _instance;

    private int KAFKA_CLIENT_CHECK_INTERVAL = 60;
    private int KAFKA_CLIENT_MAXERRORTIME = 10;

    public static final String SRC_TYPE_WIFI = "wifi";

    private KafkaService() {
    }

    ;

    public static KafkaService getInstance(String topic, String brokers) {
        if (_instance == null) {
            _instance = new KafkaService();
            _instance.wifiKafkaClient = new KafkaClient(SRC_TYPE_WIFI, brokers, topic);
            _instance.checkStart();
        }
        return _instance;
    }

    /**
     * 检查Kafka Client是否正常
     */
    private void checkStart() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        check();
                    } catch (Exception e) {
                        // TODO
                    } finally {
                        try {
                            Thread.sleep(KAFKA_CLIENT_CHECK_INTERVAL * 1000L);
                        } catch (InterruptedException e) {
                            errorLogger.error("====== checkStart.Thread.sleep error.", e);
                        }
                    }
                }
            }
        }).start();
    }

    private void check() {
        if (!isWifiKafkaUseable) {
            try {
                this.wifiKafkaClient.send(null, null);
                isWifiKafkaUseable = true;
            } catch (IOException e) {
                // do nothing
            }
        }

    }

    /**
     * @param srcType 预留字段,做数据来源区分,暂时只有wifi
     * @param data    数据
     * @param apMac   ap mac 地址
     */
    public void send(String srcType, byte[] data, String apMac) {
        if (data == null) {
            return;
        }
        KafkaClient kafkaClient;
        if (SRC_TYPE_WIFI.equalsIgnoreCase(srcType)) {
            if (!isWifiKafkaUseable) {
                throw new RuntimeException("WiFiKafka Unuseable.");
            }
            kafkaClient = wifiKafkaClient;
            boolean flag = doRealSend(kafkaClient, data, apMac);
            if (!flag) {
                isWifiKafkaUseable = false;
                throw new RuntimeException("Send data to Kafka (topic->" + kafkaClient.getTopic() + ")error.");
            }
        }
    }

    private boolean doRealSend(KafkaClient kafkaClient, byte[] ep, String deviceid) {
        int c = 0;
        while (c < this.KAFKA_CLIENT_MAXERRORTIME) {
            c++;
            try {
                kafkaClient.send(ep, deviceid);
                return true;
            } catch (Exception e) {
                errorLogger.error("====== 发送失败:" + c, e);
            }
        }
        return false;
    }

    public boolean isWifiKafkaUseable() {
        return isWifiKafkaUseable;
    }

    /**
     * Kfaka发送程序
     *
     * @author guoping.zhou@tendcloud.com
     */
    private static class KafkaClient {

        private static Logger logger = LoggerFactory.getLogger(KafkaClient.class);

        private String brokers;    // Kafka集群地址
        private String topic;    // Kafka队列名
        private Producer<String, byte[]> producer;    // Kafka消息生产者
        private String type;    // 备用字段,区分数据来源
        private boolean isUseable = true;

        KafkaClient(String type, String brokers, String topic) {
            this.type = type;
            this.brokers = brokers;
            this.topic = topic;

            Properties props = new Properties();
            logger.info("====== 初始化KafkaClient成功：brokers->{},topic->{}", brokers, topic);
            props.put("metadata.broker.list", brokers);
            props.put("key.serializer.class", "kafka.serializer.StringEncoder");
            this.producer = new Producer<String, byte[]>(new kafka.producer.ProducerConfig(props));

            logger.info("====== 初始化KafkaClient成功：brokers->{},topic->{}", brokers, topic);
        }


        void send(byte[] data, String apMac) throws IOException {
            if (apMac != null) {
                this.producer.send(new KeyedMessage<String, byte[]>(this.topic, apMac, data));
            } else {
                this.producer.send(new KeyedMessage<String, byte[]>(this.topic, data));
            }
        }

        public String getBrokers() {
            return brokers;
        }

        String getTopic() {
            return topic;
        }

        public String getType() {
            return type;
        }

        public boolean isUseable() {
            return isUseable;
        }

        public void setUseable(boolean isUseable) {
            this.isUseable = isUseable;
        }
    }
}
