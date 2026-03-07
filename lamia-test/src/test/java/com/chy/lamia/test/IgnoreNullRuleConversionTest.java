package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import com.chy.lamia.expose.rule.RuleType;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IgnoreNullRuleConversionTest {

    @Test
    void shouldIgnoreNullWhenFillingExistingBean() {
        User user = new User();
        user.setName(null);
        user.setAge(20);

        UserDTO target = new UserDTO();
        target.setName("keep-me");
        target.setAge(10);

        fill(user, target);

        assertEquals("keep-me", target.getName());
        assertEquals(Integer.valueOf(20), target.getAge());
    }

    @LamiaMapping
    static void fill(User user, UserDTO target) {
        Lamia.builder().rule(RuleType.IGNORE_NULL).mapping(user).build(target);
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
