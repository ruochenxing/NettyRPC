/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.newlandframework.rpc.parallel;

import com.newlandframework.rpc.core.RpcSystemConfig;
import com.newlandframework.rpc.jmx.ThreadPoolMonitorProvider;
import com.newlandframework.rpc.jmx.ThreadPoolStatus;
import com.newlandframework.rpc.parallel.policy.AbortPolicy;
import com.newlandframework.rpc.parallel.policy.BlockingPolicy;
import com.newlandframework.rpc.parallel.policy.CallerRunsPolicy;
import com.newlandframework.rpc.parallel.policy.DiscardedPolicy;
import com.newlandframework.rpc.parallel.policy.RejectedPolicy;
import com.newlandframework.rpc.parallel.policy.RejectedPolicyType;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * 独立出线程池主要是为了应对复杂耗I/O操作的业务，不阻塞netty的handler线程而引入
 * 当然如果业务足够简单，把处理逻辑写入netty的handler（ChannelInboundHandlerAdapter）也未尝不可
 */
public class RpcThreadPool {
	private static final Timer TIMER = new Timer("ThreadPoolMonitor", true);
	private static long monitorDelay = 100L;
	// private static long monitorPeriod = 300L;

	private static RejectedExecutionHandler createPolicy() {
		// TODO
		RejectedPolicyType rejectedPolicyType = RejectedPolicyType.fromString(
				System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_REJECTED_POLICY_ATTR, "AbortPolicy"));
		switch (rejectedPolicyType) {
		case BLOCKING_POLICY:
			return new BlockingPolicy();
		case CALLER_RUNS_POLICY:
			return new CallerRunsPolicy();
		case ABORT_POLICY:
			return new AbortPolicy();// 直接拒绝执行，抛出rejectedExecution异常。
		case REJECTED_POLICY:
			return new RejectedPolicy();
		case DISCARDED_POLICY:
			return new DiscardedPolicy();// 从任务队列的头部开始直接丢弃一半的队列元素，为任务队列“减负”。
		default: {
			break;
		}
		}

		return null;
	}

	private static BlockingQueue<Runnable> createBlockingQueue(int queues) {
		BlockingQueueType queueType = BlockingQueueType.fromString(
				System.getProperty(RpcSystemConfig.SYSTEM_PROPERTY_THREADPOOL_QUEUE_NAME_ATTR, "LinkedBlockingQueue"));
		switch (queueType) {
		case LINKED_BLOCKING_QUEUE:
			return new LinkedBlockingQueue<Runnable>();// 采用链表方式实现的无界任务队列，当然你可以额外指定其容量，使其有界。
		case ARRAY_BLOCKING_QUEUE:
			return new ArrayBlockingQueue<Runnable>(RpcSystemConfig.SYSTEM_PROPERTY_PARALLEL * queues);// 有界的的数组任务队列。
		case SYNCHRONOUS_QUEUE:
			return new SynchronousQueue<Runnable>();// 任务队列的容量固定为1，当客户端提交执行任务过来的时候，有进行阻塞。直到有个处理线程取走这个待执行的任务，否则会一直阻塞下去。
		default:
			break;
		}

		return null;
	}

	public static Executor getExecutor(int threads, int queues) {
		System.out.println("ThreadPool Core[threads:" + threads + ", queues:" + queues + "]");
		String name = "RpcThreadPool";
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.MILLISECONDS,
				createBlockingQueue(queues), new NamedThreadFactory(name, true), createPolicy());
		return executor;
	}

	// TODO ?
	public static Executor getExecutorWithJmx(int threads, int queues) {
		final ThreadPoolExecutor executor = (ThreadPoolExecutor) getExecutor(threads, queues);
		TIMER.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				ThreadPoolStatus status = new ThreadPoolStatus();
				status.setPoolSize(executor.getPoolSize());
				status.setActiveCount(executor.getActiveCount());
				status.setCorePoolSize(executor.getCorePoolSize());
				status.setMaximumPoolSize(executor.getMaximumPoolSize());
				status.setLargestPoolSize(executor.getLargestPoolSize());
				status.setTaskCount(executor.getTaskCount());
				status.setCompletedTaskCount(executor.getCompletedTaskCount());
				try {
					ThreadPoolMonitorProvider.monitor(status);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (ReflectionException e) {
					e.printStackTrace();
				} catch (MBeanException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				}
			}
		}, monitorDelay, monitorDelay);
		return executor;
	}
}
