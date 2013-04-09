/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegaltest;

import tr.com.serkanozal.jillegal.offheap.domain.builder.pool.SequentialObjectPoolCreateParameterBuilder;
import tr.com.serkanozal.jillegal.offheap.domain.model.pool.SequentialObjectPoolCreateParameter.SequentialObjectPoolReferenceType;
import tr.com.serkanozal.jillegal.offheap.pool.EagerReferencedObjectPool;
import tr.com.serkanozal.jillegal.offheap.pool.LazyReferencedObjectPool;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapService;
import tr.com.serkanozal.jillegal.offheap.service.OffHeapServiceFactory;

public class OffHeapDemo {

	public static void main(String[] args) throws Exception {
		final int OBJECT_COUNT = 2;

		OffHeapService offHeapService = OffHeapServiceFactory.getOffHeapService();
		EagerReferencedObjectPool<SampleClass> sequentialObjectPool = 
				offHeapService.createOffHeapPool(
						new SequentialObjectPoolCreateParameterBuilder<SampleClass>().
								type(SampleClass.class).
								objectCount(OBJECT_COUNT).
								referenceType(SequentialObjectPoolReferenceType.EAGER_REFERENCED).
							build()
				);
   
		System.out.println("Off heap object pool for class " + SampleClass.class.getName() + " has been allocated ...");
		
    	for (int i = 0; i < OBJECT_COUNT; i++) {
    		SampleClass obj = sequentialObjectPool.newObject();
    		obj.setOrder(i);
    		System.out.println("New object has been retrieved from off heap pool and set order value to " + i);
    	}
    	
    	for (int i = 0; i < OBJECT_COUNT; i++) {
    		SampleClass obj = sequentialObjectPool.getObject(i);
    		System.out.println("Order value of " + i + ". object at off heap pool: " + obj.getOrder());
    	}
    	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public static class SampleClass {
		
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
