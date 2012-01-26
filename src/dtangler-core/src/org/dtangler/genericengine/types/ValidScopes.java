package org.dtangler.genericengine.types;

import java.util.ArrayList;
import java.util.List;

import org.dtangler.core.exception.DtException;

public class ValidScopes {

	private final static int MAX_INDEX_VALUE = 100;
	private final List<String> names = new ArrayList<String>();
	private ItemScope defaultScope = null;

	public String getScopeName(int index) {
		if (index < 0 || index > MAX_INDEX_VALUE)
			throw new DtException("invalid scope level " + (index+1));
		if (index > names.size() - 1)
			return null;
		return names.get(index);
	}

	public String getValidScopeName(int index) {
		String scopeName = getScopeName(index);
		if (scopeName == null) {
			throw new DtException("undefined scope name at level "+(index+1));
		}
		return scopeName;
	}
	
	public boolean containsUndefinedScopeNames() {
		for (int i = 0; i < names.size(); i++) {
			if (getScopeName(i) == null)
				return true;
		}
		return false;
	}

	public void generateScopeNamesForUndefinedScopeNames(String scopeNamePattern, String regex) {
		for (int index = 0; index < names.size(); index++) {
			if (getScopeName(index) == null) {
				setScopeName(scopeNamePattern.replaceFirst(regex, (index+1)+""), index);
			}
		}
	}

	public String[] getScopeNames() {
		String scopeNames[] = new String[names.size()];
		for (int i = 0; i < names.size(); i++) {
			scopeNames[i] = getScopeName(i);
		}
		return scopeNames;
	}

	public void setScopeName(String scopeName, int index) {
		if (scopeName == null)
			throw new DtException("invalid scope name: null");
		if (index < 0 || index > MAX_INDEX_VALUE)
			throw new DtException("invalid scope level " + (index+1)
					+ ", scope: " + scopeName);
		scopeName = scopeName.trim();
		if (names.contains(scopeName)) {
			if (index == names.indexOf(scopeName))
				return;
			throw new DtException("invalid scope name \"" + scopeName
					+ "\" at level " + (index+1)
					+ ": scope already exists at level "
					+ (names.indexOf(scopeName)+1));
		}
		if (defaultScope == null) {
			defaultScope = new ItemScope(scopeName, index);
		}
		if (index >= names.size()) {
			for (int i = names.size(); i <= index; i++) {
				names.add(null);
			}
		}
		names.set(index, scopeName);
	}

	public String getDefaultScopeName() {
		return defaultScope == null ? "" : defaultScope.getDisplayName();
	}

	public ItemScope getDefaultScope() {
		return defaultScope;
	}

	public int getScopeIndex(String scopeName) {
		if (scopeName == null)
			throw new DtException("invalid scope name: null");
		scopeName = scopeName.trim();
		if (names.contains(scopeName)) {
			return names.indexOf(scopeName);
		}
		throw new DtException("invalid scope \"" + scopeName + "\"");
	}

	public void clearScopeNames() {
		defaultScope = null;
		names.clear();
	}

	public int getNumberOfScopes() {
		return names.size();
	}

}
