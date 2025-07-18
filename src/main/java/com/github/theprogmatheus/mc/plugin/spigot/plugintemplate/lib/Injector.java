package com.github.theprogmatheus.mc.plugin.spigot.plugintemplate.lib;


import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Injector {

    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final ThreadLocal<Set<Class<?>>> building = ThreadLocal.withInitial(HashSet::new);

    public Injector() {
        registerSingleton(Injector.class, this);
    }

    public <T> void registerSingleton(Class<T> clazz, T instance) {
        registerSingleton(new Class[]{clazz}, instance);
    }

    public <T> void registerSingleton(Class<?>[] classes, T instance) {
        for (Class<?> clazz : classes)
            singletons.put(clazz, instance);
    }

    public <T> T getInstance(Class<T> clazz) {

        if (singletons.containsKey(clazz))
            return (T) singletons.get(clazz);

        if (building.get().contains(clazz))
            throw new RuntimeException("Ciclo de dependência detectado para " + clazz.getName());

        building.get().add(clazz);

        try {
            T instance = createInstance(clazz);

            if (isSingleton(clazz))
                singletons.put(clazz, instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar instância de " + clazz.getName(), e);
        } finally {
            building.get().remove(clazz);
        }
    }

    private <T> T createInstance(Class<T> clazz) throws Exception {
        Constructor<?> injectConstructor = findInjectConstructor(clazz);
        T instance;

        if (injectConstructor != null) {
            Object[] params = resolveParameters(injectConstructor.getParameterTypes(), injectConstructor.getGenericParameterTypes());
            injectConstructor.setAccessible(true);
            instance = (T) injectConstructor.newInstance(params);
        } else {
            Constructor<T> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true);
            instance = defaultConstructor.newInstance();
        }
        injectFields(instance);
        injectMethods(instance);

        return instance;
    }

    private Constructor<?> findInjectConstructor(Class<?> clazz) {
        Constructor<?> injectCtor = null;
        for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
            if (ctor.isAnnotationPresent(Inject.class)) {
                if (injectCtor != null) {
                    throw new RuntimeException("Mais de um construtor @Inject em " + clazz.getName());
                }
                injectCtor = ctor;
            }
        }
        return injectCtor;
    }

    private void injectFields(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object value = resolveDependency(field.getType(), field.getGenericType());
                field.set(instance, value);
            }
        }
    }

    private void injectMethods(Object instance) throws Exception {
        Class<?> clazz = instance.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Inject.class)) {
                method.setAccessible(true);
                Object[] params = resolveParameters(method.getParameterTypes(), method.getGenericParameterTypes());
                method.invoke(instance, params);
            }
        }
    }

    private Object[] resolveParameters(Class<?>[] paramTypes, Type[] genericTypes) {
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = resolveDependency(paramTypes[i], genericTypes[i]);
        }
        return params;
    }

    private Object resolveDependency(Class<?> type, Type genericType) {
        if (Provider.class.equals(type)) {
            if (genericType instanceof ParameterizedType pt) {
                Type actualType = pt.getActualTypeArguments()[0];
                if (actualType instanceof Class<?> clazz) {
                    return (Provider<?>) () -> getInstance(clazz);
                }
            }
            throw new RuntimeException("Provider usado sem tipo genérico");
        }
        return getInstance(type);
    }

    private boolean isSingleton(Class<?> clazz) {
        return clazz.isAnnotationPresent(Singleton.class);
    }
}