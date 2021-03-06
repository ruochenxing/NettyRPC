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
package com.newlandframework.rpc.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;

import com.newlandframework.rpc.serialize.RpcSerializeProtocol;

/**
 * Rpc服务端管道初始化
 * 
 * 引入了序列化消息对象（RpcSerializeProtocol）
 */
public class MessageRecvChannelInitializer extends ChannelInitializer<SocketChannel> {

	private RpcSerializeProtocol protocol;
	private RpcRecvSerializeFrame frame = null;

	MessageRecvChannelInitializer buildRpcSerializeProtocol(RpcSerializeProtocol protocol) {
		this.protocol = protocol;
		return this;
	}

	MessageRecvChannelInitializer(Map<String, Object> handlerMap) {
		frame = new RpcRecvSerializeFrame(handlerMap);
	}

	// 当新连接accept的时候，这个方法会调用
	// 注册请求的处理handle
	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		frame.select(protocol, pipeline);
	}
}
