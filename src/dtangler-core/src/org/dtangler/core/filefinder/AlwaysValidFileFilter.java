// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.filefinder;

import java.io.File;
import java.io.FileFilter;

public class AlwaysValidFileFilter implements FileFilter {
	public boolean accept(File pathname) {
		return true;
	}
}
