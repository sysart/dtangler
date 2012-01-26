//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.testutil.output;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class FileUtil {

	public static String readFile(String fileName) {
		try {
			File file = new File(fileName);
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(file));
			byte[] bytes = new byte[(int) file.length()];
			stream.read(bytes);
			return new String(bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
