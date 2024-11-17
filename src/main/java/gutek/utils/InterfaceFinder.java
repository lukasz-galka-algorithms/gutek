package gutek.utils;

import org.reflections.Reflections;

import java.util.Set;

/**
 * Utility class for finding classes or interfaces in a package that implement or extend a specific base class or interface.
 */
public class InterfaceFinder {

    /**
     * Private constructor to prevent instantiation
     */
    private InterfaceFinder(){}

    /**
     * Finds all classes or interfaces in the specified package that extend or implement the given base class or interface.
     *
     * @param basePackage The package to scan.
     * @param baseClass   The base class or interface to search for.
     * @param <T>         The type of the base class or interface.
     * @return A set of classes or interfaces extending or implementing the base class or interface.
     */
    public static <T> Set<Class<? extends T>> findImplementations(String basePackage, Class<T> baseClass) {
        Reflections reflections = new Reflections(basePackage);
        return reflections.getSubTypesOf(baseClass);
    }
}