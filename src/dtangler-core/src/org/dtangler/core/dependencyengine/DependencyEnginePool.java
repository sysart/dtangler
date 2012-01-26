package org.dtangler.core.dependencyengine;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.exception.DtException;
import org.dtangler.core.filefinder.RecursiveFileFinder;
import org.dtangler.core.input.ConfigFileParser;

public class DependencyEnginePool {

	private final List<DependencyEngine> dependencyEngines = new ArrayList<DependencyEngine>();

	private final String packagenamePrefix = "org/dtangler";
	private final String dependencyEngineConfigFilePrefix = "dependency-engine-";
	private final String dependencyEngineConfigFileRegex = ".*"
			+ dependencyEngineConfigFilePrefix + ".*\\.properties$";
	private final String dependencyEnginePoolConfigFileRegex = ".*"
			+ dependencyEngineConfigFilePrefix + "pool\\.properties$";
	private String defaultDependencyEngineId;

	public DependencyEnginePool() {
		initPool();
	}

	public DependencyEnginePool(DependencyEngine... dependencyEngines) {
		if (dependencyEngines != null) {
			for (DependencyEngine dependencyEngine : dependencyEngines) {
				if (dependencyEngine.getDependencyEngineId() == null)
					dependencyEngine.setDependencyEngineId(dependencyEngine.getClass().getSimpleName());
				add(dependencyEngine.getDependencyEngineId(), dependencyEngine);
			}
		}
	}

	private DependencyEngine getDependencyEngine(String id) {
		for (DependencyEngine dependencyEngine : dependencyEngines) {
			if (dependencyEngine == null
					|| dependencyEngine.getDependencyEngineId() == null)
				continue;
			if (dependencyEngine.getDependencyEngineId().equalsIgnoreCase(id))
				return dependencyEngine;
		}
		return null;
	}

	private void addDependencyEngine(DependencyEngine dependencyEngine) {
		if (dependencyEngine != null)
			dependencyEngines.add(dependencyEngine);
	}

	public synchronized void add(String id, DependencyEngine dependencyEngine) {
		if (id == null || id.length() == 0) {
			throw new DtException(
					"Unable to load the dependency engine. Id not specified.");
		}
		if (getDependencyEngine(id) != null) {
			throw new DtException(
					"Unable to load the dependency engine. Dependency engine with id="
							+ id + " already exists.");
		}
		dependencyEngine.setDependencyEngineId(id);
		addDependencyEngine(dependencyEngine);
	}

	private DependencyEngine findEngine(Arguments arguments) {
		DependencyEngine engineMaybe = null;
		boolean ambiguousSituation = false;
		for (DependencyEngine dependencyEngine : dependencyEngines) {
			if (dependencyEngine == null)
				continue;
			if (dependencyEngine.getArgumentsMatchThisEngine(arguments) == DependencyEngine.ArgumentsMatch.yes)
				return dependencyEngine;
			if (dependencyEngine.getArgumentsMatchThisEngine(arguments) == DependencyEngine.ArgumentsMatch.maybe) {
				if (engineMaybe == null) {
					engineMaybe = dependencyEngine;
				} else {
					ambiguousSituation = true;
				}
			}
		}
		if (!ambiguousSituation && engineMaybe != null) {
			return engineMaybe;
		}
		throw new DtException(
				"unable to determine the dependency engine to be utilized");
	}

	public synchronized DependencyEngine get(Arguments arguments) {
		DependencyEngine dependencyEngine = null;
		try {
			dependencyEngine = findEngine(arguments);
		} catch (DtException ex) {
			dependencyEngine = getDefaultEngine();
		}
		if (arguments.getDependencyEngineId() != null
				&& arguments.getDependencyEngineId().length() > 0) {
			if (!(dependencyEngine != null
					&& dependencyEngine.getDependencyEngineId() != null && dependencyEngine
					.getDependencyEngineId().equalsIgnoreCase(
							arguments.getDependencyEngineId()))) {
				throw new DtException(
						"unable to find a dependency engine with id: "
								+ arguments.getDependencyEngineId());
			}
		};
		return dependencyEngine;
	}

	public DependencyEngine getDefaultEngine() {
		if (defaultDependencyEngineId == null)
			throw new DtException(
					"unable to find the default dependency engine id");
		return get(defaultDependencyEngineId);
	}

