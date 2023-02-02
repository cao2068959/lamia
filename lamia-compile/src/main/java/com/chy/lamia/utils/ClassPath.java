package com.chy.lamia.utils;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;


public class ClassPath {
    /**
     * 获取一个类的class文件所在的绝对路径。 这个类可以是JDK自身的类，也可以是用户自定义的类，或者是第三方开发包里的类。
     * 只要是在本程序中可以被加载的类，都可以定位到它的class文件的绝对路径。
     *
     * @param cls 一个对象的Class属性
     * @return 这个类的class文件位置的绝对路径。 如果没有这个类的定义，则返回null。
     */
    public static URL getPathFromClass(Class cls) throws IOException {
        String path = null;
        if (cls == null) {
            throw new NullPointerException();
        }
        URL url = getClassLocationURL(cls);
        return url;
    }

    /**
     * 获取类的class文件位置的URL。这个方法是本类最基础的方法，供其它方法调用。
     */
    private static URL getClassLocationURL(final Class cls) {
        if (cls == null)
            throw new IllegalArgumentException("null input: cls");
        URL result = null;
        final String clsAsResource = cls.getName().replace('.', '/').concat(
                ".class");
        final ProtectionDomain pd = cls.getProtectionDomain();
        // java.lang.Class contract does not specify
        // if 'pd' can ever be null;
        // it is not the case for Sun's implementations,
        // but guard against null
        // just in case:
        if (pd != null) {
            final CodeSource cs = pd.getCodeSource();
            // 'cs' can be null depending on
            // the classloader behavior:
            if (cs != null)
                result = cs.getLocation();

            if (result != null) {
                // Convert a code source location into
                // a full class file location
                // for some common cases:
                if ("file".equals(result.getProtocol())) {
                    try {
                        if (result.toExternalForm().endsWith(".jar")
                                || result.toExternalForm().endsWith(".zip"))
                            result = new URL("jar:".concat(
                                    result.toExternalForm()).concat("!/")
                                    .concat(clsAsResource));
                        else if (new File(result.getFile()).isDirectory())
                            result = new URL(result, clsAsResource);
                    } catch (MalformedURLException ignore) {
                    }
                }
            }
        }

        if (result == null) {
            // Try to find 'cls' definition as a resource;
            // this is not
            // document．d to be legal, but Sun's
            // implementations seem to //allow this:
            final ClassLoader clsLoader = cls.getClassLoader();
            result = clsLoader != null ? clsLoader.getResource(clsAsResource)
                    : ClassLoader.getSystemResource(clsAsResource);
        }
        return result;
    }

}
