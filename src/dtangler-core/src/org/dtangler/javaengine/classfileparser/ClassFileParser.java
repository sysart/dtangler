// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.javaengine.classfileparser;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dtangler.javaengine.types.JavaClass;

public class ClassFileParser {

	private static final String SOURCE_FILE = "SourceFile";
	private static final int CONSTANT_UTF8 = 1;
	private static final int JAVA_MAGIC = 0xCAFEBABE;
	private static final int CONSTANT_INTEGER = 3;
	private static final int CONSTANT_FLOAT = 4;
	private static final int CONSTANT_LONG = 5;
	private static final int CONSTANT_DOUBLE = 6;
	private static final int CONSTANT_CLASS = 7;
	private static final int CONSTANT_STRING = 8;
	private static final int CONSTANT_FIELD = 9;
	private static final int CONSTANT_METHOD = 10;
	private static final int CONSTANT_INTERFACEMETHOD = 11;
	private static final int CONSTANT_NAMEANDTYPE = 12;
	private static final char SEPARATOR = ';';
	private static final char CLASS_DESCRIPTOR = 'L';
	private static final char BRACKET_OPEN = '[';
	private static final char SLASH = '/';
	private static final char DOT = '.';
	private static final int ACC_INTERFACE = 0x200;
	private static final int ACC_ABSTRACT = 0x400;
	private final Constant DOUBLESLOT = new Constant((byte) 0, null);

	private Constant[] constantPool;

	public JavaClass parse(File classFile) {
		try {
			return parse(loadFile(classFile));
		} catch (IOException e) {
			throw new RuntimeException("Could not parse file:"
					+ classFile.getName(), e);
		}
	}

