package com.ai.cloud.skywalking.plugin.interceptor.enhance;

import com.ai.cloud.skywalking.plugin.exception.PluginException;
import com.ai.cloud.skywalking.plugin.AbstractClassEnhancePluginDefine;
import com.ai.cloud.skywalking.plugin.interceptor.MethodMatcher;
import com.ai.cloud.skywalking.plugin.interceptor.enhance.exception.FailedLoadEnhanceCodeSegmentException;
import javassist.*;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class ClassEnhancePluginDefine extends AbstractClassEnhancePluginDefine {

    public byte[] enhance(CtClass ctClass) throws PluginException {
        try {
            boolean isFirstEnhanceInstance = true;
            CtMethod[] ctMethod = ctClass.getDeclaredMethods();
            for (CtMethod method : ctMethod) {
                if (Modifier.isStatic(method.getModifiers())) {
                    this.enhanceClass(ctClass, method);
                } else {
                    this.enhanceInstance(ctClass, method, isFirstEnhanceInstance);
                    isFirstEnhanceInstance = false;
                }
            }

            return ctClass.toBytecode();
        } catch (Exception e) {
            throw new PluginException("Can not compile the class", e);
        }
    }

    private void enhanceClass(CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException, FailedLoadEnhanceCodeSegmentException {
        boolean isMatch = false;
        for (MethodMatcher methodMatcher : getStaticMethodsMatchers()) {
            if (methodMatcher.match(method)) {
                isMatch = true;
                break;
            }
        }

        if (isMatch) {
            // 修改方法名,
            String methodName = method.getName();
            String newMethodName = methodName + "SkywalkingEnhance";
            method.setName(newMethodName);

            CtMethod newMethod = new CtMethod(method.getReturnType(), methodName, method.getParameterTypes(), method.getDeclaringClass());

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("interceptor_class", getStaticMethodsInterceptor().getClass().getName());
            parameters.put("origin_method_name", methodName);
            parameters.put("class_name", ctClass.getName());
            parameters.put("new_method_name", newMethodName);

            newMethod.setBody(CodeGenerator.generate("enhance_static_method_code", parameters));

            ctClass.addMethod(newMethod);
        }

    }

    private void enhanceInstance(CtClass ctClass, CtMethod method, boolean isFirstEnhanceInstance)
            throws CannotCompileException, NotFoundException, FailedLoadEnhanceCodeSegmentException {
        if (isFirstEnhanceInstance) {
            // 添加一个字段,并且带上get/set方法
            CtField ctField = CtField.make(CodeGenerator.generate("add_instance_context_code"), ctClass);
            ctClass.addField(ctField);

            // 初始化构造函数
            CtConstructor[] constructors = ctClass.getDeclaredConstructors();
            for (CtConstructor constructor : constructors) {
                Map<String, String> parameter = new HashMap<String, String>();
                parameter.put("interceptor_class", getInstanceMethodsInterceptor().getClass().getName());
                constructor.insertAfter(CodeGenerator.generate("enhance_constructor_code", parameter));
            }
        }

        boolean isMatch = false;
        for (MethodMatcher methodMatcher : getInstanceMethodsMatchers()) {
            if (methodMatcher.match(method)) {
                isMatch = true;
                break;
            }
        }

        if (isMatch) {
            // 修改方法名,
            String methodName = method.getName();
            String newMethodName = methodName + "_$SkywalkingEnhance";
            method.setName(newMethodName);
            CtMethod newMethod = new CtMethod(method.getReturnType(), methodName, method.getParameterTypes(), ctClass);
            newMethod.setExceptionTypes(method.getExceptionTypes());

            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("interceptor_class", getInstanceMethodsInterceptor().getClass().getName());
            parameters.put("origin_method_name", methodName);
            parameters.put("new_method_name", java.util.regex.Matcher.quoteReplacement(newMethodName));

            newMethod.setBody(CodeGenerator.generate("enhance_instance_method_code", parameters));

            ctClass.addMethod(newMethod);
        }
    }

    /**
     * 返回需要被增强的方法列表
     *
     * @return
     */
    protected abstract MethodMatcher[] getInstanceMethodsMatchers();

    /**
     * 返回增强拦截器的实现<br/>
     * 每个拦截器在同一个被增强类的内部，保持单例
     *
     * @return
     */
    protected abstract InstanceMethodsAroundInterceptor getInstanceMethodsInterceptor();

    /**
     * 返回需要被增强的方法列表
     *
     * @return
     */
    protected abstract MethodMatcher[] getStaticMethodsMatchers();

    /**
     * 返回增强拦截器的实现<br/>
     * 每个拦截器在同一个被增强类的内部，保持单例
     *
     * @return
     */
    protected abstract StaticMethodsAroundInterceptor getStaticMethodsInterceptor();
}
