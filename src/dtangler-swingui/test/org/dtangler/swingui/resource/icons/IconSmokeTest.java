// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.resource.icons;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class IconSmokeTest {

	@Test
	public void testAllIconsArePresent() {
		for (IconKey key : IconKey.values()) {
			assertNotNull("Icon not found:" + key, IconProvider.getIcon(key));
		}
	}

}
