package com.araguacaima.braas.core.drools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class ReloadableClassLoader extends ClassLoader {

    public ReloadableClassLoader(ClassLoader parent) {
        super(parent);
    }

    Class loadClass(String name, URL myUrl) {

        try {
            URLConnection connection = myUrl.openConnection();
            InputStream input = connection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int data = input.read();

            while (data != -1) {
                buffer.write(data);
                data = input.read();
            }

            input.close();

            byte[] classData = buffer.toByteArray();

            return defineClass(name, classData, 0, classData.length);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class loadClass(String name, byte[] classData) {
        return defineClass(name, classData, 0, classData.length);
    }
}
