/*
 *  @version     1.0, Aug 23, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

public class ReflectionUtils {
    private static final String RESOURCE_PATTERN = "/**/*.class";

    public static List<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotation, String... packagesToScan) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String pkg : packagesToScan) {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    TypeFilter filter = new AnnotationTypeFilter(annotation, false);
                    if (filter.match(reader, readerFactory)) {
                        classes.add(Class.forName(className));
                    }
                }
            }
        }
        return classes;
    }

    public static Object execute(String className, String methodName, Object object, Object... parameters) {
        try {
            Class clazz = Class.forName(className);
            Class[] parameterTypes = new Class[parameters.length];
            int i = 0;
            for (Object parameter : parameters) {
                parameterTypes[i++] = parameter.getClass();
            }
            for (Method method : clazz.getMethods()) {
                if (methodName.equals(method.getName())) {
                    Class<?>[] methodParameterTypes = method.getParameterTypes();
                    boolean matches = true;
                    for (i = 0; i < parameterTypes.length; i++) {
                        if (!methodParameterTypes[i].isAssignableFrom(parameters[i].getClass())) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        // obtain a Class[] based on the passed arguments as Object[]
                        return method.invoke(object, parameters);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

}
