// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.exception.DtException;

public class ConfigFileWriter {

	private final File file;

	public ConfigFileWriter(File file) {
		this.file = file;
	}

	public void save(Arguments args) {

		BufferedWriter output = null;
		try {
			file.createNewFile();
			output = new BufferedWriter(new FileWriter(file));
			writeParameter(output, ParserConstants.INPUT_KEY, createValuesList(
					args.getInput(), ";\\\n\t"));
			writeParameter(output, ParserConstants.DEPENDENCY_ENGINE_ID_KEY,
					args.getDependencyEngineId());
			writeParameter(output, ParserConstants.SCOPE_KEY, args.getScope());
			writeParameter(output, ParserConstants.IGNORE_FILE_MASK_KEY,
					createValuesList(args.getIgnoredFileMasks(), ";\\\n\t"));
			writeParameter(output, ParserConstants.CYCLES_ALLOWED_KEY,
					createBooleanValue(args.getCyclesAllowed()));
			writeParameter(output, ParserConstants.GROUPS_KEY,
					createGroupValue(args.getGroups()));
			writeParameter(output, ParserConstants.RULES_KEY, createRuleValues(
					args.getForbiddenDependencies(), args
							.getAllowedDependencies()));
		} catch (FileNotFoundException e) {
			throw new DtException("could not create or open config file: "
					+ file.getAbsolutePath());
		} catch (IOException e) {
			throw new DtException(
					"I/O error while attempting to write to config file: "
							+ file.getAbsolutePath());
		} finally {
			try {
				if (output != null)
					output.close();
			} catch (IOException e) {
				// don't care. Nothing we can do about it
			}
		}
	}

	private String createGroupValue(Map<String, Group> groups) {
		StringBuilder sb = new StringBuilder();
		for (Group group : groups.values()) {
			if (sb.length() > 0)
				sb.append(";\\\n\t");
			addValuesList(sb, group.getName(), ParserConstants.CONTAINS, group
					.getGroupItems());
			if (group.getExcludedItems().size() > 0) {
				sb.append(" \\\n\t\t");
				sb.append(ParserConstants.DOES_NOT_CONTAIN);
				sb.append(" \\\n\t\t\t");
				sb.append(createValuesList(group.getExcludedItems(),
						",\\\n\t\t\t"));
			}
		}
		return sb.toString();
	}

	private String createRuleValues(
			Map<String, Set<String>> forbiddenDependencies,
			Map<String, Set<String>> allowedDependencies) {

		String forbiddenDeps = createMapValue(ParserConstants.CANNOT_DEPEND,
				forbiddenDependencies);
		String allowedDeps = createMapValue(ParserConstants.CAN_DEPEND,
				allowedDependencies);
		return forbiddenDeps + ";\\\n\t" + allowedDeps;
	}

	private String createMapValue(String subKey, Map<String, Set<String>> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Set<String>> entry : map.entrySet()) {
			if (sb.length() > 0)
				sb.append(";\\\n\t");
			addValuesList(sb, entry.getKey(), subKey, entry.getValue());
		}
		return sb.toString();

	}

	private void addValuesList(StringBuilder sb, String key, String subKey,
			Set<String> list) {
		sb.append(key);
		sb.append(" \\\n\t\t");
		sb.append(subKey);
		sb.append(" \\\n\t\t\t");
		sb.append(createValuesList(list, ",\\\n\t\t\t"));
	}

	private String createBooleanValue(boolean b) {
		return b ? ParserConstants.VALUE_TRUE : ParserConstants.VALUE_FALSE;
	}

	private String createValuesList(Collection<String> values, String separator) {
		StringBuilder sb = new StringBuilder();
		for (String path : values) {
			if (sb.length() > 0)
				sb.append(separator);
			sb.append(path);
		}
		return sb.toString();
	}

	private void writeParameter(BufferedWriter output, String key, String value)
			throws IOException {
		if (value == null || value.equals(""))
			return; // empty values are ignored
		output.write(key);
		output.write(" = ");
		output.write(value);
		output.write("\n\n");
	}
}
