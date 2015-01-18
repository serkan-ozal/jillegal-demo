/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegal.demo;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.InternalException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.connect.spi.TransportService.ListenKey;
import com.sun.tools.jdi.ProcessAttachingConnector;
import com.sun.tools.jdi.SharedMemoryAttachingConnector;
import com.sun.tools.jdi.SharedMemoryListeningConnector;
import com.sun.tools.jdi.SocketAttachingConnector;
import com.sun.tools.jdi.SocketListeningConnector;
import com.sun.tools.jdi.SocketTransportService;

import sun.jvm.hotspot.jdi.VirtualMachineImpl;
import tr.com.serkanozal.jillegal.Jillegal;
import tr.com.serkanozal.jillegal.config.annotation.JillegalAware;
import tr.com.serkanozal.jillegal.offheap.config.provider.annotation.OffHeapArray;
import tr.com.serkanozal.jillegal.offheap.config.provider.annotation.OffHeapObject;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ArrayOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.DefaultExtendableObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ExtendableObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ExtendableStringOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.StringOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.model.pool.ObjectOffHeapPoolCreateParameter;
import tr.com.serkanozal.jillegal.offheap.domain.model.pool.ObjectPoolReferenceType;
import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryService;
import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryServiceFactory;
import tr.com.serkanozal.jillegal.offheap.pool.DeeplyForkableStringOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.StringOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ComplexTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.EagerReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ExtendableObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ExtendableStringOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.LazyReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.PrimitiveTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapService;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapServiceFactory;
import tr.com.serkanozal.jillegal.util.JvmUtil;

@SuppressWarnings("unused")
public class OffHeapDemo {

	private static final int ELEMENT_COUNT = 100000;
	private static final int TOTAL_ELEMENT_COUNT = 10000;
	
	private static final int STRING_COUNT = 1000;
	private static final int TOTAL_STRING_COUNT = 10000;
	private static final int ESTIMATED_STRING_LENGTH = 20;

	static {
		Jillegal.init();
	}
	
	// -Xms1G -Xmx1G -XX:-UseTLAB -XX:-UseCompressedOops -verbose:gc -XX:+PrintGCDetails -XX:+UseSerialGC
	// -Xms1G -Xmx1G -XX:-UseTLAB -XX:-UseCompressedOops -verbose:gc -XX:+PrintGCDetails -XX:+UseConcMarkSweepGC 
	// -Xms1G -Xmx1G -XX:-UseTLAB -XX:-UseCompressedOops -verbose:gc -XX:+PrintGCDetails -XX:+UseG1GC
	// Note that: Crashes with "-XX:+UseParallelGC"
	// -Xms1G -Xmx1G -XX:-UseTLAB -XX:-UseCompressedOops -verbose:gc -XX:+PrintGCDetails -XX:+UseParallelGC
	public static void main(String[] args) throws Exception {
		final int TEST_STEP_COUNT = 1;
		
		OffHeapService offHeapService = OffHeapServiceFactory.getOffHeapService();

		switch (TEST_STEP_COUNT) {
			case 1:
				demoObjectOffHeapPool(offHeapService);
				break;
			case 2:
				demoJillegalAwareOffHeap(offHeapService);				
				break;				
			case 3:
				demoLazyReferencedObjectOffHeapPool(offHeapService);	
				break;	
			case 4:
				demoEagerReferencedObjectOffHeapPool(offHeapService);
				break;
			case 5:
				demoComplexTypeArrayOffHeapPool(offHeapService);
				break;
			case 6:
				demoPrimitiveTypeArrayOffHeapPool(offHeapService);
				break;	
			case 7:
				demoExtendableObjectOffHeapPoolWithLazyReferenceObjectOffHeapPool(offHeapService);
				break;	
			case 8:
				demoExtendableObjectOffHeapPoolWithEagerReferenceObjectOffHeapPool(offHeapService);
				break;	
			case 9:
				demoExtendableObjectOffHeapPoolWithDefaultObjectOffHeapPool(offHeapService);
				break;	
			case 10:
				demoStringOffHeapPool(offHeapService);
				break;	
			case 11:
				demoExtendableStringOffHeapPool(offHeapService);
				break;		
		}	
	}
	
