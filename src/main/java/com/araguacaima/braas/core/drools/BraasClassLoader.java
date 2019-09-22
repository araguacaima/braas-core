package com.araguacaima.braas.core.drools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class BraasClassLoader extends URLClassLoader {

    public BraasClassLoader(URL url, ClassLoader parent) {
        super(new URL[]{url}, parent);
    }

    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        byte[] b;
        try {
            b = loadClassFromFile(name);
            return defineClass(name, b, 0, b.length);
        } catch (IOException e) {
            String msg = "Class '" + name + "' has not been loaded by Classloader[" + BraasClassLoader.class.getName() + "] yet!";
            throw new ClassNotFoundException(msg);
        }
    }

    private byte[] loadClassFromFile(String fileName) throws IOException {
        URL[] urls = super.getURLs();
        URL url = urls[0];
        File classFile = new File(url.getFile(), fileName.replace('.', File.separatorChar) + ".class");
        URL classUrl = classFile.toURI().toURL();
        InputStream inputStream = classUrl.openStream();
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue;
        try {
            while ((nextValue = inputStream.read()) != -1) {
                byteStream.write(nextValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        buffer = byteStream.toByteArray();
        return buffer;
    }
}
