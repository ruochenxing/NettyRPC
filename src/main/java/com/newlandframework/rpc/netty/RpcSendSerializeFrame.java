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

import com.newlandframework.rpc.netty.handler.NettyRpcSendHandler;
import com.newlandframework.rpc.netty.handler.JdkNativeSendHandler;
import com.newlandframework.rpc.netty.handler.KryoSendHandler;
import com.newlandframework.rpc.netty.handler.HessianSendHandler;
import com.newlandframework.rpc.netty.handler.ProtostuffSendHandler;
import com.newlandframework.rpc.serialize.RpcSerializeFrame;
import com.newlandframework.rpc.serialize.RpcSerializeProtocol;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import io.netty.channel.ChannelPipeline;

/**
 * NettyRPC的客户端也是可以选择协议类型的，必须注意的是，NettyRPC的客户端和服务端的协议类型必须一致，才能互相通信。
 */
public class RpcSendSerializeFrame implements RpcSerializeFrame {
	private static ClassToInstanceMap<NettyRpcSendHandler> handler = MutableClassToInstanceMap.create();

	static {
		handler.putInstance(JdkNativeSendHandler.class, new JdkNativeSendHandler());
		handler.putInstance(KryoSendHandler.class, new KryoSendHandler());
		handler.putInstance(HessianSendHandler.class, new HessianSendHandler());
		handler.putInstance(ProtostuffSendHandler.class, new ProtostuffSendHandler());
	}

	// 后续可以优化成通过spring ioc方式注入
	@Override
	public void select(RpcSerializeProtocol protocol, ChannelPipeline pipeline) {
		switch (protocol) {
		case JDKSERIALIZE: {
			handler.getInstance(JdkNativeSendHandler.class).handle(pipeline);
			break;
		}
		case KRYOSERIALIZE: {
			handler.getInstance(KryoSendHandler.class).handle(pipeline);
			break;
		}
		case HESSIANSERIALIZE: {
			handler.getInstance(HessianSendHandler.class).handle(pipeline);
			break;
		}
		case PROTOSTUFFSERIALIZE: {
			handler.getInstance(ProtostuffSendHandler.class).handle(pipeline);
			break;
		}
		default:
			break;
		}
	}
}
