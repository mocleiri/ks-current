package com.sigmasys.kuali.ksa.annotation;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring-based customized version of AnnotationUtils
 *
 * @author Michael Ivanov
 */
public class AnnotationUtils {

    /**
     * Wrapper method for Spring to avoid having to import Spring's implementation
     * Searches for class-level annotation on the given instance
     *
     * @param annotationClass annotation to look for
     * @param method          method to look at
     * @return annotation instance or null if not found
     */
    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass, Method method) {
        return org.springframework.core.annotation.AnnotationUtils.getAnnotation(method, annotationClass);
    }

    /**
     * Wrapper method for Spring to avoid having to import Spring's implementation
     * Searches for class-level annotation on the given instance
     *
     * @param annotationClass annotation to look for
     * @param object          instance to look in
     * @return annotation instance or null if not found
     */
    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass, Object object) {
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(object.getClass(), annotationClass);
    }

    /**
     * Intelligently finds annotation for a named property
     * Checks for annotation at the accessor method first, then at the field level, and finally at the class level
     *
     * @param annotationClass annotation to look for
     * @param object          instance to look in
     * @param propertyName    name of the bean property to search
     * @return annotation instance or null if not found
     */
    public static <A extends Annotation> A getAnnotation(Class<A> annotationClass, Object object, String propertyName) {

        A annotation = null;

        // Check to see if either class or method has annotation
        Method method = findGetterMethod(object.getClass(), propertyName);

        if (method != null) {
            annotation = org.springframework.core.annotation.AnnotationUtils.findAnnotation(method, annotationClass);
        }

        if (annotation == null) {
            try {
                Field field = object.getClass().getField(propertyName);
                annotation = field.getAnnotation(annotationClass);
            } catch (NoSuchFieldException e) {
                // This just means that there is no field annotation
            }
        }

        return annotation;
    }

    @SuppressWarnings("unchecked")
    /**
     * Utility method that finds a getter for a given property name
     * For instance, if passed "firstName" will return getFirstName() method instance
     * @return method object or null if not found
     */
    public static Method findGetterMethod(Class theClass, String propertyName) {

        Method[] methods = theClass.getMethods();
        for (Method method : methods) {
            // only carry on if the method has no parameters
            if (method.getParameterTypes().length == 0) {
                String methodName = method.getName();

                // try "get"
                if (methodName.startsWith("get")) {
                    String testStdMethod = Introspector.decapitalize(methodName.substring(3));
                    String testOldMethod = methodName.substring(3);
                    if (testStdMethod.equals(propertyName) || testOldMethod.equals(propertyName)) {
                        return method;
                    }

                }

                // if not "get", then try "is"
                if (methodName.startsWith("is")) {
                    String testStdMethod = Introspector.decapitalize(methodName.substring(2));
                    String testOldMethod = methodName.substring(2);
                    if (testStdMethod.equals(propertyName) || testOldMethod.equals(propertyName)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * A modified version of Spring's findAnnotatedClasses that returns  a set of classes
     * instead of a set of string values with the default resource loader.
     *
     * @param annotationClass annotation
     * @param packageNames    package names where to look for the annotation
     * @param <A>             annotation type
     * @return a set of Class instances
     */
    public static <A extends Annotation> Set<Class> findAnnotatedClasses(Class<A> annotationClass,
                                                                         String... packageNames) {

        final String CLASS_RESOURCE_PATTERN = "**/*.class";

        final ResourcePatternResolver resourcePatternResolver =
                ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());

        final MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        final TypeFilter annotationFilter = new AnnotationTypeFilter(annotationClass);

        try {

            final Set<Class> annotatedClasses = new HashSet<Class>();

            for (String packageName : packageNames) {

                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(packageName)) +
                        "/" + CLASS_RESOURCE_PATTERN;

                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);

                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        if (annotationFilter.match(metadataReader, metadataReaderFactory)) {
                            Class<?> annotatedClass = Class.forName(metadataReader.getAnnotationMetadata().getClassName());
                            A annotation = annotatedClass.getAnnotation(annotationClass);
                            if (annotation != null) {
                                annotatedClasses.add(annotatedClass);
                            }
                        }
                    }
                }
            }

            return annotatedClasses;

        } catch (Exception ex) {
            throw new RuntimeException("Failure during classpath scanning", ex);
        }
    }


}