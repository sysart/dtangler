// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

public class Dependable {

	private final Scope scope;
	private final String fullyQualifiedName;
	private final String displayName;
	private final int hashCode;
	private int contentCount;

	public Dependable(Scope scope, String fullyQualifiedName,
			String displayName, int contentCount) {
		this.scope = scope;
		this.fullyQualifiedName = fullyQualifiedName;
		this.displayName = displayName;
		setContentCount(contentCount);
		// profiler shows us that we'd better cache hashcode.
		hashCode = scope.hashCode() + fullyQualifiedName.hashCode();
	}

	// used for nr of classes inside packages. MAYBE this should be something
	// like Details getDetails + formatter
	public int getContentCount() {
		return contentCount;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Scope getScope() {
		return scope;
	}

	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dependable))
			return false;
		Dependable other = (Dependable) obj;
		return scope.equals(other.scope)
				&& fullyQualifiedName.equals(other.fullyQualifiedName);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}
}
