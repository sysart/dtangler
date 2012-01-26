// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.filefinder;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import org.dtangler.core.util.WildcardMatch;

public class FullPathWildCardFileFilter implements FileFilter {

	private final List<String> masksToIgnore;
	private final List<String> extensions;

	/**
	 * 
	 * @param extensions
	 *            file extensions that the file must have in order to even be
	 *            validated
	 * @param masksToIgnore
	 *            masks against the full path+filename are compared. If a
	 *            filename matches one or more of the masks, it is filtered out.
	 *            Wildcards ('*') are allowed in any position of theinput string
	 */
	public FullPathWildCardFileFilter(List<String> extensions,
			List<String> masksToIgnore) {
		this.extensions = extensions;
		this.masksToIgnore = masksToIgnore;

	}

	public boolean accept(File pathname) {
		if (!isValidExtension(pathname.getName()))
			return false;
		return !isMatch(pathname.getAbsolutePath());
	}

	private boolean isValidExtension(String name) {
		for (String extension : extensions) {
			if (name.toLowerCase().endsWith(extension))
				return true;
		}
		return false;
	}

	private boolean isMatch(String absolutePath) {
		for (String mask : masksToIgnore)
			if (isMatch(absolutePath, mask))
				return true;
		return false;
	}

	private boolean isMatch(String absolutePath, String mask) {
		if (absolutePath.equals(mask))
			return true;
		return new WildcardMatch(mask).isMatch(absolutePath);
	}

}
