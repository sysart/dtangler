// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.core.acceptancetests.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.testutil.dependenciesbuilder.DependencyGraphBuilder;
import org.dtangler.core.testutil.dsmdriver.DsmDriver;
import org.junit.Before;
import org.junit.Test;

public class DsmAcceptanceTest {

	private DependencyGraphBuilder builder;

	@Before
	public void setUp() {
		builder = new DependencyGraphBuilder();
	}

	private DsmDriver createDsm() {
		DependencyGraph dependencies = builder.getDependencies();
		Dsm dsm = new DsmEngine(dependencies).createDsm();
		return new DsmDriver(dsm);
	}

	@Test
	public void testEmptyDsm() {
		DsmDriver dsm = createDsm();
		assertTrue(dsm.getRowNames().isEmpty());
	}

	@Test
	public void testDsmWithNoDependencies() {
		builder.add("foo");
		builder.add("bar");
		DsmDriver dsm = createDsm();
		assertTrue(dsm.getRowNames().contains("foo"));
		assertTrue(dsm.getRowNames().contains("bar"));
		assertEquals(0, dsm.row("foo").col("bar").getWeight());
		assertEquals(0, dsm.row("bar").col("foo").getWeight());
	}

	@Test
	public void testDsmWithGoodDependencies() {
		builder.add("serviceimpl").dependsOn("dao").and("daoimpl", 2)
				.dependsOn("dao");

		DsmDriver dsm = createDsm();
		assertEquals(Arrays.asList("serviceimpl", "daoimpl", "dao"), dsm
				.getRowNames());

		assertEquals(0, dsm.row("serviceimpl").col("daoimpl").getWeight());
		assertEquals(0, dsm.row("serviceimpl").col("dao").getWeight());

		assertEquals(2, dsm.row("daoimpl").col("serviceimpl").getWeight());
		assertEquals(0, dsm.row("daoimpl").col("dao").getWeight());

		assertEquals(1, dsm.row("dao").col("serviceimpl").getWeight());
		assertEquals(1, dsm.row("dao").col("daoimpl").getWeight());
	}

	@Test
	public void testDsmWithOuterDependencies() {
		builder.add("foo").dependsOn("bar");
		builder.addOuterDependant("externalFooUser", "foo");
		builder.addOuterDependee("foo", "externalUsedByFoo");

		DsmDriver dsm = createDsm();

		assertEquals(Arrays.asList("foo", "bar"), dsm.getRowNames());
		assertEquals(1, dsm.row("bar").col("foo").getWeight());
		assertEquals(0, dsm.row("foo").col("bar").getWeight());
	}

}
