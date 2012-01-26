// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.genericengine.dependenciesstreamparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.exception.DtException;
import org.dtangler.core.testutil.ClassPathEntryFinder;
import org.dtangler.genericengine.dependenciesstreamparser.ItemDependenciesStreamParser;
import org.dtangler.genericengine.types.Item;
import org.dtangler.genericengine.types.ValidScopes;
import org.junit.Test;

public class ItemDependenciesStreamParserTest {
	
	@Test
	public void testScope0Parser() {
		String corePath = ClassPathEntryFinder.getPathContaining("core");
		String dtPath = corePath
				+ "/org/dtangler/genericengine/dependencyengine/testdata/testParsing1.dt";
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		ValidScopes validScopes = new ValidScopes();
		Set<Item> items = parser.parse(validScopes,
				new File(dtPath), "UTF-8");
		assertEquals(10, items.size());
		List<String> listNames = getItemNames(items);
		assertTrue(listNames.containsAll(new HashSet<String>(Arrays.asList(
				"Homer", "Pizza", "Pepperoni", "Cheese", "Beer", "Bart", "Lisa", "Marge", "Coca Cola", "Onion"))));
		for (Item item : items) {
			if (item.getDisplayname().equals("Homer")) {
				assertEquals(item.getDependencies().size(), 2);
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pizza"),
								new Item("Beer")))));
			} else if (item.getDisplayname().equals("Pizza")) {
				assertEquals(item.getDependencies().size(), 2);
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pepperoni"),
								new Item("Cheese")))));
			} else if (item.getDisplayname().equals("Bart")) {
				assertEquals(item.getDependencies().size(), 2);
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pizza"),
								new Item("Coca Cola")))));
			} else if (item.getDisplayname().equals("Lisa")) {
				assertEquals(item.getDependencies().size(), 3);
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Pizza"),
								new Item("Onion"),
								new Item("Coca Cola")))));
			} else if (item.getDisplayname().equals("Marge")) {
				assertEquals(item.getDependencies().size(), 1);
				assertTrue(item.getDependencies().keySet().containsAll(
						new HashSet<Item>(Arrays.asList(new Item("Onion")))));
			} else if (item.getDisplayname().equals("Coca Cola") ||
					item.getDisplayname().equals("Onion") ||
					item.getDisplayname().equals("Beer") ||
					item.getDisplayname().equals("Pepperoni") ||
					item.getDisplayname().equals("Cheese")) {
				assertEquals(item.getDependencies().size(), 0);
			} else {
				assertTrue(false);
			}
		}
	}

	@Test
	public void testLineParserWhiteSpaces() {
		Set<String> setAbcdef = new HashSet<String>(Arrays.asList(
				"a", "b", "c", "d", "e", "f"));
		ValidScopes validScopes = new ValidScopes();
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		List<String> listNames = null;

		listNames = getItemNames(parser.parseItem(validScopes, "a b c d e f", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		listNames = getItemNames(parser.parseItem(validScopes, " { a\t   }\t   {\tb}   {c\t}\t{ d }{e} {f} ", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		listNames = getItemNames(parser.parseItem(validScopes, "\t\t\ta b c d e f\t", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		String line = " scope   {a} scope{b } scope{ c} scope{ d } scope{e}scope {f}";
		try {
			listNames = getItemNames(parser.parseItem(validScopes, line, "UTF-8"));
			assertTrue(false);
		} catch (DtException e) {
			validScopes.clearScopeNames();
			listNames = getItemNames(parser.parseItem(validScopes, line, "UTF-8"));
		}
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		listNames = getItemNames(parser.parseItem(validScopes, "scope {a}\t scope{b }scope{c} scope{ d} scope{\te\t\t}\tscope\t {f}", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		listNames = getItemNames(parser.parseItem(validScopes, "scope2 {z a}\t scope2{ z b }scope2{ x\tc} scope2{\tx\t\td\t} scope2\t{\ty\te\t\t}\tscope2\t {y f }", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

		listNames = getItemNames(parser.parseItem(validScopes, " scope3{w z a}\t scope3{ w z b }scope3{w x\tc} scope3{w \tx\t\td\t} scope3\t{w \ty\te\t\t}\tscope3\t { w y f }", "UTF-8"));
		assertTrue(listNames.containsAll(setAbcdef));
		assertTrue(listNames.size() == setAbcdef.size());

	}

	@Test
	public void testScopes() {
		ValidScopes validScopes = new ValidScopes();
		ItemDependenciesStreamParser parser = new ItemDependenciesStreamParser();
		List<Item> items = parser.parseItem(validScopes, "itemScope {a b c d e f g h i j k l m n}", "UTF-8");
		assertEquals(1, items.size());
		List<String> listNames = getItemNames(items);
		assertTrue(listNames.containsAll(new HashSet<String>(Arrays.asList("n"))));
		validScopes.generateScopeNamesForUndefinedScopeNames("scope #", "#");
		assertEquals(14, validScopes.getNumberOfScopes());
		assertEquals(14, validScopes.getScopeNames().length);
		int scopeIndex = 0;
		for (String scopeName : validScopes.getScopeNames()) {
			if (scopeIndex == validScopes.getNumberOfScopes()-1) {
				assertTrue(scopeName.equals("itemScope"));
			} else {
				assertTrue(scopeName.equals("scope "+(scopeIndex+1)));
			}
			scopeIndex++;
		}
	}

	private List<String> getItemNames(Set<Item> items) {
		List<String> names = new ArrayList<String>();
		for (Item item : items) {
			names.add(item.getDisplayname());
		}
		return names;
	}

	private List<String> getItemNames(List<Item> items) {
		List<String> names = new ArrayList<String>();
		for (Item item : items) {
			names.add(item.getDisplayname());
		}
		return names;
	}

}