	public synchronized DependencyEngine get(String id) {
		DependencyEngine engine = getDependencyEngine(id);
		if (engine == null)
			throw new DtException(
					"unable to find a dependency engine with id: " + id);
		return engine;
	}

	public synchronized List<String> getDependencyEngineIds() {
		List<String> list = new ArrayList<String>();
		for (DependencyEngine dependencyEngine : dependencyEngines) {
			if (dependencyEngine == null
					|| dependencyEngine.getDependencyEngineId() == null)
				continue;
			list.add(new String(dependencyEngine.getDependencyEngineId()));
		}
		return list;
	}

	private class FileNameDependencyEngineConfig implements FileFilter {
		public boolean accept(File file) {
			boolean match = false;
			if (file.isFile()
					&& file.getName().matches(dependencyEngineConfigFileRegex)) {
				match = true;
			}
			return match;
		}
	}

	private InputStream getFileAsInputStream(String fileName) {
		if (isJarFileName(fileName)) {
			try {
				JarURLConnection jarURLConnection = openJarURLConnection(fileName);
				JarFile jarFile = jarURLConnection.getJarFile();
				return jarFile.getInputStream(jarURLConnection.getJarEntry());
			} catch (DtException e) {
				throw new RuntimeException("Cannot open file: " + fileName + ": "
						+ e.getCause());
			} catch (IOException e) {
				throw new RuntimeException("Cannot open file: " + fileName + ": "
						+ e.getCause());
			}
		} else {
			try {
				File file = new File(fileName);
				return new FileInputStream(file);
			} catch (IOException e) {
				throw new RuntimeException("Cannot open file: " + fileName
						+ e.getCause());
			}
		}
	}

	private void loadDependencyEngine(InputStream inputStream) {
		Map<String, String> configFileValues = new ConfigFileParser(
				inputStream, DependencyEngineConfigConstants.VALID_KEYS)
				.parseValues();
		String id = configFileValues
				.get(DependencyEngineConfigConstants.ID_DEPENDENCY_ENGINE_KEY);
		String dependencyEngineClassPath = configFileValues
				.get(DependencyEngineConfigConstants.CLASS_NAME_DEPENDENCY_ENGINE_KEY);
		Object dependencyEngine = null;
		try {
			Class<?> c = Class.forName(dependencyEngineClassPath);
			dependencyEngine = c.newInstance();
		} catch (Exception e) {
			throw new DtException(
					"unable to instantiate dependency engine id = " + id
							+ ", class name = " + dependencyEngineClassPath);
		}
		if (dependencyEngine instanceof DependencyEngine) {
			add(id, (DependencyEngine) dependencyEngine);
		} else {
			throw new DtException("invalid dependency engine class: id = " + id
					+ ", class name = " + dependencyEngineClassPath
					+ ". Dependency engine must implement the interface "
					+ DependencyEngine.class.getName());
		}
	}

	private void loadDependencyEnginePoolSettings(InputStream inputStream) {
		Map<String, String> configFileValues = new ConfigFileParser(
				inputStream, DependencyEnginePoolConfigConstants.VALID_KEYS)
				.parseValues();
		defaultDependencyEngineId = configFileValues
				.get(DependencyEnginePoolConfigConstants.ID_DEFAULT_DEPENDENCY_ENGINE_KEY);
		if (defaultDependencyEngineId == null)
			throw new DtException(
					"unable to determine the default dependency engine id");
	}

	private void initPool() {
		List<String> listDependencyEngineConfigFilenames = searchDependencyEngineConfigFiles(
				packagenamePrefix, DependencyEnginePool.class);
		for (String configFileName : listDependencyEngineConfigFilenames) {
			if (configFileName == null)
				continue;
			InputStream inputStream = getFileAsInputStream(configFileName);
			if (configFileName.matches(dependencyEnginePoolConfigFileRegex)) {
				loadDependencyEnginePoolSettings(inputStream);
			} else {
				loadDependencyEngine(inputStream);
			}
		}
		if (dependencyEngines.isEmpty()) {
			throw new RuntimeException("error: no dependency engine found");
		}
	}

