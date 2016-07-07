package com.shl.mongodb;

import java.util.Date;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class UserCodec implements Codec<User>{

	@Override
	public void encode(BsonWriter writer, User value, EncoderContext encoderContext) {
        writer.writeStartDocument();
		writer.writeString("username", value.getUsername());
        writer.writeString("address", value.getAddress());
        writer.writeInt32("age", value.getAge());
        writer.writeInt32("score", value.getScore());
        writer.writeDateTime("birthday", value.getBirthday().getTime());
        writer.writeString("password", value.getPassword());
	    writer.writeEndDocument();
	}

	@Override
	public Class<User> getEncoderClass() {
		return User.class;
	}

	//读取时会调用decode方法把从数据中读取的bson数据转换成对应的JAVA对象，读取时字段的顺序必须按照存储的顺序
	@Override
	public User decode(BsonReader reader, DecoderContext decoderContext) {
		User user=new User();
		reader.readStartDocument();
		user.setId(reader.readObjectId().toHexString());
		user.setBirthday(new Date(reader.readDateTime("birthday")));
		user.setScore(reader.readInt32("score"));
		user.setPassword(reader.readString("password"));
		user.setAddress(reader.readString("address"));
		user.setAge(reader.readInt32("age"));
		user.setUsername(reader.readString("username"));
	    reader.readEndDocument();
		return user;
	}

}
