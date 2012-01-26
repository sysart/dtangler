// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.jarfileparser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.dtangler.javaengine.classfileparser.ClassFileParser;
import org.dtangler.javaengine.types.JavaClass;

public class JarFileParser {

	private String path;
	private Map<String, byte[]> bytes = new HashMap();

	public Set<JavaClass> parse(File file) throws IOException {
		this.path = file.getAbsolutePath();
		readBytesFromJar();

		ClassFileParser parser = new ClassFileParser();
		Set<JavaClass> classes = new HashSet();
		for (String name : bytes.keySet()) {
			classes.add(parser.parse(new DataInputStream(
					new ByteArrayInputStream(bytes.get(name)))));
		}
		return classes;
	}

	private void readBytesFromJar() throws IOException {
		JarInputStream input = new JarInputStream(new FileInputStream(path));
		try {
			JarEntry nextJarEntry = input.getNextJarEntry();
			while (nextJarEntry != null) {
				if (nextJarEntry.getName().endsWith(".class"))
					readBytesFromJarEntry(nextJarEntry.getName(), input);
				nextJarEntry = input.getNextJarEntry();
			}
		} finally {
			input.close();
		}
	}

	private void readBytesFromJarEntry(String fromJarEntry, JarInputStream input)
			throws IOException {
		BufferedInputStream bufferedInput = new BufferedInputStream(input);
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		int b;
		while ((b = bufferedInput.read()) != -1)
			output.write(b);
		bytes.put(fromJarEntry, output.toByteArray());
	}

}
