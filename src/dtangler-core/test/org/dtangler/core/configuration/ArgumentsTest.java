// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class ArgumentsTest {

	private Arguments args;

	@Test
	public void testForbiddenDependencies() {
		args = new Arguments();
		Map<String, Set<String>> expected = createMap("foo", "bay", "bar");
		args.setForbiddenDependencies(expected);
		assertEquals(expected, args.getForbiddenDependencies());
	}

	@Test
	public void testAllowedDepednencies() {
		args = new Arguments();
		Map<String, Set<String>> expected = createMap("foo", "bay", "bar");
		args.setAllowedDependencies(expected);
		assertEquals(expected, args.getAllowedDependencies());
	}

	@Test
	public void testInputConfiguration() {
		String path1 = "d:/temp";
		String path2 = "test/folder";
		List<String> paths = new ArrayList();
		paths.add(path1);
		paths.add(path2);

		args = new Arguments();
		args.setInput(paths);
		assertEquals(paths, args.getInput());
	}

	@Test
	public void testCyclesAllowedTrue() {
		args = new Arguments();
		args.setCyclesAllowed(true);
		assertTrue(args.getCyclesAllowed());
	}

	@Test
	public void testCyclesAllowedFalse() {
		args = new Arguments();
		args.setCyclesAllowed(false);
		assertFalse(args.getCyclesAllowed());
	}

	@Test
	public void testEqualsHashCodeAndDeepCopy() {
		Arguments a1 = new Arguments();
		Arguments a2 = new Arguments();
		Arguments b1 = new Arguments();

		a1.setInput(Arrays.asList("foo", "bar"));
		a2.setInput(Arrays.asList("foo", "bar"));
		b1.setInput(Arrays.asList("foo", "oof"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setInput(Arrays.asList("foo", "bar"));
		a1.setConfigFileName("myprops.properties");
		a2.setConfigFileName("myprops.properties");
		b1.setConfigFileName("yourprops.properties");

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setConfigFileName("myprops.properties");
		a1.setCyclesAllowed(true);
		a2.setCyclesAllowed(true);
		b1.setCyclesAllowed(false);

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setCyclesAllowed(true);
		a1.setForbiddenDependencies(createMap("foo", "bar"));
		a2.setForbiddenDependencies(createMap("foo", "bar"));
		b1.setForbiddenDependencies(createMap("foo", "oof"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setForbiddenDependencies(createMap("foo", "bar"));
		a1.setGroups(createGroup("foo", "bar"));
		a2.setGroups(createGroup("foo", "bar"));
		b1.setGroups(createGroup("foo", "oof"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setGroups(createGroup("foo", "bar"));
		a1.setGroups(createGroupWithExclusion("foo", "bar"));
		a2.setGroups(createGroupWithExclusion("foo", "bar"));
		b1.setGroups(createGroupWithExclusion("foo", "oof"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setGroups(createGroupWithExclusion("foo", "bar"));
		a1.setIgnoredFileMasks(Arrays.asList("foo", "bar"));
		a2.setIgnoredFileMasks(Arrays.asList("foo", "bar"));
		b1.setIgnoredFileMasks(Arrays.asList("foo", "oof"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setIgnoredFileMasks(Arrays.asList("foo", "bar"));
		a1.setScope("packages");
		a2.setScope("packages");
		b1.setScope("classes");

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);

		b1.setScope("packages");
		b1.setAllowedDependencies(createMap("xx", "yy", "zz"));
		a1.setAllowedDependencies(createMap("xxx", "yyy", "zzz"));
		a2.setAllowedDependencies(createMap("xxx", "yyy", "zzz"));

		assertEqualsHashCodeAndDeepCopy(a1, a2, b1);
	}

	private Map<String, Group> createGroup(String... deps) {
		Map<String, Group> result = new HashMap();
		result.put("foo", new Group("foo", new HashSet(Arrays.asList(deps))));
		return result;
	}

	private Map<String, Group> createGroupWithExclusion(String... deps) {
		Map<String, Group> result = new HashMap();
		result.put("foo", new Group("foo", Collections.EMPTY_SET, new HashSet(
				Arrays.asList(deps))));
		return result;
	}

	private Map<String, Set<String>> createMap(String key, String... values) {
		Map<String, Set<String>> result = new HashMap();
		result.put(key, new HashSet(Arrays.asList(values)));
		return result;
	}

	private void assertEqualsHashCodeAndDeepCopy(Arguments a1, Arguments a2,
			Arguments b1) {
		assertEquals(a1, a2);
		assertEquals(a1.hashCode(), a2.hashCode());

		assertFalse(a1.equals(b1));
		assertFalse(a1.hashCode() == b1.hashCode());

		assertEquals(a1, a1.createDeepCopy());
	}
}