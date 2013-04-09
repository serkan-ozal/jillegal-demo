/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegaltest;

import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryService;
import tr.com.serkanozal.jillegal.offheap.memory.DirectMemoryServiceFactory;

public class DirectMemoryDemo {

	public static void main(String[] args) throws Exception {
		DirectMemoryService directMemoryService = DirectMemoryServiceFactory.getDirectMemoryService();
		
		SampleClass obj = new SampleClass();
		
		long addressOfField_b = directMemoryService.addressOfField(obj, "b");
		long addressOfField_i = directMemoryService.addressOfField(obj, "i");
		long addressOfField_l = directMemoryService.addressOfField(obj, "l");
		
		System.out.println("Value of b with direct memory access: " + directMemoryService.getByte(addressOfField_b));
		System.out.println("Value of i with direct memory access: " + directMemoryService.getInt(addressOfField_i));
		System.out.println("Value of l with direct memory access: " + directMemoryService.getLong(addressOfField_l));
		
		directMemoryService.putByte(addressOfField_b, (byte)10); // Note that b is final static field
		directMemoryService.putInt(addressOfField_i, 55);
		directMemoryService.putLong(addressOfField_l, 100);

		System.out.println("Values of b, i and l are changed with direct memory access ...");
		
		System.out.println("Value of b with direct memory access: " + directMemoryService.getByte(addressOfField_b));
		System.out.println("Value of i with direct memory access: " + directMemoryService.getInt(addressOfField_i));
		System.out.println("Value of l with direct memory access: " + directMemoryService.getLong(addressOfField_l));
		
		///////////////////////////////////////////////////////////////////////////////////////////
		
		SampleClass objSource = new SampleClass();
		SampleClass objTarget = new SampleClass();
		
		objSource.setI(100);
		objSource.setL(1000);
		
		objTarget.setI(200);
		objTarget.setL(2000);

		System.out.println("Value of i on targetObject: " + objTarget.getI());
		System.out.println("Value of l on targetObject: " + objTarget.getL());
		
		directMemoryService.changeObject(objSource, objTarget);
		
		System.out.println("Source object has been directly copied to target object ...");
		
		System.out.println("Value of i on targetObject: " + objTarget.getI());
		System.out.println("Value of l on targetObject: " + objTarget.getL());
		
		SampleClass[] sampleObjectArray = new SampleClass[100];
		sampleObjectArray[0] = new SampleClass();
		sampleObjectArray[1] = new SampleClass();
System.out.println(Long.toHexString(directMemoryService.addressOf(sampleObjectArray[0]) >> 3) );
System.out.println(Long.toHexString(directMemoryService.addressOf(sampleObjectArray[1]) >> 3) );
		SampleClass sc = new SampleClass();

		//c1 0f 69 ff : 1111 1111 0110 1001 0000 1111 1100 0001 
		//4b 13 69 ff : 1111 1111 0110 1001 0001 0011 0100 1011 

		System.out.println(Long.toHexString(directMemoryService.addressOfClass(SampleClass.class)));
		
		for (int i = 0; i < 100; i++) {
			System.out.print(String.format("%02x ", directMemoryService.getByte(SampleClass.class, (long)i)));
			if ((i + 1) % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println("\n\n");

		for (int i = 0; i < 100; i++) {
		    System.out.print(String.format("%02x ", directMemoryService.getByte(sampleObjectArray, (long)i)));
		    if ((i + 1) % 16 == 0) {
		    	System.out.println();
		    }
		}
		System.out.println("\n\n");

		for (int i = 0; i < 100; i++) {
		    System.out.print(String.format("%02x ", directMemoryService.getByte(sc, (long)i)));
		    if ((i + 1) % 16 == 0) {
		    	System.out.println();
		    }
		}
		System.out.println("\n\n");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////

	public static class SampleClass {
		
		private final static byte b = 100;
		
		private int i = 5;
		private long l = 10;
		
		public int getI() {
			return i;
		}
		
		public void setI(int i) {
			this.i = i;
		}

		public long getL() {
			return l;
		}
		
		public void setL(long l) {
			this.l = l;
		}

		public static byte getB() {
			return b;
		}
		
	}

}
