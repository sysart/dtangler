// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.configuration;

public class ParserConstants {

	// General stuff
	public static final String VALUE_TRUE = "true";
	public static final String VALUE_FALSE = "false";

	public static final String DEPENDENCY_ENGINE_ID_KEY = "engine";
	public static final String DEPENDENCY_ENGINE_ID_VALUE_JAVA = "java";
	public static final String DEPENDENCY_ENGINE_ID_VALUE_GENERIC = "generic";

	public static final String CONFIG_FILE_KEY = "configFile";

	public static final String BIG_SEPARATOR = ";";
	public static final String SMALL_SEPARATOR = ",";

	public static final String IGNORE_FILE_MASK_KEY = "ignoreFileMask";

	public static final String CYCLES_ALLOWED_KEY = "cyclesAllowed";

	public static final String RULES_KEY = "rules";
	public static final String CANNOT_DEPEND = "cannot depend on";
	public static final String CAN_DEPEND = "can depend on";

	public static final String GROUPS_KEY = "groups";
	// backward compatibility:
	public static final String GROUP_KEY = "group";

	public static final String CONTAINS = "contains";
	public static final String DOES_NOT_CONTAIN = "does not contain";
	public static final String GROUP_IDENTIFIER = "@";

	public static final String SCOPE_KEY = "scope";

	public static final String INPUT_KEY = "input";
	public static final String INPUT_KEY_VALUE_STANDARD_INPUT = "stdin";

	// backward compatibility:
	public static final String CLASS_PATH_KEY = "classPath";

	public static final String[] VALID_KEYS = { DEPENDENCY_ENGINE_ID_KEY,
			INPUT_KEY, CLASS_PATH_KEY, IGNORE_FILE_MASK_KEY,
			CYCLES_ALLOWED_KEY, RULES_KEY, GROUPS_KEY, GROUP_KEY,
			CONFIG_FILE_KEY, SCOPE_KEY };
}
