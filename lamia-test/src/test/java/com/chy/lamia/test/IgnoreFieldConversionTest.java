package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IgnoreFieldConversionTest {

    @Test
    void shouldIgnoreMismatchedField() {
        SourceWithNestedUser source = new SourceWithNestedUser();
        source.setName("alice");
        source.setUser(new User());

        TargetWithStringUser result = convert(source);

        assertEquals("alice", result.getName());
        assertEquals("preset", result.getUser());
    }

    @LamiaMapping
    static TargetWithStringUser convert(SourceWithNestedUser source) {
        return Lamia.builder().rule().ignoreField(TargetWithStringUser::getUser).mapping(source).build(TargetWithStringUser.class);
    }

    @Data
    static class User {
        private String name;
    }

    @Data
    static class SourceWithNestedUser {
        private String name;
        private User user;
    }

    @Data
    static class TargetWithStringUser {
        private String name;
        private String user = "preset";
    }
}
