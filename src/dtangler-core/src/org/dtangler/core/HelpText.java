//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core;

import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.input.CommandLineParser;

public class HelpText {

	public final static String helpText = "\nUsage: \n"
			+ "\t-<run option>=<argument>\n\n"

			+ "\tYou can enter multiple arguments to the same option by separating\n"
			+ "\tthem with a semicolon (;).\n\n"

			+ "\tYou can enter multiple values to the same argument by separating\n"
			+ "\tthem with a comma (,).\n\n"

			+ "List of possible run options:\n\n"

			+ "Setup: \n\n" +

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.CONFIG_FILE_KEY)
			+ "<path and name of the configuration properties file>\n"
			+ "\t\tPath of the config file.\n\n"
			+

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.INPUT_KEY)
			+ "<path to folder containing the items being analyzed |\n"
			+ "\t\tpath and name of the dependencies file>\n"
			+ "\t\tWith "
			+ CommandLineParser.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY)
			+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_JAVA
			+ ", path of items being analyzed.\n"
			+ "\t\tWith "
			+ CommandLineParser.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY)
			+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC
			+ ", path and name of the file\n"
			+ "\t\tcontaining the definitions of the dependencies (.dt) or\n"
			+ "\t\t"
			+ ParserConstants.INPUT_KEY_VALUE_STANDARD_INPUT
			+ " if the definitions are read from standard input.\n"
			+ "\t\t"
			+ CommandLineParser.getKeyString(ParserConstants.INPUT_KEY)
			+ " is interchangeable with the older "
			+ CommandLineParser.getKeyString(ParserConstants.CLASS_PATH_KEY)
			+ " run option.\n\n"
			+

			"\t"
			+ CommandLineParser
					.getKeyString(ParserConstants.IGNORE_FILE_MASK_KEY)
			+ "<mask for items ignored>\n"
			+ "\t\tItems being ignored.\n\n"
			+

			"\t"
			+ CommandLineParser
					.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY)
			+ "<"+ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_JAVA+"|"+ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC+"|yourownengine>\n"
			+ "\t\tThe id of the dependency engine. Default value: "+ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_JAVA+"\n\n"

			+ "Configuring dependency rules: \n\n"
			+

			"\t"
			+ CommandLineParser
					.getKeyString(ParserConstants.CYCLES_ALLOWED_KEY)
			+ "<true|false>\n"
			+ "\t\tAllow/disallow cyclic dependencies. Default value: false\n\n"
			+

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.GROUPS_KEY)
			+ "<group name> "
			+ ParserConstants.CONTAINS
			+ " <item name> "
			+ ParserConstants.DOES_NOT_CONTAIN
			+ " <item name>\n"
			+ "\t\tGroup items to simplify configuration.\n"
			+ "\t\t"
			+ CommandLineParser.getKeyString(ParserConstants.GROUPS_KEY)
			+ " is interchangeable with the older "
			+ CommandLineParser.getKeyString(ParserConstants.GROUP_KEY)
			+ " run option.\n\n"
			+

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.RULES_KEY)
			+ "<item name> "
			+ ParserConstants.CANNOT_DEPEND
			+ " <item name>"
			+ ParserConstants.BIG_SEPARATOR
			+ "<item name> "
			+ ParserConstants.CAN_DEPEND
			+ " <item name>\n"
			+ "\t\tSpecify forbidden and allowed dependencies. \n"
			+ "\t\tAllowed dependencies override forbidden dependencies.\n\n"
			+

			"Java-specific parameters: \n\n"
			+

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.SCOPE_KEY)
			+ "<locations|packages|classes>\n"
			+ "\t\tSpecify the scope of the dependency analysis. \n"
			+ "\t\tUse this option with the "
			+ CommandLineParser.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY)
			+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_JAVA + " option. \n"
			+ "\t\tDefault value: packages\n\n"
			+

			"Generic dependency engine -specific parameters: \n\n"
			+

			"\t"
			+ CommandLineParser.getKeyString(ParserConstants.SCOPE_KEY)
			+ "<any scope defined in the dependency definitions file>\n"
			+ "\t\tSpecify the scope of the dependency analysis.\n" 
			+ "\t\tUse this option with the "
			+ CommandLineParser.getKeyString(ParserConstants.DEPENDENCY_ENGINE_ID_KEY)
			+ ParserConstants.DEPENDENCY_ENGINE_ID_VALUE_GENERIC + " option. \n"
			+ "\t\tDefault value: first scope found from the dependency definitions file\n\n"
			+

			"The full documentation with examples can be read from:\n"
			+ "\thttp://www.dtangler.org/documentation " + "\n";
}