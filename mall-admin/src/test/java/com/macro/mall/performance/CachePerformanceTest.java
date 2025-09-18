package com.macro.mall.performance;

import com.macro.mall.service.CaseCacheService;
import com.macro.mall.test.util.CacheTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存性能测试
 * 测试缓存操作的性能指标
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CachePerformanceTest {

    @Autowired
    private CaseCacheService caseCacheService;

    @Test
    public void testCacheOperationPerformance() throws InterruptedException {
        // Given
        int operationCount = 100;
        int threadCount = 10;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(operationCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicLong totalTime = new AtomicLong(0);

        // When
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < operationCount; i++) {
            final long caseId = i + 1;
            executor.submit(() -> {
                try {
                    long opStartTime = System.nanoTime();
                    caseCacheService.clearCacheForCaseCreate(caseId, operator);
                    long opEndTime = System.nanoTime();
                    totalTime.addAndGet(opEndTime - opStartTime);
                } finally {
                    latch.countDown();
                }
            });
        }

        // Then
        assertTrue(latch.await(30, TimeUnit.SECONDS), "所有操作应在30秒内完成");

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        double avgTime = totalTime.get() / (double) operationCount / 1_000_000; // 转换为毫秒

        System.out.println("性能测试结果:");
        System.out.println("总操作数: " + operationCount);
        System.out.println("并发线程数: " + threadCount);
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", operationCount * 1000.0 / totalDuration) + " ops/sec");

        // 验证性能要求
        assertTrue(avgTime < 50.0, "平均响应时间应小于50ms，实际: " + avgTime + "ms");
        assertTrue(totalDuration < 10000, "总耗时应小于10秒，实际: " + totalDuration + "ms");

        executor.shutdown();
    }

    @Test
    public void testMessagePublishPerformance() throws InterruptedException {
        // Given
        int messageCount = 50;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(messageCount);
        AtomicLong totalTime = new AtomicLong(0);

        // When
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i++) {
            final long caseId = i + 1;
            new Thread(() -> {
                try {
                    long opStartTime = System.nanoTime();
                    caseCacheService.clearCacheForCaseUpdate(caseId, operator);
                    long opEndTime = System.nanoTime();
                    totalTime.addAndGet(opEndTime - opStartTime);
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        // Then
        assertTrue(latch.await(20, TimeUnit.SECONDS), "所有消息发布应在20秒内完成");

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        double avgTime = totalTime.get() / (double) messageCount / 1_000_000;

        System.out.println("消息发布性能测试结果:");
        System.out.println("消息数量: " + messageCount);
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均发布时间: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("消息发布率: " + String.format("%.2f", messageCount * 1000.0 / totalDuration) + " msg/sec");

        // 验证性能要求
        assertTrue(avgTime < 100.0, "平均消息发布时间应小于100ms，实际: " + avgTime + "ms");
    }

    @Test
    public void testConcurrentCacheOperations() throws InterruptedException {
        // Given
        int threadCount = 20;
        int operationsPerThread = 10;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicLong successCount = new AtomicLong(0);
        AtomicLong errorCount = new AtomicLong(0);

        // When
        long startTime = System.currentTimeMillis();

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        try {
                            long caseId = threadId * operationsPerThread + i + 1;
                            caseCacheService.clearCacheForCaseDelete(caseId, operator);
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // Then
        assertTrue(latch.await(30, TimeUnit.SECONDS), "所有并发操作应在30秒内完成");

        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        long totalOperations = threadCount * operationsPerThread;

        System.out.println("并发测试结果:");
        System.out.println("并发线程数: " + threadCount);
        System.out.println("每线程操作数: " + operationsPerThread);
        System.out.println("总操作数: " + totalOperations);
        System.out.println("成功操作数: " + successCount.get());
        System.out.println("失败操作数: " + errorCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("成功率: " + String.format("%.2f", successCount.get() * 100.0 / totalOperations) + "%");

        // 验证并发操作的可靠性
        assertTrue(successCount.get() >= totalOperations * 0.95,
            "成功率应大于95%，实际: " + (successCount.get() * 100.0 / totalOperations) + "%");

        executor.shutdown();
    }

    @Test
    public void testMemoryUsageUnderLoad() throws InterruptedException {
        // Given
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        int operationCount = 200;
        String operator = CacheTestData.createTestOperator();

        // When
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < operationCount; i++) {
            caseCacheService.clearCacheForCaseCreate((long) i + 1, operator);

            // 每50次操作检查一次内存
            if (i % 50 == 0) {
                System.gc(); // 建议垃圾回收
                Thread.sleep(10); // 短暂暂停
            }
        }

        long endTime = System.currentTimeMillis();

        // 强制垃圾回收
        System.gc();
        Thread.sleep(1000);

        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = afterMemory - beforeMemory;

        // Then
        System.out.println("内存使用测试结果:");
        System.out.println("操作前内存: " + beforeMemory / 1024 / 1024 + "MB");
        System.out.println("操作后内存: " + afterMemory / 1024 / 1024 + "MB");
        System.out.println("内存增长: " + memoryIncrease / 1024 / 1024 + "MB");
        System.out.println("操作数量: " + operationCount);
        System.out.println("总耗时: " + (endTime - startTime) + "ms");

        // 验证内存使用是否合理（增长不超过50MB）
        assertTrue(memoryIncrease < 50 * 1024 * 1024,
            "内存增长应小于50MB，实际增长: " + memoryIncrease / 1024 / 1024 + "MB");
    }
}