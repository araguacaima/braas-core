package com.araguacaima.braas.core;

import com.araguacaima.commons.utils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class Commons {
    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.toString();
    public static final ClassLoaderUtils classLoaderUtils = new ClassLoaderUtils(Commons.class.getClassLoader());
    public static final PropertiesHandlerUtils propertiesHandlerUtils = PropertiesHandlerUtils.getInstance();
    public static final ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();
    public static final EnumsUtils<MessageType> enumsUtils = EnumsUtils.getInstance();
    public static final StringUtils stringUtils = StringUtils.getInstance();
    public static final String DEFAULT_PACKAGE_NAME = "com.araguacaima.braas";

    public static File findFile(String filePath, ClassLoader cl) throws IOException {
        if (cl == null) {
            cl = Commons.class.getClassLoader();
        }
        InputStream inputstream = cl.getResourceAsStream(filePath);
        URL resource = cl.getResource(filePath);
        if (resource != null) {
            return new File(resource.getPath());
        }
        if (inputstream == null) {
            inputstream = classLoaderUtils.getClassLoader().getResourceAsStream(filePath);
            resource = classLoaderUtils.getClassLoader().getResource(filePath);
            if (resource != null) {
                return new File(resource.getPath());
            }
            if (inputstream == null) {
                inputstream = classLoaderUtils.getClassLoader().getParent().getResourceAsStream(filePath);
                resource = classLoaderUtils.getClassLoader().getParent().getResource(filePath);
                if (resource != null) {
                    return new File(resource.getPath());
                }
                if (inputstream == null) {
                    inputstream = ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
                    resource = ClassLoader.getSystemClassLoader().getResource(filePath);
                    if (resource != null) {
                        return new File(resource.getPath());
                    }
                    if (inputstream == null) {
                        try {
                            inputstream = new java.io.FileInputStream((new File(filePath)));
                            resource = (new File(filePath)).toURI().toURL();
                            filePath = resource.getPath();
                        } catch (Throwable t) {
                            if (!filePath.startsWith(".")) {
                                String prefix = ".";
                                if (!filePath.startsWith("/") && !filePath.startsWith("\\\\")) {
                                    prefix = prefix + "/";
                                }
                                return findFile(prefix + filePath, cl);
                            }
                        }
                    }
                }
            }
        }
        inputstream.close();
        return new File(filePath);
    }

    public static Class[] getClassesFromClassLoader(ClassLoader classLoader) throws NoSuchFieldException, IllegalAccessException {
        Field f;
        Class[] classes = null;
        f = ClassLoader.class.getDeclaredField("classes");
        f.setAccessible(true);
        Vector<Class> classes_ = (Vector<Class>) f.get(classLoader);
        int classesSize = classes_.size();
        if (classesSize > 0) {
            classes = new Class[classesSize];
            for (int i = 0; i < classesSize; i++) {
                classes[i] = classes_.get(i);
            }
        }
        return classes;
    }
}
