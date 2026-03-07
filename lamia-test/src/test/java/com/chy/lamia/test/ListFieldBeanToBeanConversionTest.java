package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListFieldBeanToBeanConversionTest {

    @Test
    void shouldConvertBeanWithListField() {
        UserTagSource source = new UserTagSource();
        source.setName("alice");
        source.setTags(Arrays.asList("dev", "java"));

        UserTagTarget result = convert(source);

        assertEquals("alice", result.getName());
        assertEquals(Arrays.asList("dev", "java"), result.getTags());
    }

    @LamiaMapping
    static UserTagTarget convert(UserTagSource source) {
        return Lamia.builder().mapping(source).build(UserTagTarget.class);
    }

    @Data
    static class UserTagSource {
        private String name;
        private List<String> tags;
    }

    @Data
    static class UserTagTarget {
        private String name;
        private List<String> tags;
    }
}