	private String getFileName(String fileName) {
		if (fileName == null)
			return null;
		try {
			fileName = URLDecoder.decode(fileName, "UTF-8");
			if (fileName.indexOf('!') != -1)
				fileName = fileName.substring(0, fileName.indexOf('!'));
		} catch (Exception e) {
		}
		if (fileName.length() > 1 && fileName.matches("/.:/.*"))
			return fileName.substring(1);
		if (fileName.length() > "file:/".length()
				&& fileName.matches("file:/.:/.*"))
			return fileName.substring("file:/".length());
		if (fileName.length() > "file:".length()
				&& fileName.matches("file:/.*"))
			return fileName.substring("file:".length());

		return fileName;
	}

	private boolean isJarFileName(String fileName) {
		if (fileName == null)
			throw new DtException("invalid file name: null");
		return fileName.lastIndexOf("!") >= 0 ? true : false;
	}

	private String getJarFileEntryName(String fileName) {
		if (fileName == null)
			throw new DtException("invalid jar file name: null");
		int exclPos = fileName.lastIndexOf("!");
		if (exclPos >= 0 && exclPos < fileName.length() - 1) {
			return fileName.substring(exclPos + 2);
		}
		throw new DtException("file " + fileName + " is not a jar file");
	}

	private List<String> searchDependencyEngineConfigFiles(File file) {
		List<String> fileNames = new ArrayList<String>();
		if (file == null || file.getAbsolutePath() == null)
			return fileNames;
		if (file.isDirectory()) {
			RecursiveFileFinder fileFinder = new RecursiveFileFinder();
			fileFinder.setFilter(new FileNameDependencyEngineConfig());
			fileFinder.findFiles(file.getPath());
			for (File f : fileFinder.getFiles()) {
				if (f == null || f.getAbsolutePath() == null)
					continue;
				fileNames.add(f.getAbsolutePath());
			}
		} else {
			if (new FileNameDependencyEngineConfig().accept(file)) {
				fileNames.add(file.getAbsolutePath());
			}
		}
		return fileNames;
	}

	private List<String> searchDependencyEngineConfigFiles(
			String prefixPackageName, Class<?> contentLoaderClass) {
		List<String> fileNames = new ArrayList<String>();
		Enumeration<?> enumFiles = findConfigFileUrls(prefixPackageName,
				contentLoaderClass);
		while (enumFiles != null && enumFiles.hasMoreElements()) {
			URL url = (URL) enumFiles.nextElement();
			if (url == null || url.getFile() == null
					|| url.getFile().length() <= 1)
				continue;
			String fileName = getFileName(url.getPath());
			if (url.getProtocol() != null
					&& url.getProtocol().equalsIgnoreCase("jar")) {
				String elementInJar = getJarFileEntryName(url.getPath());
				JarFile jarFile = openJar(url);
				List<JarEntry> entries = Collections.list(jarFile.entries());
				for (JarEntry entry : entries) {
					String entryName = entry.getName();
					if (entryName.startsWith(elementInJar)) {
						if (!entry.isDirectory()
								&& entryName
										.matches(dependencyEngineConfigFileRegex)) {
							fileNames.add(fileName + "!" + "/"
									+ entry.toString());
						}
					}
				}
			} else {
				List<String> list = searchDependencyEngineConfigFiles(new File(
						fileName));
				if (list != null && list.size() > 0) {
					fileNames.addAll(list);
				}
			}
		}
		return fileNames;
	}

	private JarFile openJar(URL url) {
		try {
			JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
			return jarURLConnection.getJarFile();
		} catch (IOException e) {
			throw new RuntimeException("Cannot open jar file: " + url.getFile(), e);
		}
	}

	private JarURLConnection openJarURLConnection(String fileName) {
		try {
			URL url = null;
			if (fileName.toLowerCase().startsWith("http")) {
				url = new URL("jar:"+fileName);
			} else {
				if (fileName.startsWith("/"))
					fileName = fileName.substring(1);
				url = new URL("jar:file:/"+fileName);
			}
			return (JarURLConnection)url.openConnection();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Cannot open jar file: " + fileName, e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open jar file: " + fileName, e);
		}
	}

	private Enumeration<URL> findConfigFileUrls(String prefixPackageName,
			Class<?> contentLoaderClass) {
		try {
			return contentLoaderClass.getClassLoader().getResources(
					prefixPackageName);
		} catch (IOException e) {
			throw new RuntimeException(
					"Cannot find config file URLs starting with prefix:"
							+ prefixPackageName, e);
		}
	}

}
