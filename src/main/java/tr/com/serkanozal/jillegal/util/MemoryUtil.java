/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegal.util;

public class MemoryUtil {
	
	static {
		System.loadLibrary("MemoryUtil");
	}
	
	private MemoryUtil() {
        
    }
	
	public static Class<?> pinClass(Class<?> clazz) {
		return pinClass(clazz.getName().replace(".", "/"));
	}
    
	public static native Class<?> pinClass(String className);
	public static native Object pinObject(Object obj);
	
	public static void main(String[] args) {
		pinClass(String.class);
	}
    
}
