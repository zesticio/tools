package com.zestic.retrofit.boot;

import com.zestic.common.utils.ClassInspectionUtil;
import com.zestic.retrofit.annotation.EnableRetrofitClient;
import com.zestic.retrofit.annotation.HttpInterceptor;
import com.zestic.retrofit.boot.context.RetrofitInterceptorContext;
import com.zestic.retrofit.boot.context.RetrofitInterceptorContextImpl;
import okhttp3.Interceptor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * BEan definition registrar responsible for registering retrofit specific bean definitions
 * <p>
 * Currently registering instantiation aware bean post processor adapter responsible for instantiating retrofit
 * services and registering individual retrofit service interfaces as bean definition so that they can
 * instantiated by the post processor adapter
 */
/**
 * @author deebendukumar
 */
public class ImportBeanDefinitionRegistrarImpl implements ImportBeanDefinitionRegistrar {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RetrofitAutoConfiguration.class);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        doRegisterRetrofitServiceBeanDefinitions(annotationMetadata, registry);
    }

    /**
     * Scans for interfaces annotated with {@link com.zestic.retrofit.annotation.HttpInterceptor} from packages defined by
     * {@link EnableRetrofitClient}
     *
     * @param annotationMetadata
     * @param registry
     */
    private void doRegisterRetrofitServiceBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);
        for (String packageToScan : packagesToScan) {
            logger.info("Trying to find candidates from package {" + packageToScan + "}");
            Collection<Class<?>> classes = ClassInspectionUtil.findAnnotatedClasses(HttpInterceptor.class, packageToScan);
            if (classes != null && !classes.isEmpty()) {
                RetrofitInterceptorContext context = RetrofitInterceptorContextImpl.getInstance();
                for (Class<?> classz : classes) {
                    if (classz.isAnnotationPresent(HttpInterceptor.class)) {
                        HttpInterceptor annotation = classz.getAnnotation(HttpInterceptor.class);
                        String interceptorId = "".equals(annotation.name()) ? annotation.value() : annotation.name();
                        logger.info(interceptorId);
                        Interceptor interceptor = null;
                        try {
                            interceptor = (Interceptor) createServiceInstance(classz, interceptorId);
                            context.register(interceptorId, (Interceptor) interceptor);
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Inspects the packages to be scanned for {@link com.zestic.retrofit.annotation.HttpInterceptor} interfaces from the link {@link EnableRetrofitClient}
     * import annotation
     * <p>
     * returns the list of packages to be scanned for {@link com.zestic.retrofit.annotation.HttpInterceptor}
     *
     * @param metadata
     * @return
     */
    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableRetrofitClient.class.getName()));

        String[] value = attributes.getStringArray("value");
        String[] basePackages = attributes.getStringArray("basePackages");
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        if (!ObjectUtils.isEmpty(value)) {
            Assert.state(ObjectUtils.isEmpty(basePackages),
                    "@RetrofitServiceScan basePackages and value attributes are mutually exclusive");
        }
        Set<String> packagesToScan = new LinkedHashSet<String>();
        packagesToScan.addAll(Arrays.asList(value));
        packagesToScan.addAll(Arrays.asList(basePackages));

        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }

        if (packagesToScan.isEmpty()) {
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }

    <T> T createServiceInstance(Class<T> serviceClass, String interceptorId) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return (T) serviceClass.getConstructor().newInstance();
    }

    <T> T findServiceInstance(Class<T> serviceClass, String interceptorId) throws NoSuchMethodException {
        ApplicationContext context = new AnnotationConfigApplicationContext(serviceClass.getClass());
        return context.getBean(interceptorId, serviceClass);
    }
}
