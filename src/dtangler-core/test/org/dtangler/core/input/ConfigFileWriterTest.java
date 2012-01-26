//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.core.input;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.configuration.Group;
import org.dtangler.core.configuration.ParserConstants;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.junit.Test;

public class ConfigFileWriterTest {
	String fileName = ClassPathEntryFinder.getPathContaining("core")
			+ "/test.properties";

	@Test
	public void testWriteAndRead() {
		Arguments writtenArgs = write(createArgs(), fileName);
		Arguments readArgs = read(fileName);

		assertEquals(2, writtenArgs.getInput().size());
		assertEquals(writtenArgs.getInput().get(0), readArgs.getInput().get(0));
		assertEquals(writtenArgs.getInput().get(1), readArgs.getInput().get(1));
		assertEquals(2, writtenArgs.getIgnoredFileMasks().size());
		assertEquals(3, writtenArgs.getGroups().size());
		assertEquals("classes", writtenArgs.getScope());
		assertEquals(writtenArgs.getAllowedDependencies(), readArgs
				.getAllowedDependencies());
		assertEquals(writtenArgs.getForbiddenDependencies(), readArgs
				.getForbiddenDependencies());

		assertEquals(writtenArgs, readArgs);
	}

	@Test
	public void testEmpty() {
		Arguments args = write(new Arguments(), fileName);
		Arguments readArgs = read(fileName);

		assertEquals(0, args.getInput().size());
		assertEquals(0, args.getIgnoredFileMasks().size());
		assertEquals(0, args.getGroups().size());
		assertNull(args.getScope());
		assertEquals(args, readArgs);
	}

	private Arguments createArgs() {
		Arguments args = new Arguments();

		args.setInput(Arrays.asList("foo", "bar"));
		args.setIgnoredFileMasks(Arrays.asList("foo*", "*bar*"));
		args.setCyclesAllowed(true);

		Map<String, Set<String>> cannotDepend = new HashMap();
		cannotDepend
				.put("abc", new HashSet(Arrays.asList("def", "ghi", "ijk")));
		cannotDepend.put("def", new HashSet(Arrays.asList("xyz")));
		args.setForbiddenDependencies(cannotDepend);

		Map<String, Set<String>> canDepend = new HashMap();
		canDepend.put("xxx", new HashSet(Arrays.asList("yyy", "zzz")));
		canDepend.put("afl", new HashSet(Arrays.asList("eif")));
		args.setAllowedDependencies(canDepend);

		Map<String, Group> groups = new HashMap();
		createGroup(groups, "A", "a.a", "a.b", "a.c");
		createGroup(groups, "Group B", "b.a", "b.b.*", "b.c");
		groups.put("exclusionGroup", new Group("exclusionGroup", new HashSet(
				Arrays.asList("xx", "yy")), new HashSet(Arrays.asList("xx",
				"yy"))));
		args.setGroups(groups);
		args.setScope("classes");

		return args;
	}

	private Arguments read(String fileName) {
		return new ArgumentParser().parseArguments(new ConfigFileParser(
				new File(fileName), ParserConstants.VALID_KEYS).parseValues());
	}

	private Arguments write(Arguments args, String fileName) {
		new ConfigFileWriter(new File(fileName)).save(args);
		return args;
	}

	private void createGroup(Map<String, Group> groups, String name,
			String... items) {
		Set<String> groupItems = new HashSet();
		for (String item : items)
			groupItems.add(item);
		groups.put(name, new Group(name, groupItems));
	}
}
