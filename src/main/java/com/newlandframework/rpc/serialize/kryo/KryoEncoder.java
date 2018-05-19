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
package com.newlandframework.rpc.serialize.kryo;

import com.newlandframework.rpc.serialize.MessageCodecUtil;
import com.newlandframework.rpc.serialize.MessageEncoder;

/**
 * Kryo自己的编码器、解码器，其实只要调用Kryo编解码工具类（KryoCodecUtil）里面的encode、decode方法就可以了
 */
public class KryoEncoder extends MessageEncoder {

	public KryoEncoder(MessageCodecUtil util) {
		super(util);
	}
}
