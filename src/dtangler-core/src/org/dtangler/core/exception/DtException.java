// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.exception;

public class DtException extends RuntimeException {

	public DtException(String messageToUser) {
		super(messageToUser);
	}

	public DtException(String messageToUser, Throwable cause) {
		super(messageToUser, cause);
	}

}
