package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapToBeanConversionTest {

    @Test
    void shouldConvertMapToBean() {
        Map<String, Object> source = new HashMap<>();
        source.put("name", "alice");
        source.put("age", 18);

        UserDTO result = convert(source);

        assertEquals("alice", result.getName());
        assertEquals(Integer.valueOf(18), result.getAge());
    }

    @LamiaMapping
    static UserDTO convert(Map<String, Object> source) {
        return Lamia.builder().mapping(source).build(UserDTO.class);
    }

    @Data
    static class UserDTO {
        private String name;
        private Integer age;
    }
}
