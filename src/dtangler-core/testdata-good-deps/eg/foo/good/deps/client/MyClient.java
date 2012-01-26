// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package eg.foo.good.deps.client;

import eg.foo.good.deps.api.MyApi;

public class MyClient {
	public static final AnotherClass a = new AnotherClass();
	MyApi api;

	public void doSomething() {
		new YetAnotherClass().toString();
	}
}
