/**
 * @author SERKAN OZAL
 *         
 *         E-Mail: <a href="mailto:serkanozal86@hotmail.com">serkanozal86@hotmail.com</a>
 *         GitHub: <a>https://github.com/serkan-ozal</a>
 */

package tr.com.serkanozal.jillegal.demo;

import tr.com.serkanozal.jillegal.compiler.domain.model.DefaultCodeType;
import tr.com.serkanozal.jillegal.compiler.exception.ClassCompileException;
import tr.com.serkanozal.jillegal.compiler.service.ClassCompilerService;
import tr.com.serkanozal.jillegal.compiler.service.ClassCompilerServiceFactory;

public class ClassCompilerDemo {

	private static final ClassCompilerService classCompilerService = 
			ClassCompilerServiceFactory.getClassCompilerService();
	
	public static void main(String[] args) throws Exception {
		//compileAndBuildJavaCode();
		compileAndBuildGroovyCode();
	}
	
	/**
	 * JavaClassCompiler doesn't work due to incorrect class major/minor version 
	 * between tools.jar and Java 8. Currently tools.jar is build on Java 6. 
	 * As soon as possible, Java 7 and Java 8 versions of tools.jar will be added
	 */
	@SuppressWarnings("unused")
	private static void compileAndBuildJavaCode() 
			throws ClassCompileException, InstantiationException, IllegalAccessException {
		String code = 
			"package tr.com.serkanozal.jillegal.compiler.demo;"+ "\n" +
			"\n" +
			"public class SampleJavaClass {" + "\n" +
			"\n" +
			"\t" + "public String toString() {" + "\n" +
			"\t" + "\t" + "return \"I am SampleJavaClass\";" + "\n" +
			"\t" + 	"}" + "\n" +
			"\n" +
			"}";

		Class<?> compiledClass = classCompilerService.getClassCompiler(DefaultCodeType.JAVA).compile(code);
		Object obj = compiledClass.newInstance();
		
		System.out.println(obj.toString());
	}
	
	private static void compileAndBuildGroovyCode() 
			throws ClassCompileException, InstantiationException, IllegalAccessException {
		String code = 
			"package tr.com.serkanozal.jillegal.compiler.demo;"+ "\n" +
			"\n" +
			"public class SampleGroovyClass {" + "\n" +
			"\n" +
			"\t" + "public String toString() {" + "\n" +
			"\t" + "\t" + "def str = \"I am SampleGroovyClass\";" + "\n" +
			"\t" + "\t" + "return str;" + "\n" +
			"\t" + 	"}" + "\n" +
			"\n" +
			"}";

		Class<?> compiledClass = classCompilerService.getClassCompiler(DefaultCodeType.GROOVY).compile(code);
		Object obj = compiledClass.newInstance();
		
		System.out.println(obj.toString());
	}
	
}
