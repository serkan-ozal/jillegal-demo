/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegaltest;

import java.util.ArrayList;
import java.util.List;

import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ArrayOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.DefaultExtendableObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ExtendableObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.ObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.model.pool.ObjectPoolReferenceType;
import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryService;
import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryServiceFactory;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ComplexTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.EagerReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ExtendableObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.LazyReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.PrimitiveTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapService;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapServiceFactory;
import tr.com.serkanozal.jillegal.util.JvmUtil;

@SuppressWarnings("unused")
public class OffHeapDemo {

	private static final int ELEMENT_COUNT = 10000000;
	private static final int TOTAL_ELEMENT_COUNT = 10000;
	
	public static void main(String[] args) throws Exception {
		OffHeapService offHeapService = OffHeapServiceFactory.getOffHeapService();

		demoObjectOffHeapPool(offHeapService);
		
//		demoLazyReferencedObjectOffHeapPool(offHeapService);
//		demoEagerReferencedObjectOffHeapPool(offHeapService);
//		demoComplexTypeArrayOffHeapPool(offHeapService);
//		demoPrimitiveTypeArrayOffHeapPool(offHeapService);
//		demoExtendableObjectOffHeapPoolWithLazyReferenceObjectOffHeapPool(offHeapService);
//		demoExtendableObjectOffHeapPoolWithEagerReferenceObjectOffHeapPool(offHeapService);
//		demoExtendableObjectOffHeapPoolWithDefaultObjectOffHeapPool(offHeapService);
	}
	
	private static void demoObjectOffHeapPool(OffHeapService offHeapService) throws Exception {
		long start, finish;
		long usedMemory1, usedMemory2;
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		JvmUtil.runGC();
		
		Thread.sleep(2000);
		
		//////////////////////////////////////////////////////////////////////////////////////

		usedMemory1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory on heap before On-Heap allocation: " + usedMemory1 + " bytes");
		
		start = System.currentTimeMillis();
		
		SampleClass[] array = new SampleClass[ELEMENT_COUNT];
		
		System.out.println("Array for class with size " + ELEMENT_COUNT + " for class " +  
				SampleClass.class.getName() + " has been allocated ...");
		
		SampleLinkClass link1 = new SampleLinkClass();
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = new SampleClass();
    		obj.setOrder(i);
    		obj.setLink(link1);
    		array[i] = obj;
    	}
		
		long start2 = System.currentTimeMillis();
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = array[i];
    		SampleLinkClass link = obj.getLink();
    	}
		
		System.out.println(System.currentTimeMillis() - start2);
    	
		finish = System.currentTimeMillis();
		
		System.out.println("Array for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		usedMemory2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory on heap after On-Heap allocation: " + usedMemory2 + " bytes");
		
		System.out.println("Memory used by On-Heap allocation: " + (usedMemory2 - usedMemory1) + " bytes");
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("\n");
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		JvmUtil.runGC();
		
		Thread.sleep(2000);
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		usedMemory1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory on heap before Off-Heap allocation: " + usedMemory1 + " bytes");
		
		start = System.currentTimeMillis();
		
		EagerReferencedObjectOffHeapPool<SampleClass> eagerReferencedObjectPool = 
				offHeapService.createOffHeapPool(
						new ObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								autoImplementNonPrimitiveFieldSetters(true).
								referenceType(ObjectPoolReferenceType.EAGER_REFERENCED).
							build());
		
		JvmUtil.runGC();
		
		Thread.sleep(2000);
			
		System.out.println("Sequential Off Heap Object Pool with size " + ELEMENT_COUNT + " for class " + 
				SampleClass.class.getName() + " has been allocated ...");

		DirectMemoryService directMemoryService = DirectMemoryServiceFactory.getDirectMemoryService();
		
		SampleLinkClass link2 = new SampleLinkClass();

		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = eagerReferencedObjectPool.get();
    		obj.setOrder(i);
    		obj.setLink(link2);
    	}

		long start1 = System.currentTimeMillis();
		
		SampleClass[] objArray = eagerReferencedObjectPool.getObjectArray();
    	for (int i = 0; i < objArray.length; i++) {
    		SampleClass obj = objArray[i];
    		SampleLinkClass link = obj.getLink();
    	}
    	
    	System.out.println(System.currentTimeMillis() - start1);
    	
		finish = System.currentTimeMillis();
		
		System.out.println("Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		usedMemory2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used memory on heap after Off-Heap allocation: " + usedMemory2 + " bytes");
		
		System.out.println("Memory used by Off-Heap allocation: " + (usedMemory2 - usedMemory1) + " bytes");
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
		ComplexTypeArrayOffHeapPool<SampleClass> complexTypeArrayPoolWithNoInit = 
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
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	private static class SampleBaseClass {
		
	}

	private static class SampleClass {
		
		private int i1 = 5;
		private int i2 = 10;
		private int order;
		private SampleLinkClass link;
		
		public SampleClass() {
			
		}

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
		
		public SampleLinkClass getLink() {
			return link;
		}
		
		public void setLink(SampleLinkClass link) {
			this.link = link;
		}

	}
	
	private static class SampleLinkClass {
		
		private long linkNo;

		public long getLinkNo() {
			return linkNo;
		}
		
		public void setLinkNo(long linkNo) {
			this.linkNo = linkNo;
		}

	}
	
}
