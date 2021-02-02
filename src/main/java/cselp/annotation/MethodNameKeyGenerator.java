package cselp.annotation;

import org.springframework.cache.interceptor.DefaultKeyGenerator;

import java.lang.reflect.Method;

public class MethodNameKeyGenerator extends DefaultKeyGenerator {

    public Object generate(Object target, Method method, Object... params) {
        Object key = super.generate(target, method, params);
        return method.getName() + "-" + key.hashCode();
    }
}
