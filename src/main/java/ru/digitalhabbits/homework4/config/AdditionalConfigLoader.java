package ru.digitalhabbits.homework4.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;

public class AdditionalConfigLoader implements EnvironmentPostProcessor {
    private static final String CONFIG_PATH = "config";
    private static final String LOCATION_PATH = format("classpath:%s/*.properties", CONFIG_PATH);
    private final PropertySourceLoader loader = new PropertiesPropertySourceLoader();

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            Arrays.stream(new PathMatchingResourcePatternResolver().getResources(LOCATION_PATH))
                    .map(this::loadSources)
                    .forEach(environment.getPropertySources()::addLast);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private PropertySource<?> loadSources(Resource path) {
        if (!path.exists()) {
            throw new IllegalArgumentException(format("%s does not exist", path));
        }
        try {
            return loader.load(path.getFilename(), path).get(0);
        }
        catch (IOException e) {
            throw new IllegalStateException(format("cannot load configuration from %s", path), e);
        }
    }
}
