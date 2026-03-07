package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FillExistingBeanConversionTest {

    @Test
    void shouldFillExistingBean() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        UserDTO target = new UserDTO();
        fill(user, target);

        assertEquals("alice", target.getName());
        assertEquals(Integer.valueOf(18), target.getAge());
    }

    @LamiaMapping
    static void fill(User user, UserDTO target) {
        Lamia.builder().mapping(user).build(target);
    }

    @Data
    static class User {
        private String name;
        private Integer age;
    }

    @Data
    static class UserDTO {
        private String name;
        private Integer age;
    }
}
