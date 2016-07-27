package test.ai.cloud.plugin;

import com.ai.cloud.skywalking.plugin.TracingBootstrap;
import com.ai.cloud.skywalking.plugin.exception.PluginException;
import javassist.NotFoundException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class PluginMainTest {
    @Test
    public void testMain()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, NotFoundException,
            PluginException {
        TracingBootstrap.main(new String[] {"sample.ai.cloud.plugin.PluginMainTest"});
    }

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
            SecurityException {
        long start = System.currentTimeMillis();

        BeInterceptedClass inst = (BeInterceptedClass) Class.forName("sample.ai.cloud.plugin.BeInterceptedClass").newInstance();
        inst.printabc();
        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms");

        BeInterceptedClass.call();
    }
}