	private DataInput loadFile(File file) throws IOException {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] data = new byte[fileInputStream.available()];
			fileInputStream.read(data, 0, data.length);
			return new DataInputStream(new ByteArrayInputStream(data));
		} catch (RuntimeException e) {
			throw e;
		} finally {
			if (fileInputStream != null)
				fileInputStream.close();
		}
	}

	public JavaClass parse(DataInput in) throws IOException {
		checkMagic(in);
		skipVersionInfo(in);

		constantPool = parseConstantPool(in);
		boolean isAbstract = parseAccessFlags(in);
		JavaClass jClass = parseClassName(in);
		jClass.setAbstract(isAbstract);
		parseSuperClassName(in, jClass);
		parseInterfaces(in, jClass);
		parseFields(in, jClass);
		parseMethods(in, jClass);
		parseSourceFile(in, jClass);
		addClassConstantReferences(jClass);

		return jClass;
	}

	private void checkMagic(DataInput in) throws IOException {
		if (in.readInt() != JAVA_MAGIC)
			throw new RuntimeException("Bad Magic");
	}

	private void skipVersionInfo(DataInput in) throws IOException {
		// skip major and minor
		in.skipBytes(4);
	}

	private Constant[] parseConstantPool(DataInput in) throws IOException {
		Constant[] pool = new Constant[in.readUnsignedShort()];
		for (int i = 1; i < pool.length; i++) {
			Constant constant = parseNextConstant(in);
			pool[i] = constant;
			// 8-byte constants use two constant pool entries
			if (constant != null && DOUBLESLOT.equals(constant))
				i++;
		}
		return pool;
	}

	private boolean parseAccessFlags(DataInput in) throws IOException {
		int accessFlags = in.readUnsignedShort();

		boolean isAbstract = ((accessFlags & ACC_ABSTRACT) != 0);
		boolean isInterface = ((accessFlags & ACC_INTERFACE) != 0);
		return isAbstract || isInterface;
	}

	private JavaClass parseClassName(DataInput in) throws IOException {
		int entryIndex = in.readUnsignedShort();
		String className = getClassConstantName(entryIndex);
		return new JavaClass(className);
	}

	private void parseSuperClassName(DataInput in, JavaClass jClass)
			throws IOException {
		int entryIndex = in.readUnsignedShort();
		String superClassName = getClassConstantName(entryIndex);
		addDependency(superClassName, jClass);
	}

	private void parseInterfaces(DataInput in, JavaClass jClass)
			throws IOException {
		int interfacesCount = in.readUnsignedShort();
		for (int i = 0; i < interfacesCount; i++) {
			int entryIndex = in.readUnsignedShort();
			String interfaceName = getClassConstantName(entryIndex);
			addDependency(interfaceName, jClass);
		}
	}

	private void parseFields(DataInput in, JavaClass jClass) throws IOException {
		int fieldsCount = in.readUnsignedShort();

		for (int i = 0; i < fieldsCount; i++) {
			int descriptorIndex = parseFieldOrMethodInfo(in);
			String descriptor = toUTF8(descriptorIndex);
			List<String> types = descriptorToTypes(descriptor);
			for (int t = 0; t < types.size(); t++) {
				addDependency(types.get(t), jClass);
			}
		}
	}

	private void parseMethods(DataInput in, JavaClass jClass)
			throws IOException {
		int methodCount = in.readUnsignedShort();

		for (int i = 0; i < methodCount; i++) {
			int descriptorIndex = parseFieldOrMethodInfo(in);
			String descriptor = toUTF8(descriptorIndex);
			List<String> types = descriptorToTypes(descriptor);
			for (int t = 0; t < types.size(); t++) {
				addDependency(types.get(t), jClass);
			}
		}
	}

	private Constant parseNextConstant(DataInput in) throws IOException {
		byte tag = in.readByte();
		switch (tag) {
		case (CONSTANT_UTF8):
			return new Constant(tag, in.readUTF());
		case (CONSTANT_FIELD):
		case (CONSTANT_METHOD):
		case (CONSTANT_INTERFACEMETHOD):
		case (CONSTANT_NAMEANDTYPE):
		case (CONSTANT_INTEGER):
		case (CONSTANT_FLOAT):
			in.skipBytes(4);
			return null;
		case (CONSTANT_CLASS):
			return new Constant(tag, in.readUnsignedShort());
		case (CONSTANT_STRING):
			in.skipBytes(2);
			return null;
		case (CONSTANT_LONG):
		case (CONSTANT_DOUBLE):
			in.skipBytes(8);
			return DOUBLESLOT;
		}

		throw new IOException("Unknown constant: " + tag);
	}

	private int parseFieldOrMethodInfo(DataInput in) throws IOException {
		// skip accessFlags (unsigned short) and nameIndex (unsigned short)
		in.skipBytes(4);

		int descriptorIndex = in.readUnsignedShort();

		// skip attributes
		int attributesCount = in.readUnsignedShort();
		for (int a = 0; a < attributesCount; a++) {
			in.skipBytes(2);
			in.skipBytes(in.readInt());
		}
		return descriptorIndex;
	}

	private void parseSourceFile(DataInput in, JavaClass jClass)
			throws IOException {
		int attributesCount = in.readUnsignedShort();

		for (int i = 0; i < attributesCount; i++) {
			int nameIndex = in.readUnsignedShort();
			int length = in.readInt();
			if (nameIndex != -1 && SOURCE_FILE.equals(toUTF8(nameIndex))) {
				byte[] b = new byte[length];
				in.readFully(b);

				// Section 4.7.7 of VM Spec - Class File Format
				int b0 = b[0] < 0 ? b[0] + 256 : b[0];
				int b1 = b[1] < 0 ? b[1] + 256 : b[1];
				int pe = b0 * 256 + b1;

				jClass.setSourceFile(toUTF8(pe));
				break;
			} else
				in.skipBytes(length);
		}
	}

	private Constant getConstantPoolEntry(int entryIndex) throws IOException {
		if (entryIndex < 0 || entryIndex >= constantPool.length) {
			throw new IOException("Illegal constant pool index : " + entryIndex);
		}

		return constantPool[entryIndex];
	}

	private void addClassConstantReferences(JavaClass jClass)
			throws IOException {
		for (int j = 1; j < constantPool.length; j++) {
			Constant constant = constantPool[j];
			if (constant != null && constant.getTag() == CONSTANT_CLASS) {
				String name = toUTF8(constant.getNameIndex());
				addDependency(name, jClass);
			}
		}
	}

	private String getClassConstantName(int entryIndex) throws IOException {
		Constant entry = getConstantPoolEntry(entryIndex);
		return slashesToDots(toUTF8(entry.getNameIndex()));
	}

	private String toUTF8(int entryIndex) throws IOException {
		Constant entry = getConstantPoolEntry(entryIndex);
		if (entry.getTag() == CONSTANT_UTF8)
			return (String) entry.getValue();

		throw new IOException("Constant pool entry is not a UTF8 type: "
				+ entryIndex);
	}

	private String slashesToDots(String s) {
		return s.replace(SLASH, DOT);
	}

	private void addDependency(String s, JavaClass jClass) {
		if (s.charAt(0) == BRACKET_OPEN) {
			List<String> types = descriptorToTypes(s);
			if (types.size() == 0)
				return; // primitives

			s = types.get(0);
		}

		s = slashesToDots(s);
		jClass.addDependency(s);
	}

	private List<String> descriptorToTypes(String descriptor) {
		List<String> types = null;
		int startIndex = 0;
		int splitIndex = descriptor.indexOf(SEPARATOR, startIndex);
		while (splitIndex >= 0) {
			String string = descriptor.substring(startIndex, splitIndex);
			startIndex = splitIndex + 1;
			splitIndex = descriptor.indexOf(SEPARATOR, startIndex);

			int index = string.indexOf(CLASS_DESCRIPTOR);
			if (index >= 0) {
				if (types == null)
					types = new ArrayList();
				types.add(string.substring(index + 1));
			}
		}

		return types != null ? types : Collections.EMPTY_LIST;
	}

	class Constant {

		private final byte tag;
		private final int nameIndex;
		private final Object value;

		Constant(byte tag, Object value) {
			this.tag = tag;
			this.nameIndex = -1;
			this.value = value;
		}

		Constant(byte tag, int nameIndex) {
			this.tag = tag;
			this.nameIndex = nameIndex;
			this.value = null;
		}

		byte getTag() {
			return tag;
		}

		int getNameIndex() {
			return nameIndex;
		}

		Object getValue() {
			return value;
		}
	}
}