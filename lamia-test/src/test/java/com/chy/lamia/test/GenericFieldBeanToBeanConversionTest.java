package com.chy.lamia.test;

import com.chy.lamia.convert.core.annotation.LamiaMapping;
import com.chy.lamia.expose.Lamia;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericFieldBeanToBeanConversionTest {

    @Test
    void shouldConvertBeanWithGenericField() {
        Payload<String> payload = new Payload<>();
        payload.setValue("alice");
        payload.setItems(Arrays.asList("dev", "java"));

        GenericFieldSource source = new GenericFieldSource();
        source.setPayload(payload);

        GenericFieldTarget result = convert(source);

        assertEquals("alice", result.getPayload().getValue());
        assertEquals(Arrays.asList("dev", "java"), result.getPayload().getItems());
    }

    @LamiaMapping
    static GenericFieldTarget convert(GenericFieldSource source) {
        return Lamia.builder().mapping(source).build(GenericFieldTarget.class);
    }

    @Data
    static class GenericFieldSource {
        private Payload<String> payload;
    }

    @Data
    static class GenericFieldTarget {
        private Payload<String> payload;
    }

    @Data
    static class Payload<T> {
        private T value;
        private List<T> items;
    }
}
