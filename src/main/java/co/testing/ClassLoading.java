package co.testing;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

@Resource
public class ClassLoading {

    public static void main(String[] args) throws IOException {
        Class<?> currentClass = ClassLoading.class;
        System.out.println(currentClass.getName());
        System.out.println(currentClass.getPackage().getName());

        Package p = currentClass.getPackage();
        System.out.println(currentClass.getPackage().getClass().getName());
        System.out.println(currentClass.getPackage().getClass().getDeclaredClasses());

        ClassLoader classLoader = currentClass.getClassLoader();
        String path = p.getName().replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());

            System.out.println(file.toString());
        }
    }

}
