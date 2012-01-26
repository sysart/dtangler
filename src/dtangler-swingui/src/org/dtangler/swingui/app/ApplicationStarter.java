//This product is provided under the terms of EPL (Eclipse Public License) 
//version 1.0.
//
//The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php

package org.dtangler.swingui.app;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.input.ArgumentBuilder;
import org.dtangler.swingui.aboutinfodisplayer.AboutInfoDisplayer;
import org.dtangler.swingui.aboutinfodisplayer.impl.AboutInfoDisplayerImpl;
import org.dtangler.swingui.directoryselector.impl.SwingDirectorySelector;
import org.dtangler.swingui.dsm.impl.DsmViewFactoryImpl;
import org.dtangler.swingui.fileinput.impl.FileInputSelectorImpl;
import org.dtangler.swingui.fileselector.FileSelector;
import org.dtangler.swingui.fileselector.impl.SwingFileSelector;
import org.dtangler.swingui.groupselector.GroupSelector;
import org.dtangler.swingui.groupselector.impl.GroupSelectorImpl;
import org.dtangler.swingui.mainview.MainViewFactory;
import org.dtangler.swingui.mainview.impl.MainViewFactoryImpl;
import org.dtangler.swingui.rulememberselector.RuleMemberSelector;
import org.dtangler.swingui.rulememberselector.impl.RuleMemberSelectorImpl;
import org.dtangler.swingui.rulesselector.RulesSelector;
import org.dtangler.swingui.rulesselector.impl.RulesSelectorImpl;
import org.dtangler.swingui.textinput.impl.TextInputSelectorImpl;
import org.dtangler.swingui.windowmanager.DialogManager;
import org.dtangler.swingui.windowmanager.WindowManager;

public class ApplicationStarter {

	private final MainViewFactory viewFactory;

	public ApplicationStarter(WindowManager windowManager,
			DialogManager dialogManager) {
		viewFactory = createMainViewFactory(windowManager, dialogManager);
	}

	private MainViewFactory createMainViewFactory(WindowManager windowManager,
			DialogManager dialogManager) {
		DependencyEngineFactory dependencyEngineFactory = new DependencyEngineFactory();
		DsmViewFactoryImpl dsmViewFactory = new DsmViewFactoryImpl();
		SwingDirectorySelector swingDirectorySelector = new SwingDirectorySelector();
		TextInputSelectorImpl textInputSelector = new TextInputSelectorImpl(
				windowManager);
		FileInputSelectorImpl fileInputSelector = new FileInputSelectorImpl(
				swingDirectorySelector, textInputSelector, windowManager, dependencyEngineFactory);
		RuleMemberSelector ruleMemberSelector = new RuleMemberSelectorImpl(
				windowManager);
		GroupSelector groupSelector = new GroupSelectorImpl(windowManager,
				textInputSelector);
		RulesSelector rulesSelector = new RulesSelectorImpl(ruleMemberSelector,
				windowManager, groupSelector);
		AboutInfoDisplayer aboutInfoDisplayer = new AboutInfoDisplayerImpl(
				windowManager);
		FileSelector fileSelector = new SwingFileSelector();
		MainViewFactory mainViewFactory = new MainViewFactoryImpl(
				dsmViewFactory, fileInputSelector, rulesSelector, fileSelector,
				windowManager, aboutInfoDisplayer, dialogManager, dependencyEngineFactory);

		return mainViewFactory;
	}

	public void start(String[] args) {
		Arguments arguments = new ArgumentBuilder().build(args);
		viewFactory.openMainView(arguments);
	}

}
