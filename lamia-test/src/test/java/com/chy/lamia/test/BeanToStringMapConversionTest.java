package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanToStringMapConversionTest {

    @Test
    void shouldFillExistingStringMap() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        Map<String, String> result = convert(user);

        assertEquals("alice", result.get("name"));
        assertEquals("18", result.get("age"));
    }

    @LamiaMapping
    static Map<String, String> convert(User user) {
        Map<String, String> result = new HashMap<>();
        Lamia.builder().mapping(user).build(result);
        return result;
    }

    @Data
    static class User {
        private String name;
        private Integer age;
    }
}
