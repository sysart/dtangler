// This product is provided under the terms of EPL (Eclipse Public License) 
// version 1.0.
//
// The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 

package org.dtangler.swingui.rulememberselector.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;

import org.dtangler.core.exception.DtException;
import org.dtangler.swingui.windowmanager.MockWindowManager;
import org.junit.Before;
import org.junit.Test;

public class RuleMemberSelectorFeatureTest {

	private MockWindowManager windowManager;

	@Before
	public void setUp() {
		windowManager = new MockWindowManager();
	}

	private RuleMemberViewDriver openView(List<String> groupNames) {
		new RuleMemberSelectorImpl(windowManager).selectRuleMember(groupNames);
		return new RuleMemberViewDriver(windowManager.getLastShownView());

	}

	@Test
	public void testMemberTypeSelection() {
		RuleMemberViewDriver view = openView(Arrays.asList("foo", "bar", "bay"));
		assertTrue(view.literalRadio.isSelected().isTrue());
		assertTrue(view.literal.isEnabled().isTrue());
		assertFalse(view.groupRadio.isSelected().isTrue());
		assertFalse(view.groups.isEnabled().isTrue());

		view.groupRadio.click();

		assertFalse(view.literalRadio.isSelected().isTrue());
		assertFalse(view.literal.isEnabled().isTrue());
		assertTrue(view.groupRadio.isSelected().isTrue());
		assertTrue(view.groups.isEnabled().isTrue());
	}

	@Test
	public void testGroupNames() {
		RuleMemberViewDriver view = openView(Arrays.asList("foo", "bar", "bay"));
		assertTrue(view.groups.contains(new String[] { "foo", "bar", "bay" })
				.isTrue());
	}

	@Test
	public void testOkisEnabledOnlyWhenValidMemberIsDefined() {
		RuleMemberViewDriver view = openView(Arrays.asList("foo", "bar", "bay"));

		assertTrue(view.literalRadio.isSelected().isTrue());
		assertEquals("", view.literal.getText());

		assertFalse("literal input is empty", view.okButton.isEnabled()
				.isTrue());

		view.literal.setText("x");
		assertTrue(view.okButton.isEnabled().isTrue());

		view.groupRadio.click();
		JComboBox cbx = (JComboBox) view.groups.getAwtComponent();
		cbx.setSelectedIndex(-1);

		assertFalse("group selectiob is empty", view.okButton.isEnabled()
				.isTrue());

		view.groups.select("bar");
		assertTrue(view.okButton.isEnabled().isTrue());
	}

	@Test
	public void testCancel() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				RuleMemberViewDriver view = new RuleMemberViewDriver(
						windowManager.getLastShownView());
				view.literal.setText("myvalue");
				view.cancelButton.click();
			}
		});

		String result = new RuleMemberSelectorImpl(windowManager)
				.selectRuleMember(Collections.EMPTY_LIST);
		assertNull(result);
		assertNull(windowManager.getLastShownView());
	}

	@Test
	public void testSelectLiteral() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				RuleMemberViewDriver view = new RuleMemberViewDriver(
						windowManager.getLastShownView());
				view.literal.setText("myvalue");
				view.okButton.click();
			}
		});

		String result = new RuleMemberSelectorImpl(windowManager)
				.selectRuleMember(Collections.EMPTY_LIST);
		assertEquals("myvalue", result);
		assertNull(windowManager.getLastShownView());
	}

	@Test
	public void testSelectGroup() {
		windowManager.setTestCodeForNextModal(new Runnable() {
			public void run() {
				RuleMemberViewDriver view = new RuleMemberViewDriver(
						windowManager.getLastShownView());
				view.groupRadio.click();
				view.groups.select("bar");
				view.okButton.click();
			}
		});

		String result = new RuleMemberSelectorImpl(windowManager)
				.selectRuleMember(Arrays.asList("foo", "bar", "bay"));
		assertEquals("@bar", result);
		assertNull(windowManager.getLastShownView());
	}

	@Test
	public void testLiteralCannotContainGroupSign() {
		RuleMemberViewDriver view = openView(Collections.EMPTY_LIST);
		view.literal.setText("@foo");
		try {
			view.okButton.click();
			fail("did not throw");
		} catch (DtException e) {
			// Ok
		}
	}

	@Test
	public void testGroupRadioAndGroupAreDisabledWhenNoGroups() {
		RuleMemberViewDriver view = openView(Collections.EMPTY_LIST);

		assertFalse(view.groupRadio.isEnabled().isTrue());
		assertFalse(view.groups.isEnabled().isTrue());
	}

}
