/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegaltest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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
                    insertBeforeConstructors(
                    	new BeforeConstructorInterceptor<SampleClass>() {
							@Override
							public void beforeConstructor(SampleClass obj, Constructor<SampleClass> constructor, Object[] args) {
								System.out.println("Intercepted by Jillegal before constructor ...");
							}
						}).
						
                    insertAfterConstructors("System.out.println(\"Intercepted by Jillegal after constructor ...\");").

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
	
	public static class SampleClass {

		public SampleClass() {
			System.out.println("SampleInstrumentClass.SampleClassToInstrument()"); 
		}
		
		public void methodToIntercept() {
			System.out.println("SampleInstrumentClass.methodToIntercept()"); 
		}
		
	}

}
