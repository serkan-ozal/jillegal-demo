/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegaltest;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.management.VMManagement;
import tr.com.serkanozal.jillegal.Jillegal;

import tr.com.serkanozal.jillegal.instrument.Instrumenter;
import tr.com.serkanozal.jillegal.instrument.domain.model.GeneratedClass;
import tr.com.serkanozal.jillegal.instrument.interceptor.constructor.BeforeConstructorInterceptor;
import tr.com.serkanozal.jillegal.instrument.interceptor.method.BeforeMethodInterceptor;
import tr.com.serkanozal.jillegal.instrument.service.InstrumenterService;
import tr.com.serkanozal.jillegal.instrument.service.InstrumenterServiceFactory;

public class InstrumentationDemo {

	public static void main(String[] args) throws Exception {
		Jillegal.init();
		
		System.out.println("Before Intrumentation: ");
		System.out.println("=====================================================");
		
		SampleClass obj1 = new SampleClass();
		obj1.methodToIntercept();
		
		System.out.println("=====================================================");
		
		
		System.out.println("After Intrumentation: ");
		System.out.println("=====================================================");
		
        InstrumenterService instrumenterService = InstrumenterServiceFactory.getInstrumenterService();
        Instrumenter<SampleClass> inst = instrumenterService.getInstrumenter(SampleClass.class);
        GeneratedClass<SampleClass> redefinedClass =
        		
                inst.
                /*
                    insertBeforeConstructors(
                    	new BeforeConstructorInterceptor<SampleClass>() {
							@Override
							public void beforeConstructor(SampleClass obj, Constructor<SampleClass> constructor, Object[] args) {
								System.out.println("Intercepted by Jillegal before constructor ...");
							}
						}).
						
                    insertAfterConstructors("System.out.println(\"Intercepted by Jillegal after constructor ...\");").
                    */
                    insertBeforeMethod("methodToIntercept", 
                    	new BeforeMethodInterceptor<SampleClass>() {
							@Override
                    		public void beforeMethod(SampleClass obj, Method method, Object[] args) {
								System.out.println("Intercepted by Jillegal before methodToIntercept method ...");
							}
						}).
							
                    insertAfterMethod("methodToIntercept", 
                    	"System.out.println(\"Intercepted by Jillegal after methodToIntercept method ...\");").
                    			
                 build();

        instrumenterService.redefineClass(redefinedClass); 

        SampleClass obj2 = new SampleClass();
        obj2.methodToIntercept();
        
        System.out.println("=====================================================");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	public static class SampleClass {

		public long l1, l2, l3, l4, l5, l6, l7, l8, l9;
		public int i1, i2, i3, i4, i5, i6, i7;
		
		public SampleClass() {
			System.out.println("SampleInstrumentClass.SampleClassToInstrument()"); 
		}
		
		public void methodToIntercept() {
			System.out.println("SampleInstrumentClass.methodToIntercept()"); 
		}
		
	}
	*/
	
	private static String getPidFromRuntimeMBean() throws Exception {
        RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        Field jvmField = mxbean.getClass().getDeclaredField("jvm");

        jvmField.setAccessible(true);
        VMManagement management = (VMManagement) jvmField.get(mxbean);
        Method method = management.getClass().getDeclaredMethod("getProcessId");
        method.setAccessible(true);
        Integer processId = (Integer) method.invoke(management);

        return processId.toString();
    }
	
}
