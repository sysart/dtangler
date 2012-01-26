// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.ruleanalysis;

import java.util.Set;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;

public class Rule {

	public enum Type {
		cannotDepend, canDepend
	}

	private Type type;
	private RuleMember leftSide;
	private Set<RuleMember> rightSide;

	public Rule(Type type, RuleMember leftSide, Set<RuleMember> rightSide) {
		this.type = type;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
	}

	public Type getType() {
		return type;
	}

	public RuleMember getLeftSide() {
		return leftSide;
	}

	public Set<RuleMember> getRightSide() {
		return rightSide;
	}

	public boolean appliesTo(Dependency dependency) {
		return appliesToLeftSide(dependency.getDependant())
				&& appliesToRightSide(dependency.getDependee());
	}

	public boolean appliesToLeftSide(Dependable dependable) {
		return getLeftSide().appliesTo(dependable);
	}

	public boolean appliesToRightSide(Dependable dependable) {
		for (RuleMember rightSideItem : rightSide) {
			if (rightSideItem.appliesTo(dependable))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		String ruleType = " cannot depend on ";
		if (type.equals(Rule.Type.canDepend)) {
			ruleType = " can depend on ";
		}
		return leftSide + ruleType + getCommaSeparatedItems(rightSide);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((leftSide == null) ? 0 : leftSide.hashCode());
		result = prime * result
				+ ((rightSide == null) ? 0 : rightSide.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Rule))
			return false;
		Rule other = (Rule) obj;
		return type.equals(other.type) && leftSide.equals(other.leftSide)
				&& rightSide.equals(other.rightSide);
	}

	private String getCommaSeparatedItems(Set<RuleMember> items) {
		if (items.isEmpty())
			return "";
		return items.toString().substring(1, items.toString().length() - 1);
	}
}
