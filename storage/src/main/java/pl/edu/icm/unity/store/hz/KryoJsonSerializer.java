/*
 * Copyright (c) 2016 ICM Uniwersytet Warszawski All rights reserved.
 * See LICENCE.txt file for licensing information.
 */
package pl.edu.icm.unity.store.hz;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pl.edu.icm.unity.JsonUtil;
import pl.edu.icm.unity.base.utils.JsonSerializer;

/**
 * Kryo serializer delegating the actual work to {@link JsonSerializer} 
 * @author K. Benedyczak
 */
public class KryoJsonSerializer<T> extends Serializer<T>
{
	private JsonSerializer<T> jsonSerializer;

	public KryoJsonSerializer(JsonSerializer<T> jsonSerializer)
	{
		this.jsonSerializer = jsonSerializer;
	}

	@Override
	public void write(Kryo kryo, Output output, T object)
	{
		ObjectNode json = jsonSerializer.toJson(object);
		byte[] asBytes = JsonUtil.serialize2Bytes(json);
		output.writeInt(asBytes.length);
		output.writeBytes(asBytes);
	}

	@Override
	public T read(Kryo kryo, Input input, Class<T> type)
	{
		int length = input.readInt();
		byte[] src = new byte[length];
		input.readBytes(src);
		ObjectNode json = JsonUtil.parse(src);
		return jsonSerializer.fromJson(json);
	}
}