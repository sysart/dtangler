// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.dependencies;

public class Dependency {

	private final Dependable dependant;
	private final Dependable dependee;

	public Dependency(Dependable dependant, Dependable dependee) {
		this.dependant = dependant;
		this.dependee = dependee;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dependency))
			return false;
		Dependency other = (Dependency) obj;
		return dependant.equals(other.dependant)
				&& dependee.equals(other.dependee);
	}

	@Override
	public int hashCode() {
		return dependant.hashCode() + dependee.hashCode();
	}

	public Dependable getDependant() {
		return dependant;
	}

	public Dependable getDependee() {
		return dependee;
	}

	@Override
	public String toString() {
		return dependant + "-->" + dependee;
	}

}
