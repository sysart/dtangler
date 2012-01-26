// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.genericengine.types;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.dtangler.core.exception.DtException;

public class Item {

	private final Map<Item, Integer> dependencies = new HashMap<Item, Integer>();
	private String scope = "";
	private String displayname;
	private String[] parentDisplaynames;
	private String encoding;
	private int contentCount = 1;

	public Item(String displayname) {
		init("", displayname, null, "UTF-8");
	}

	public Item(String scope, String displayname) {
		init(scope, displayname, null, "UTF-8");
	}

	public Item(String scope, String displayname,
			String[] parentDisplaynames) {
		init(scope, displayname, parentDisplaynames, "UTF-8");
	}

	public Item(String scope, String displayname,
			String[] parentDisplaynames, String encoding) {
		init(scope, displayname, parentDisplaynames, encoding);
	}


	private void init(String scope, String displayname,
			String[] parentDisplaynames, String encoding) {
		try {
			this.scope = decodeValue(scope.trim(), encoding);
		} catch (Exception e) {
			throw new DtException("invalid scope in item definition: "
					+ e.getMessage());
		}
		try {
			this.displayname = decodeValue(displayname.trim(), encoding);
		} catch (Exception e) {
			throw new DtException("invalid displayname in item definition: "
					+ e.getMessage());
		}
		if (parentDisplaynames != null) {
			try {
				this.parentDisplaynames = new String[parentDisplaynames.length];
				int iParent = 0;
				for (String parent : parentDisplaynames) {
					this.parentDisplaynames[iParent++] = decodeValue(parent
							.trim(), encoding);
				}
			} catch (Exception e) {
				throw new DtException(
						"invalid parent name list in item definition: "
								+ e.getMessage());
			}
		}
		this.encoding = encoding;
	}

	public static String decodeValue(String str) {
		return decodeValue(str, "UTF-8");
	}
	
	public static String decodeValue(String str, String encoding) {
		if (str == null)
			throw new DtException("no item value to decode");
		try {
			return URLDecoder.decode(str, encoding);
		} catch (Exception e) {
			throw new DtException("unable to decode value" + str);
		}
	}

	public static String encodeValue(String str) {
		return encodeValue(str, "UTF-8");
	}

	public static String encodeValue(String str, String encoding) {
		if (str == null || str.trim().length() == 0)
			throw new DtException("no item value to encode");
		try {
			return URLEncoder.encode(str, encoding);
		} catch (Exception e) {
			throw new DtException("unable to encode value " + str);
		}
	}

	public void addDependency(Item item) {
		if (item.equals(this))
			return;
		Integer weight = dependencies.get(item);
		if (weight == null) {
			dependencies.put(item, 1);
		} else {
			dependencies.put(item, ++weight);
		}
	}

	public String getDisplayname() {
		return this.displayname;
	}

	public String getScope() {
		return this.scope;
	}

	public int getScopeIndex() {
		if (this.parentDisplaynames == null)
			return 0;
		return this.parentDisplaynames.length;
	}

	public String[] getParentDisplaynames() {
		return parentDisplaynames;
	}

	public String getFullyqualifiedname() {
		String fullName = "";
		if (parentDisplaynames != null) {
			for (String parent : parentDisplaynames) {
				fullName += (encodeValue(parent, this.encoding) + " ");
			}
		}
		fullName += encodeValue(this.displayname, this.encoding);
		return fullName;
	}

	public static String getFullyqualifiedDisplayname(String name, String encoding) {
		if (name == null)
			return null;
		if (encoding != null) {
			name = decodeValue(name, encoding);
		}
		return name.replaceAll(" ", "/");
	}

	public String[] getParentFullyqualifiednames() {
		if (parentDisplaynames == null) {
			return null;
		}
		String[] fullNames = new String[parentDisplaynames.length];
		for (int iParent = 0; iParent < parentDisplaynames.length; iParent++) {
			fullNames[iParent] = "";
			for (int jParent = 0; jParent < iParent; jParent++) {
				if (parentDisplaynames[iParent] != null) {
					fullNames[iParent] += (encodeValue(
							parentDisplaynames[jParent], this.encoding) + " ");
				}
			}
			fullNames[iParent] += encodeValue(parentDisplaynames[iParent], this.encoding);
		}
		return fullNames;
	}

	public String getItemDefinitionAsString() {
		return scope.length() > 0 ? scope + "{" + getFullyqualifiedname() + "}"
				: getFullyqualifiedname();
	}

	public Map<Item, Integer> getDependencies() {
		return this.dependencies;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Item))
			return false;
		Item other = (Item) obj;
		return getItemDefinitionAsString().equals(
				other.getItemDefinitionAsString());
	}

	@Override
	public int hashCode() {
		return getItemDefinitionAsString().hashCode();
	}

	@Override
	public String toString() {
		return String.format("Item[%s]", getItemDefinitionAsString());
	}

	public int getContentCount() {
		return this.contentCount;
	}

	public void setContentCount(int contentCount) {
		this.contentCount = contentCount;
	}
	
	public String getEncoding() {
		return this.encoding;
	}
}
