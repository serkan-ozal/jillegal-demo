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
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.SequentialObjectPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.pool.SequentialObjectPool;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapService;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapServiceFactory;

public class Main {

	public static void main(String[] args) throws Exception {
		Jillegal.init();
		
		main1(args);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main1(String[] args) {
		
		final int OBJECT_COUNT = 10000;

		OffHeapService offHeapService = OffHeapServiceFactory.getOffHeapService();
		SequentialObjectPool<SampleOffHeapClass> sequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new SequentialObjectPoolCreateParameterBuilder<SampleOffHeapClass>().
								type(SampleOffHeapClass.class).
								objectCount(OBJECT_COUNT).
							build()
				);
   
    	for (int i = 0; i < OBJECT_COUNT; i++) {
    		SampleOffHeapClass obj = sequentialObjectPool.newObject();
    		obj.setOrder(i);
    	}
    	
    	for (int i = 0; i < OBJECT_COUNT; i++) {
    		SampleOffHeapClass obj = sequentialObjectPool.getObject(i);
    		System.out.println(obj.getOrder());
    	}
    	
	}

	public static class SampleOffHeapClass {
		
		private int i1 = 5;
		private int i2 = 10;
		private int order;

		public int getI1() {
			return i1;
		}
		
		public int getI2() {
			return i2;
		}
		
		public int getOrder() {
			return order;
		}
		
		public void setOrder(int order) {
			this.order = order;
		}

	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main2(String[] args) throws Exception {
		
		SampleClassToInstrument obj1 = new SampleClassToInstrument();
		obj1.methodToIntercept();

        InstrumenterService instrumenterService = InstrumenterServiceFactory.getInstrumenterService();
        Instrumenter<SampleClassToInstrument> inst = instrumenterService.getInstrumenter(SampleClassToInstrument.class);
        GeneratedClass<SampleClassToInstrument> redefinedClass =
        		
                inst.
                
                    insertBeforeConstructors(
                    	new BeforeConstructorInterceptor<SampleClassToInstrument>() {
							@Override
							public void beforeConstructor(SampleClassToInstrument obj, Constructor<SampleClassToInstrument> constructor, Object[] args) {
								System.out.println("Intercepted by Jillegal before constructor ...");
							}
						}).
						
                    insertAfterConstructors("System.out.println(\"Intercepted by Jillegal after constructor ...\");").
                    
                    insertBeforeMethod("methodToIntercept", 
                    	new BeforeMethodInterceptor<SampleClassToInstrument>() {
							@Override
							public void beforeMethod(SampleClassToInstrument obj, Method method, Object[] args) {
								System.out.println("Intercepted by Jillegal before methodToIntercept method ...");
							}
						}).
							
                    insertAfterMethod("methodToIntercept", 
                    			"System.out.println(\"Intercepted by Jillegal after methodToIntercept method ...\");").
                    			
                 build();

        instrumenterService.redefineClass(redefinedClass); 
        
        SampleClassToInstrument obj2 = new SampleClassToInstrument();
        obj2.methodToIntercept();
	}
	
	public static class SampleClassToInstrument {

		public SampleClassToInstrument() {
			System.out.println("SampleInstrumentClass.SampleClassToInstrument()"); 
		}
		
		public void methodToIntercept() {
			System.out.println("SampleInstrumentClass.methodToIntercept()"); 
		}
		
	}

}
