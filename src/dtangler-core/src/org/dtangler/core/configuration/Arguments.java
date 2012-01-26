// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class Arguments {

	/**
	 * Contains the path(s) to the items that are being analyzed for
	 * dependencies.
	 */
	private List<String> input = Collections.EMPTY_LIST;

	/**
	 * Logical groups of items that can be used, for example, in rule
	 * definitions.
	 */
	private Map<String, Group> groups = Collections.EMPTY_MAP;

	/**
	 * Defines whether cyclic dependencies will fail the build. The default
	 * value is false.
	 */
	private boolean cyclesAllowed = false;

	/**
	 * Maps forbidden dependencies. The items on the key side can not depend on
	 * any items on the right side.
	 */
	private Map<String, Set<String>> forbiddenDependencies = Collections.EMPTY_MAP;

	/**
	 * Maps allowed dependencies. As opposed to forbidden dependencies, these
	 * override any possible forbidden dependency.
	 */
	private Map<String, Set<String>> allowedDependencies = Collections.EMPTY_MAP;

	/**
	 * file masks that are ignored when scanning the input paths. Masks are case
	 * sensitive and can contain multiple '*' wildcards
	 */
	private List<String> ignoredFileMasks = Collections.EMPTY_LIST;

	/**
	 * Defines the analyzing scope. This parameter is interpreted by the
	 * frontend used.
	 */
	private String scope;

	private String configFileName;

	/**
	 * The id of the dependency engine. The default value is
	 * DependencyEngineFactory.DEFAULT_DEPENDENCY_ENGINE_ID
	 */
	private String dependencyEngineId;

	public Arguments createDeepCopy() {
		Arguments copy = new Arguments();
		copy.setInput(new ArrayList(getInput()));
		copy.setConfigFileName(getConfigFileName());
		copy.setCyclesAllowed(getCyclesAllowed());
		copy
				.setForbiddenDependencies(createDeepCopy(getForbiddenDependencies()));
		copy.setAllowedDependencies(createDeepCopy(getAllowedDependencies()));
		copy.setGroups(createGroupDeepCopy(getGroups()));
		copy.setIgnoredFileMasks(new ArrayList(ignoredFileMasks));
		copy.setScope(getScope());
		copy.setDependencyEngineId(getDependencyEngineId());
		return copy;
	}

	private Map<String, Group> createGroupDeepCopy(Map<String, Group> orig) {
		Map<String, Group> copy = new HashMap();
		for (Entry<String, Group> entry : orig.entrySet()) {
			Group g = entry.getValue();
			copy.put(entry.getKey(), new Group(g.getName(), g.getGroupItems(),
					g.getExcludedItems()));
		}
		return copy;
	}

	private Map<String, Set<String>> createDeepCopy(
			Map<String, Set<String>> orig) {
		Map<String, Set<String>> copy = new HashMap();
		for (Entry<String, Set<String>> entry : orig.entrySet())
			copy.put(entry.getKey(), new HashSet(entry.getValue()));
		return copy;
	}

	public void setInput(List<String> pathList) {
		input = pathList;
	}

	public List<String> getInput() {
		return input;
	}

	public Map<String, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, Group> groups) {
		this.groups = groups;
	}

	public void setCyclesAllowed(boolean cyclesAllowed) {
		this.cyclesAllowed = cyclesAllowed;
	}

	public boolean getCyclesAllowed() {
		return cyclesAllowed;
	}

	public Map<String, Set<String>> getForbiddenDependencies() {
		return forbiddenDependencies;
	}

	public Map<String, Set<String>> getAllowedDependencies() {
		return allowedDependencies;
	}

	public void setForbiddenDependencies(Map<String, Set<String>> cannotDepend) {
		forbiddenDependencies = cannotDepend;
	}

	public void setAllowedDependencies(Map<String, Set<String>> canDepend) {
		allowedDependencies = canDepend;
	}

	public List<String> getIgnoredFileMasks() {
		return ignoredFileMasks;
	}

	public void setIgnoredFileMasks(List<String> ignoredFileMasks) {
		this.ignoredFileMasks = ignoredFileMasks;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Arguments))
			return false;
		Arguments other = (Arguments) obj;
		return this.input.equals(other.input)
				&& nullSaveEquals(this.configFileName, other.configFileName)
				&& this.cyclesAllowed == other.cyclesAllowed
				&& this.forbiddenDependencies
						.equals(other.forbiddenDependencies)
				&& this.allowedDependencies.equals(other.allowedDependencies)
				&& this.groups.equals(other.groups)
				&& this.ignoredFileMasks.equals(other.ignoredFileMasks)
				&& nullSaveEquals(this.scope, other.scope)
				&& nullSaveEquals(this.dependencyEngineId,
						other.dependencyEngineId);
	}

	private boolean nullSaveEquals(Object o1, Object o2) {
		if (o1 == null)
			return o2 == null;
		return o1.equals(o2);
	}

	public int hashCode() {
		return input.hashCode() + nullSaveHashCode(configFileName)
				+ (cyclesAllowed ? 0 : 17) + forbiddenDependencies.hashCode()
				+ allowedDependencies.hashCode() + groups.hashCode()
				+ ignoredFileMasks.hashCode() + nullSaveHashCode(scope)
				+ nullSaveHashCode(dependencyEngineId);
	}

	private int nullSaveHashCode(Object obj) {
		return obj != null ? obj.hashCode() : 0;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getDependencyEngineId() {
		return dependencyEngineId;
	}

	public void setDependencyEngineId(String dependencyEngineId) {
		this.dependencyEngineId = dependencyEngineId;
	}

}
