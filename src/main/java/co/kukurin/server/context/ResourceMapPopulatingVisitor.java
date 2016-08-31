package co.kukurin.server.context;

import co.kukurin.custom.ErrorHandler;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static co.kukurin.server.context.ContextIntializer.PACKAGE_SEPARATOR;

public class ResourceMapPopulatingVisitor extends SimpleFileVisitor<Path> {

    private final String packageName;
    private Map<String, Class<?>> resourceNameToClass;

    ResourceMapPopulatingVisitor(String packageName) {
        this.packageName = packageName;
    }

    public Map<String, Class<?>> getResourceNameToClassMap() {
        return resourceNameToClass;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        boolean hasAlreadyVisitedBasePackage = this.resourceNameToClass == null;

        if(hasAlreadyVisitedBasePackage) {
            this.resourceNameToClass = new HashMap<>();
            return FileVisitResult.CONTINUE;
        }

        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        ErrorHandler
                .optionalResult(() -> classFromPath(file))
                .ifPresent(clazz -> putDependingOnAnnotation(file, clazz))
                .orElseDo(() -> System.out.println("didn't load class for file " + file)); // TODO logger

        return super.visitFile(file, attrs);
    }

    // TODO annotation check
    private Class<?> putDependingOnAnnotation(Path file, Class<?> clazz) {
        return this.resourceNameToClass.put(file.getFileName().toString(), clazz);
    }

    private Class<?> classFromPath(Path file) throws ClassNotFoundException {
        return Class.forName(this.packageName + PACKAGE_SEPARATOR + classNameFromPath(file));
    }

    private String classNameFromPath(Path file) {
        final char fileExtensionSeparator = '.';
        String fileName = file.getFileName().toString();

        return fileName.substring(0, fileName.lastIndexOf(fileExtensionSeparator));
    }

}
