package com.example.producer.service;

import com.example.producer.model.JobStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void initializeJob(String jobId, int totalRows) {
        redisTemplate.opsForValue().set(jobId, new JobStatus("processing", totalRows, 0, 0));
    }

    public JobStatus getJobStatus(String jobId) {
        return (JobStatus) redisTemplate.opsForValue().get(jobId);
    }
}
