//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.app;

import org.dtangler.swingui.windowmanager.UIExceptionHandler;

public class UIExceptionHandlerDelegator {

	private static UIExceptionHandler handler;

	static void setUIExceptionHandler(UIExceptionHandler handler) {
		UIExceptionHandlerDelegator.handler = handler;
	}

	public void handle(Throwable t) throws Throwable {
		if (handler != null)
			handler.handleUIException(t);
		else
			throw t;
	}
}
