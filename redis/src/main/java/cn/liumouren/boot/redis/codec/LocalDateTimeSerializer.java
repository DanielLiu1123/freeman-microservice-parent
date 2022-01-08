package cn.liumouren.boot.redis.codec;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime to timestamp
 *
 * @author freeman
 * @date 2022/1/9 02:16
 */
public class LocalDateTimeSerializer implements ObjectSerializer {

    public static final LocalDateTimeSerializer instance = new LocalDateTimeSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull();
            return;
        }
        LocalDateTime localDateTime = (LocalDateTime) object;
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        out.writeLong(instant.toEpochMilli());
    }
}
