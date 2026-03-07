package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanToBeanConversionTest  {
    @Test
    void shouldConvertBeanToBean() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        UserDTO result = convert(user);

        assertEquals("alice", result.getName());
        assertEquals(Integer.valueOf(18), result.getAge());
    }

    @Test
    @LamiaMapping
    void shouldConvertBeanToBeanWithInline() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        UserDTO result = (UserDTO) Lamia.mapping(user);

        assertEquals("alice", result.getName());
        assertEquals(Integer.valueOf(18), result.getAge());
    }

    @LamiaMapping
    static UserDTO convert(User user) {
        return (UserDTO) Lamia.mapping(user);
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
