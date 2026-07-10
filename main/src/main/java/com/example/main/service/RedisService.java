package com.example.main.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.main.model.JobStatus;

@Service
public class RedisService {
    
    private final RedisTemplate<String,Object> redisTemplate;
    public RedisService(RedisTemplate<String,Object> redisTemplate){
        this.redisTemplate=redisTemplate;
    }

    public void initializeJob(String jobId,int totalRows){
        JobStatus jobStatus=new JobStatus(
            "processing",
            totalRows,
            0,
            0
        );
        redisTemplate.opsForValue().set(jobId,jobStatus);
    }

    public JobStatus getJobStatus(String jobId) {
        return (JobStatus) redisTemplate
                .opsForValue()
                .get(jobId);
    }

    public void incrementDone(String jobId) {
        JobStatus jobStatus = getJobStatus(jobId);
        jobStatus.setDone(jobStatus.getDone() + 1);
        redisTemplate.opsForValue()
                .set(jobId, jobStatus);
    }
    public void incrementFailed(String jobId) {
    JobStatus jobStatus = getJobStatus(jobId);
    jobStatus.setFailed(jobStatus.getFailed() + 1);
    redisTemplate.opsForValue()
            .set(jobId, jobStatus);
}
public void completeJob(String jobId) {
    JobStatus jobStatus = getJobStatus(jobId);
    jobStatus.setStatus("COMPLETED");
    redisTemplate.opsForValue()
            .set(jobId, jobStatus);
}

}