	private static void demoObjectOffHeapPool(OffHeapService offHeapService) throws Exception {
		long start, finish;
		long usedMemory1, usedMemory2;

		MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
		
		//////////////////////////////////////////////////////////////////////////////////////

		JvmUtil.runGC();
		
		Thread.sleep(2000);
		
		usedMemory1 = memoryBean.getHeapMemoryUsage().getUsed();
		System.out.println("Used memory on heap before On-Heap allocation: " + usedMemory1 + " bytes");
		
		start = System.currentTimeMillis();
		
		SampleClass[] array = new SampleClass[ELEMENT_COUNT];
		
		System.out.println("Array for class with size " + ELEMENT_COUNT + " for class " +  
				SampleClass.class.getName() + " has been allocated ...");

		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = new SampleClass();
    		obj.setOrder(i);
    		array[i] = obj;
    	}
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = array[i];
    	}

		finish = System.currentTimeMillis();
		
		System.out.println("Array for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		usedMemory2 =  memoryBean.getHeapMemoryUsage().getUsed();
		System.out.println("Used memory on heap after On-Heap allocation: " + usedMemory2 + " bytes");
		
		System.out.println("Memory used by On-Heap allocation: " + (usedMemory2 - usedMemory1) + " bytes");
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("\n");
		
		JvmUtil.runGC();
		
		Thread.sleep(2000);
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		ObjectOffHeapPoolCreateParameterBuilder<SampleClass> offHeapPoolParameterBuilder = 
				new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
						type(SampleClass.class).
						objectCount(ELEMENT_COUNT).
						makeOffHeapableAsAuto(true).
						referenceType(ObjectPoolReferenceType.EAGER_REFERENCED);
		ObjectOffHeapPoolCreateParameter<SampleClass> offHeapPoolParameter = offHeapPoolParameterBuilder.build();
		
		offHeapService.makeOffHeapable(SampleClass.class);
		
		offHeapService.newObject(SampleClass.class);

		DirectMemoryService directMemoryService = DirectMemoryServiceFactory.getDirectMemoryService();

		usedMemory1 = memoryBean.getHeapMemoryUsage().getUsed();
		System.out.println("Used memory on heap before Off-Heap allocation: " + usedMemory1 + " bytes");
		
		start = System.currentTimeMillis();

		EagerReferencedObjectOffHeapPool<SampleClass> eagerReferencedObjectPool = 
				offHeapService.createOffHeapPool(offHeapPoolParameter);
		
		System.out.println("Sequential Off Heap Object Pool with size " + ELEMENT_COUNT + " for class " + 
				SampleClass.class.getName() + " has been allocated ...");

		for (int i = 0; i < ELEMENT_COUNT; i++) {
			SampleClass obj = eagerReferencedObjectPool.get();
    		obj.setOrder(i);
    	}

		SampleClass[] objArray = eagerReferencedObjectPool.getObjectArray();
    	for (int i = 0; i < objArray.length; i++) {
    		SampleClass obj = objArray[i];
    	}

		finish = System.currentTimeMillis();
		
		System.out.println("Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		usedMemory2 = memoryBean.getHeapMemoryUsage().getUsed();
		System.out.println("Used memory on heap after Off-Heap allocation: " + usedMemory2 + " bytes");
		
		System.out.println("Memory used by Off-Heap allocation: " + (usedMemory2 - usedMemory1) + " bytes");
	}
	
	private static void demoJillegalAwareOffHeap(OffHeapService offHeapService) throws Exception {
		System.out.println("Object Array Off-Heap Pool for class " + SampleClass.class.getName() + 
				" has been automatically allocated and injected for Jillegal-Aware class ...");
		
		JillegalAwareSampleClassWrapper sampleClassWrapper = new JillegalAwareSampleClassWrapper();
		
		sampleClassWrapper.getSampleClass().setOrder(-1);
		
		SampleClass[] objArray = sampleClassWrapper.getSampleClassArray();
    	
		for (int i = 0; i < objArray.length; i++) {
    		SampleClass obj = objArray[i];
    		obj.setOrder(i);
    		System.out.println("Order value of auto injected off-heap object field has been set to " + i);
    	}
		
		System.out.println("Order value of sample object at off heap pool: " + 
				sampleClassWrapper.getSampleClass().getOrder());
    	
		for (int i = 0; i < objArray.length; i++) {
			SampleClass obj = objArray[i];
			System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
		}
		
		System.out.println("\n\n");
	}
	
	private static void demoLazyReferencedObjectOffHeapPool(OffHeapService offHeapService) {
		LazyReferencedObjectOffHeapPool<SampleClass> lazyReferencedSequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								referenceType(ObjectPoolReferenceType.LAZY_REFERENCED).
							build());	
		
		System.out.println("Lazy Referenced Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = lazyReferencedSequentialObjectPool.get();
    		obj.setOrder(i);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = lazyReferencedSequentialObjectPool.getAt(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
		System.out.println("\n\n");
	}
	
	private static void demoEagerReferencedObjectOffHeapPool(OffHeapService offHeapService) {
		EagerReferencedObjectOffHeapPool<SampleClass> eagerReferencedSequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								referenceType(ObjectPoolReferenceType.EAGER_REFERENCED).
							build());
							
		System.out.println("Eager Referenced Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = eagerReferencedSequentialObjectPool.get();
    		obj.setOrder(i);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = eagerReferencedSequentialObjectPool.getAt(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
		System.out.println("\n\n");
	}

	private static void demoComplexTypeArrayOffHeapPool(OffHeapService offHeapService) {
		ComplexTypeArrayOffHeapPool<SampleClass, SampleClass[]> complexTypeArrayPoolWithNoInit = 
				offHeapService.createOffHeapPool(
						new ArrayOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								length(ELEMENT_COUNT).
								initializeElements(false).
							build());
		
		System.out.println("Complex Type Off Heap Array Pool with no initialization for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		SampleClass[] complexArrayWithNoInit = complexTypeArrayPoolWithNoInit.getArray();
		
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = new SampleClass();
    		obj.setOrder(i);
    		complexTypeArrayPoolWithNoInit.setAt(obj, i); // Note that "array[i] = obj" is not valid, because JVM doesn't know array created at off-heap
    		System.out.println("New complex typed element has been created on heap and " + 
    				"assigned to off heap pool array " + i);
    	}
   
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = complexArrayWithNoInit[i];
    		System.out.println("Order value of " + i + ". element at off heap pool: " + obj.getOrder());
    	}
    	
		System.out.println("\n\n");
	}
	
	private static void demoPrimitiveTypeArrayOffHeapPool(OffHeapService offHeapService) {
		PrimitiveTypeArrayOffHeapPool<Integer, int[]> primitiveTypeArrayPool = 
				offHeapService.createOffHeapPool(
						new ArrayOffHeapPoolCreateParameterBuilder<Integer>().
								type(Integer.class).
								length(ELEMENT_COUNT).
								usePrimitiveTypes(true).
							build());
		
		System.out.println("Primitive Type Off Heap Array Pool for class " + 
				int.class.getName() + " has been allocated ...");
		
		int[] primitiveArray = primitiveTypeArrayPool.getArray();
		
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		primitiveArray[i] = i;
    		System.out.println("New pritimive typed element has been assigned to off heap pool array " + i);
    	}
    	
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		int order = primitiveArray[i];
    		System.out.println("Order value of " + i + ". element at off heap pool: " + order);
    	}	
    	
    	System.out.println("\n\n");
	}

	private static void demoExtendableObjectOffHeapPoolWithLazyReferenceObjectOffHeapPool(OffHeapService offHeapService) {
		LazyReferencedObjectOffHeapPool<SampleClass> sequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								referenceType(ObjectPoolReferenceType.LAZY_REFERENCED).
							build());

		ExtendableObjectOffHeapPool<SampleClass> extendableObjectPool =
				offHeapService.createOffHeapPool(
						new ExtendableObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								forkableObjectOffHeapPool(sequentialObjectPool).
							build());
		
		System.out.println("Extendable Lazy Referenced Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		List<SampleClass> objList = new ArrayList<SampleClass>();
		
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = extendableObjectPool.get();
    		obj.setOrder(i);
    		objList.add(obj);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = objList.get(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
    	System.out.println("\n\n");
	}
	
	private static void demoExtendableObjectOffHeapPoolWithEagerReferenceObjectOffHeapPool(OffHeapService offHeapService) {
		EagerReferencedObjectOffHeapPool<SampleClass> sequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								referenceType(ObjectPoolReferenceType.EAGER_REFERENCED).
							build());
   
		ExtendableObjectOffHeapPool<SampleClass> extendableObjectPool =
				offHeapService.createOffHeapPool(
						new ExtendableObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								forkableObjectOffHeapPool(sequentialObjectPool).
							build());
		
		System.out.println("Extendable Eager Referenced Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		List<SampleClass> objList = new ArrayList<SampleClass>();
		
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = extendableObjectPool.get();
    		obj.setOrder(i);
    		objList.add(obj);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = objList.get(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
    	System.out.println("\n\n");
	}
	
	private static void demoExtendableObjectOffHeapPoolWithDefaultObjectOffHeapPool(OffHeapService offHeapService) {
		ExtendableObjectOffHeapPool<SampleClass> extendableObjectPool =
				offHeapService.createOffHeapPool(
						new DefaultExtendableObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								elementType(SampleClass.class).
							build());
		
		System.out.println("Extendable Default Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		List<SampleClass> objList = new ArrayList<SampleClass>();
		
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = extendableObjectPool.get();
    		obj.setOrder(i);
    		objList.add(obj);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < TOTAL_ELEMENT_COUNT; i++) {
    		SampleClass obj = objList.get(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
    	System.out.println("\n\n");
	}
	
	private static void demoStringOffHeapPool(OffHeapService offHeapService) {
		StringOffHeapPool stringPool = 
				offHeapService.createOffHeapPool(
						new StringOffHeapPoolCreateParameterBuilder().
								estimatedStringCount(STRING_COUNT).
								estimatedStringLength(ESTIMATED_STRING_LENGTH).
							build());
   
    	for (int i = 0; i < STRING_COUNT; i++) {
    		System.out.println(stringPool.get("String " + i));
    	}
	}
	
	private static void demoExtendableStringOffHeapPool(OffHeapService offHeapService) {
		DeeplyForkableStringOffHeapPool stringPool = 
				offHeapService.createOffHeapPool(
						new StringOffHeapPoolCreateParameterBuilder().
								estimatedStringCount(STRING_COUNT).
								estimatedStringLength(ESTIMATED_STRING_LENGTH).
							build());
   
		ExtendableStringOffHeapPool extendableStringPool =
				offHeapService.createOffHeapPool(
						new ExtendableStringOffHeapPoolCreateParameterBuilder().
								forkableStringOffHeapPool(stringPool).
							build());
		
		for (int i = 0; i < TOTAL_STRING_COUNT; i++) {
			System.out.println(extendableStringPool.get("String " + i));
    	}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	private static class SampleClassWrapper {
		
		private SampleClass sampleClass;
		
		public SampleClass getSampleClass() {
			return sampleClass;
		}
		
		public void setSampleClass(SampleClass sampleClass) {
			this.sampleClass = sampleClass;
		}
		
	}
	
	@JillegalAware
	private static class JillegalAwareSampleClassWrapper {
		
		@OffHeapObject
		private SampleClass sampleClass;
		
		@OffHeapArray(length = 1000)
		private SampleClass[] sampleClassArray;
		
		public SampleClass getSampleClass() {
			return sampleClass;
		}
		
		public void setSampleClass(SampleClass sampleClass) {
			this.sampleClass = sampleClass;
		}
		
		public SampleClass[] getSampleClassArray() {
			return sampleClassArray;
		}
		
		public void setSampleClassArray(SampleClass[] sampleClassArray) {
			this.sampleClassArray = sampleClassArray;
		}
		
	}

	private static class SampleClass {
		
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

}
