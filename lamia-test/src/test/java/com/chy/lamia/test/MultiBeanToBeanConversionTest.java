package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultiBeanToBeanConversionTest {

    @Test
    void shouldMergeMultipleBeansIntoBean() {
        User user = new User();
        user.setName("alice");
        user.setAge(18);

        Address address = new Address();
        address.setCity("Shanghai");

        UserProfileDTO result = convert(user, address);

        assertEquals("alice", result.getName());
        assertEquals(Integer.valueOf(18), result.getAge());
        assertEquals("Shanghai", result.getCity());
    }

    @LamiaMapping
    static UserProfileDTO convert(User user, Address address) {
        return Lamia.builder().mapping(user, address).build(UserProfileDTO.class);
    }

    @Data
    static class User {
        private String name;
        private Integer age;
    }

    @Data
    static class Address {
        private String city;
    }

    @Data
    static class UserProfileDTO {
        private String name;
        private Integer age;
        private String city;
    }
}
