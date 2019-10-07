package com.araguacaima.braas.core.google.wrapper;

import com.araguacaima.braas.core.google.model.Config;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Properties;

public class ConfigWrapper {

    public static Properties toProperties(Collection<Config> configs) {
        Properties properties = new Properties();
        if (CollectionUtils.isNotEmpty(configs)) {
            configs.forEach(config -> properties.setProperty(config.getKey(), config.getValue()));
        }
        return properties;
    }
}
