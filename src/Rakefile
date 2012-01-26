#####################################################################################################
# Rakefile for dtangler. (www.dtangler.org)
# 
# This product is provided under the terms of EPL (Eclipse Public License) 
# version 1.0.
#
# The full license text can be read from: http://www.eclipse.org/org/documents/epl-v10.php 
#
#
# Usage: 
#  - required: JAVA_HOME environment variable must be set to the JDK to build dtangler with
#  - optional: BUILDDIR environment variable controls where the build is being performed
#               the default is '_build'
#  - optional: RELEASEDIR environment variable controls where the build results are published.
#               the default is '_release'

#####################################################################################################
            
require "Rake_incremental_java_build.rb"

RELEASE_FOLDER = env_or_default("RELEASEDIR", "_release")



#####################################################################################################
# Create a DSM text file with dtangler
#####################################################################################################
def create_dsm(core_jar, input, outputFile)
	puts "Running dtangler on #{input}"
  java_cmd_line = "#{JAVABIN}/java -jar #{core_jar} -input=#{input} > #{outputFile}"
 	sh java_cmd_line do |ok, res|
    if(!ok)
       raise "DEPENDENCY CHECK FAILED: There are cycles or illegal dependencies. See #{outputFile}."
    end
  end
end


#####################################################################################################
# Define subprojects and their dependencies
#####################################################################################################

  forms = Library.new "lib-forms"
  junit = Library.new "lib-junit-4.4"
  uispec4j = Library.new "lib-uispec4j"
  bumblebee = Library.new "lib-bumblebee"

  core = Project.new "dtangler-core"
  core.tests_depend_on junit
  
  core.test.depends_on CompilationUnit.new('dtangler-core', 'testdata-good-deps')
  core.test.depends_on CompilationUnit.new('dtangler-core', 'testdata-cyclic-deps')
  core.tests_depend_on bumblebee

  ui = Project.new "dtangler-ui"
  ui.depends_on core
  ui.tests_depend_on junit

  swingui = Project.new "dtangler-swingui"
  swingui.depends_on ui
  swingui.depends_on forms
  swingui.tests_depend_on uispec4j
  swingui.tests_depend_on junit
  swingui.tests_depend_on bumblebee 
  swingui.tests_depend_on bumblebee 

  testsuites = Project.new "dtangler-testsuites"
  testsuites.test.depends_on core.test
  testsuites.test.depends_on ui.test
  testsuites.test.depends_on swingui.test
  testsuites.tests_depend_on bumblebee
  testsuites.junit_cmd = "-Ddtangler-root=. org.junit.runner.JUnitCore org.dtangler.testsuites.docsuites.Dtangler"


  # Define the files for release
  core_jar = "#{RELEASE_FOLDER}/dtangler-core.jar"
  gui_jar = "#{RELEASE_FOLDER}/dtangler-gui.jar"
  core_dsm = "#{RELEASE_FOLDER}/dtangler-core-dsm.txt"
  gui_dsm = "#{RELEASE_FOLDER}/dtangler-gui-dsm.txt"

  documentation_success = "#{BUILD_FOLDER}/documentation_success"

  # Publish rake targets
  Buildable.init_rake_tasks

  file core_jar => [core.test_success] do
    create_jar(core_jar, "dtangler-core/MANIFEST.MF", core.prod.classes_folder)
  end

  file gui_jar => [core.test_success, ui.test_success, swingui.test_success] do
    create_jar(gui_jar, "dtangler-swingui/MANIFEST.MF", [core.prod.classes_folder, ui.prod.classes_folder, swingui.prod.classes_folder])
  end

  # Documentation is generated while running the tests in the testsuites-project
  task :documentation => [testsuites.test_success]

  file core_dsm => [core_jar] do
		create_dsm(core_jar, core_jar, core_dsm)
  end

  file gui_dsm => [core_jar, gui_jar] do
		create_dsm(core_jar, gui_jar, gui_dsm)
  end

  task :default => [core_jar, gui_jar, core_dsm, gui_dsm, :documentation]


