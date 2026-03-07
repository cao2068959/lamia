package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanToObjectMapConversionTest {

    @Test
    void shouldConvertBeanToObjectMap() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        Map<String, Object> result = convert(user);

        assertEquals("alice", result.get("name"));
        assertEquals(18, result.get("age"));
    }

    @LamiaMapping
    static Map<String, Object> convert(User user) {
        return (Map<String, Object>) Lamia.mapping(user);
    }

    @Data
    static class User {
        private String name;
        private Integer age;
    }
}
