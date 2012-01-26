//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.resource.icons;

import java.net.URL;

import javax.swing.ImageIcon;

public class IconProvider {

	public static ImageIcon getIcon(IconKey iconKey) {
		URL iconFile = IconProvider.class
				.getResource("/org/dtangler/swingui/resource/icons/"
						+ iconKey.name() + ".png");
		if (iconFile == null)
			return null;
		return new ImageIcon(iconFile);
	}

}
