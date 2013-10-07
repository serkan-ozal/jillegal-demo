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
import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.SequentialObjectOffHeapPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.model.pool.ObjectPoolReferenceType;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ComplexTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.EagerReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.ExtendableObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.LazyReferencedObjectOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.pool.impl.PrimitiveTypeArrayOffHeapPool;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapService;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapServiceFactory;

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
	
	private static void demoObjectOffHeapPool(OffHeapService offHeapService) {
		long start, finish;
		long freeMemory1, freeMemory2;
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		freeMemory1 = Runtime.getRuntime().freeMemory();
		System.out.println("Free memory on heap before Off-Heap allocation: " + freeMemory1 + " bytes");
		
		start = System.currentTimeMillis();
		
		EagerReferencedObjectOffHeapPool<SampleClass> eagerReferencedSequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new SequentialObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(ELEMENT_COUNT).
								referenceType(ObjectPoolReferenceType.EAGER_REFERENCED).
							build());
							
		System.out.println("Sequential Off Heap Object Pool with size " + ELEMENT_COUNT + " for class " + 
				SampleClass.class.getName() + " has been allocated ...");
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = eagerReferencedSequentialObjectPool.get();
    		obj.setOrder(i);
    		obj.setLink(new SampleClass());
    	}
    	
    	for (int i = 0; i < ELEMENT_COUNT; i++) {
    		eagerReferencedSequentialObjectPool.getAt(i);
    	}
    	
		finish = System.currentTimeMillis();
		
		System.out.println("Sequential Off Heap Object Pool for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		freeMemory2 = Runtime.getRuntime().freeMemory();
		System.out.println("Free memory on heap after Off-Heap allocation: " + freeMemory2 + " bytes");
		
		System.out.println("Memory used by Off-Heap allocation: " + (freeMemory2 - freeMemory1) + " bytes");
		
		//////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("\n");
	
		//////////////////////////////////////////////////////////////////////////////////////
		
		freeMemory1 = Runtime.getRuntime().freeMemory();
		System.out.println("Free memory on heap before On-Heap allocation: " + freeMemory1 + " bytes");
		
		start = System.currentTimeMillis();
		
		SampleClass[] array = new SampleClass[ELEMENT_COUNT];
		
		System.out.println("Array for class with size " + ELEMENT_COUNT + " for class " +  
				SampleClass.class.getName() + " has been allocated ...");
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = new SampleClass();
    		obj.setOrder(i);
    		obj.setLink(new SampleClass());
    		array[i] = obj;
    	}
		
		for (int i = 0; i < ELEMENT_COUNT; i++) {
    		SampleClass obj = array[i];
    	}
    	
		finish = System.currentTimeMillis();
		
		System.out.println("Array for class " + 
				SampleClass.class.getName() + " has been allocated, got and set for " + 
				ELEMENT_COUNT + " elements in " + (finish - start) + " milliseconds ...");
		
		freeMemory2 = Runtime.getRuntime().freeMemory();
		System.out.println("Free memory on heap after On-Heap allocation: " + freeMemory2 + " bytes");
		
		System.out.println("Memory used by On-Heap allocation: " + (freeMemory2 - freeMemory1) + " bytes");
	}
	
	private static void demoLazyReferencedObjectOffHeapPool(OffHeapService offHeapService) {
		LazyReferencedObjectOffHeapPool<SampleClass> lazyReferencedSequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new SequentialObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
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
						new SequentialObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
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
						new SequentialObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
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
						new SequentialObjectOffHeapPoolCreateParameterBuilder<SampleClass>().
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

	public static class SampleClass {
		
		private int i1 = 5;
		private int i2 = 10;
		private int order;
		private SampleClass link;

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
		
		public SampleClass getLink() {
			return link;
		}
		
		public void setLink(SampleClass link) {
			this.link = link;
		}

	}
	
}
