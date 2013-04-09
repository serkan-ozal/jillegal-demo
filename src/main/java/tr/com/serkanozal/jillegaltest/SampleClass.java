package tr.com.serkanozal.jillegaltest;

public class SampleClass {

	public long l1, l2, l3, l4, l5, l6, l7, l8, l9;
	public int i1, i2, i3, i4, i5, i6, i7;
	
	public SampleClass() {
		System.out.println("SampleInstrumentClass.SampleClassToInstrument()"); 
	}
	
	public void methodToIntercept() {
		System.out.println("SampleInstrumentClass.methodToIntercept()"); 
	}
	
	public long getL1() {
		return l1;
	}
	
}
