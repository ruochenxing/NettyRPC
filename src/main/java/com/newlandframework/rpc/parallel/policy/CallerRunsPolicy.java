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
package com.newlandframework.rpc.parallel.policy;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 不抛弃任务，也不抛出异常，而是调用者自己来运行。这个是主要是因为过多的并行请求会加剧系统的负载，
 * 线程之间调度操作系统会频繁的进行上下文切换。当遇到线程池满的情况，与其频繁的切换、中断。
 * 不如把并行的请求，全部串行化处理，保证尽量少的处理延时，大概是我能想到的Doug Lea的设计初衷吧。
 */
public class CallerRunsPolicy extends ThreadPoolExecutor.CallerRunsPolicy {
	private static final Logger LOG = LoggerFactory.getLogger(CallerRunsPolicy.class);

	private String threadName;

	public CallerRunsPolicy() {
		this(null);
	}

	public CallerRunsPolicy(String threadName) {
		this.threadName = threadName;
	}

	@Override
	public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
		if (threadName != null) {
			LOG.error("RPC Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
		}

		super.rejectedExecution(runnable, executor);
	}
}
