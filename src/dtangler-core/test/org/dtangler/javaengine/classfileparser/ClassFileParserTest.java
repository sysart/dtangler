// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.classfileparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.dtangler.javaengine.classfileparser.ClassFileParser;
import org.dtangler.javaengine.types.JavaClass;
import org.junit.Test;

public class ClassFileParserTest {

	@Test
	public void testGetClassName() throws IOException {
		JavaClass result = parse("SimpleClass.class");
		assertEquals(getTestDataPackage() + ".SimpleClass", result
				.getFullName());

	}

	private String getTestDataPackage() {
		return "org.dtangler.javaengine.classfileparser.testdata";
	}

	@Test
	public void testGetSourceFileName() throws IOException {
		JavaClass result = parse("SimpleClass.class");
		assertEquals("SimpleClass.java", result.getSourceFileName());
	}

	@Test
	public void testIsAbstract() {
		assertFalse(parse("SimpleClass.class").isAbstract());
		assertTrue(parse("SimpleInterface.class").isAbstract());
		assertTrue(parse("SimpleAbstractClass.class").isAbstract());
	}

	@Test
	public void testClassWithNoDependencies() {
		assertTrue(parse("SimpleClass.class").getDependencies().isEmpty());
	}

	@Test
	public void testExtendedClass() {
		Set<String> dependencies = parse("SimpleExtendedClass.class")
				.getDependencies().keySet();
		assertEquals(1, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	@Test
	public void testSimpleClassWithPrimitiveFields() {
		assertTrue(parse("SimpleClassWithPrimitiveFields.class")
				.getDependencies().isEmpty());
	}

	@Test
	public void testSimpleFieldDependencies() {
		Set<String> dependencies = parse("SimpleFieldDependencies.class")
				.getDependencies().keySet();
		assertEquals(4, dependencies.size());
		assertTrue(dependencies.contains("java.lang.String"));
		assertTrue(dependencies.contains("java.util.Set"));
		assertTrue(dependencies.contains("java.util.HashSet"));
		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	@Test
	public void testSimpleMethodParameterDependencies() {
		Set<String> dependencies = parse(
				"SimpleMethodParameterDependencies.class").getDependencies()
				.keySet();
		assertEquals(2, dependencies.size());
		assertTrue(dependencies.contains("java.lang.String"));
		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	@Test
	public void testSimpleMethodReturnValueDependencies() {
		Set<String> dependencies = parse(
				"SimpleMethodReturnValueDependencies.class").getDependencies()
				.keySet();
		assertEquals(2, dependencies.size());
		assertTrue(dependencies.contains("java.lang.String"));
		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	@Test
	public void testSimpleDependenciesInsideMethods() {
		Set<String> dependencies = parse(
				"SimpleDependenciesInsideMethods.class").getDependencies()
				.keySet();
		assertEquals(2, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage()
				+ ".SimpleClassWithStaticMethod"));

		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	// TODO This does not work yet: @Test
	public void testSimpleAnnotationDependency() {
		Set<String> dependencies = parse("SimpleAnnotationDependency.class")
				.getDependencies().keySet();
		assertEquals(1, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage()
				+ ".SimpleAnnotation"));
	}

	@Test
	public void testInnerClassDependency() {
		Set<String> dependencies = parse("SimpleClassWithInnerClass.class")
				.getDependencies().keySet();
		assertEquals(1, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage()
				+ ".SimpleClassWithInnerClass"));

		dependencies = parse("SimpleClassWithInnerClass$InnerClass.class")
				.getDependencies().keySet();
		assertEquals(1, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage()
				+ ".SimpleClassWithInnerClass"));
	}

	// TODO This does not work yet: @Test
	public void testStaticInnerClassDependency() {
		Set<String> dependencies = parse(
				"SimpleClassWithStaticInnerClass.class").getDependencies()
				.keySet();
		assertEquals(0, dependencies.size());
		dependencies = parse("SimpleClassWithStaticInnerClass$InnerClass.class")
				.getDependencies().keySet();
		assertEquals(0, dependencies.size());
	}

	// TODO This does not work yet: @Test
	public void testTemplateDependency() {
		Set<String> dependencies = parse("SimpleTemplateDependency.class")
				.getDependencies().keySet();
		assertEquals(1, dependencies.size());
		assertTrue(dependencies.contains(getTestDataPackage() + ".SimpleClass"));
	}

	private JavaClass parse(String path) {
		InputStream stream = getClass().getResourceAsStream("testdata/" + path);
		try {
			return new ClassFileParser().parse(new DataInputStream(stream));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
