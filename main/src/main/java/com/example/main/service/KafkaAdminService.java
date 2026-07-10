package com.example.main.service;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KafkaAdminService {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private static final String TOPIC = "excel-upload";
    private static final String GROUP = "excel-group";

    public Map<String, Object> getTopicInfo() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", GROUP);

        Map<String, Object> result = new LinkedHashMap<>();

        try (AdminClient admin = AdminClient.create(props)) {
            TopicDescription td = admin.describeTopics(List.of(TOPIC)).topicNameValues().get(TOPIC).get();

            List<TopicPartition> tps = new ArrayList<>();
            for (var p : td.partitions())
                tps.add(new TopicPartition(TOPIC, p.partition()));

            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
                Map<TopicPartition, Long> endOffsets = consumer.endOffsets(tps);
                Map<TopicPartition, OffsetAndMetadata> committed = admin.listConsumerGroupOffsets(GROUP)
                        .partitionsToOffsetAndMetadata().get();

                long totalMessages = 0, totalLag = 0;
                List<Map<String, Object>> partitions = new ArrayList<>();

                for (var p : td.partitions()) {
                    TopicPartition tp = new TopicPartition(TOPIC, p.partition());
                    long end = endOffsets.getOrDefault(tp, 0L);
                    long done = committed.containsKey(tp) ? committed.get(tp).offset() : 0L;
                    long lag = end - done;
                    totalMessages += end;
                    totalLag += lag;

                    Map<String, Object> pInfo = new LinkedHashMap<>();
                    pInfo.put("partition", p.partition());
                    pInfo.put("broker", p.leader().id());
                    pInfo.put("messages", end);
                    pInfo.put("lag", lag);
                    partitions.add(pInfo);
                }

                result.put("topic", TOPIC);
                result.put("brokers", 3);
                result.put("partitions", td.partitions().size());
                result.put("totalMessages", totalMessages);
                result.put("totalLag", totalLag);
                result.put("status", totalLag == 0 ? "all consumed" : "processing");
                result.put("breakdown", partitions);
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }
}
