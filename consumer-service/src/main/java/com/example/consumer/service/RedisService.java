package com.example.consumer.service;

import com.example.consumer.model.JobStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public JobStatus getJobStatus(String jobId) {
        return (JobStatus) redisTemplate.opsForValue().get(jobId);
    }

    public void incrementDone(String jobId) {
        JobStatus status = getJobStatus(jobId);
        status.setDone(status.getDone() + 1);
        redisTemplate.opsForValue().set(jobId, status);
    }

    public void incrementFailed(String jobId) {
        JobStatus status = getJobStatus(jobId);
        status.setFailed(status.getFailed() + 1);
        redisTemplate.opsForValue().set(jobId, status);
    }

    public void completeJob(String jobId) {
        JobStatus status = getJobStatus(jobId);
        status.setStatus("COMPLETED");
        redisTemplate.opsForValue().set(jobId, status);
    }
}
