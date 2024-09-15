package io.jsondb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectMapperProvider {
    private static final Supplier<ObjectMapper> DEFAULT_MAPPER_BUILDER;
    private static final ObjectMapper DEFAULT_MAPPER;

    static {
        DEFAULT_MAPPER_BUILDER = () -> {
            return new ObjectMapper()
                    .registerModule(new ParameterNamesModule())
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule());
        };

        DEFAULT_MAPPER = DEFAULT_MAPPER_BUILDER.get();
    }

    public static ObjectMapper defaultMapper() {
        return DEFAULT_MAPPER;
    }

    public static ObjectMapper buildNewDefaultMapper() {
        return DEFAULT_MAPPER_BUILDER.get();
    }

    public static void configure(Consumer<ObjectMapper> conf) {
        conf.accept(DEFAULT_MAPPER);
    }
}
