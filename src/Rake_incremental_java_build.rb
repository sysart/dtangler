#####################################################################################################
# An incremental java build systme using Rake.
# See dtangler Rakefile for usage example.
#####################################################################################################

require "ftools"

def env_or_default(env_var, default_value)
	result = ENV[env_var]
	result = default_value if result.nil?
  raise "#{env_var} not set" if result.nil?
	puts "#{env_var} = #{result}"
	return result
end

# no default, JAVA_HOME must be set
JAVA_HOME = env_or_default("JAVA_HOME", nil)	
JAVABIN = "#{JAVA_HOME}/bin"

BUILD_FOLDER = env_or_default("BUILDDIR", "_build")

# Windowland uses ';' Unixland uses ':', workaround:
PATH_SEPARATOR = (File.exist?('C:\\') ? ';' : ':');


#####################################################################################################
# Destroy a dir and then create it again (empty of course).
# Creates also any parent dirs as needed
#####################################################################################################
def recreate_dir(dir)
  # puts "Creating dir #{dir}"
  rm_rf dir
  mkdir_p dir
end


#####################################################################################################
# Compiles all java files in the src folder to the classes folder. Also copies all non java files
# from src_folder to classes_folder
#####################################################################################################
def compile_java(src_folder, classes_folder, class_path)
  puts "Compiling java. src: #{src_folder} output: #{classes_folder}"
  recreate_dir(classes_folder)
  src_files = FileList.new("#{src_folder}/**/*.java").to_s

  compile_class_path = class_path.join(PATH_SEPARATOR)
  cp_arg = ''
  cp_arg = "-cp #{compile_class_path}" if(class_path.size > 0)

  options = '-Xlint:-unchecked'
  
  FileList["#{src_folder}/**/*"].exclude("#{src_folder}/**/*.java").each {|source|
    dest = source.sub(src_folder,classes_folder)
    File.mkpath File.dirname(dest)
    File.cp(source, dest) unless File.directory?(source)
  }  
  
  javac_cmd_line = "#{JAVABIN}/javac #{options} -sourcepath #{src_folder} #{cp_arg} -d #{classes_folder} #{src_files}"
  sh javac_cmd_line do |ok, res|
      if(!ok)
         rm_rf classes_folder
         exit 1
      end
  end

end

#####################################################################################################
# Creates jar file from given folder
#####################################################################################################
def create_jar(abs_jarfile, manifest, folders)
  rm_rf abs_jarfile
  File.mkpath File.dirname(abs_jarfile)
  jar_action = "cmf #{manifest}"
  folders.each do |folder|
    cmd = "#{JAVABIN}/jar #{jar_action} #{abs_jarfile} -C #{folder} org"
    puts "jarring with : '#{cmd}'"
    sh cmd do |ok, res|
        if(!ok)
           rm_rf abs_jarfile
           exit 1
        end
    end
    jar_action = 'uf'
  end
end

#####################################################################################################
# FIXME: remove dtangler dependency to make this script a general purpose java build thingy
#####################################################################################################
def run_junit(project_name, classpath, successfile, junit_cmd)
  test_class_path = classpath.join(PATH_SEPARATOR)

  puts "Running junit tests for #{project_name}"

  java_cmd_line = "#{JAVABIN}/java -cp #{test_class_path} #{junit_cmd}"
  sh java_cmd_line do |ok, res|
    if(ok)
      touch successfile
    else
      rm_rf successfile
      exit 1
    end
  end
end

#####################################################################################################
# Type definitions
#####################################################################################################
class Buildable
  def initialize(name)
    @name = name
  end

  def Buildable.init_rake_tasks
    puts "Creating rake tasks for all Buildables"
    ObjectSpace.each_object(Buildable) { |p| p.create_rake_tasks }
  end
end


class Library < Buildable
  def create_rake_tasks
    puts "Creating rake tasks for JavaLibrary #{@name}"
  end

  def classpath
    # return paths to jars as an array
    FileList.new("#{@name}/*.jar").to_a
  end

  def prod
    self  # we know how to answer to 'classpath'
  end

  def test
    self  # we know how to answer to 'classpath'
  end

  def testutil
    self  # we know how to answer to 'classpath'
  end

end

#
# A compilation unit has a name like: 'src', 'testutil' or 'test'
#
class CompilationUnit < Buildable

  def initialize(project_path, name)
    super("#{project_path}-#{name}")
    @src_folder = "#{project_path}/#{name}"
    @classes_folder = "#{BUILD_FOLDER}/#{project_path}/classes_#{name}"
    @deps = Array.new
  end

  def depends_on(d)
    @deps << d unless d == nil
  end

  def classes_folder
      @classes_folder
  end

  def classpath
    cp = [classes_folder].concat deps_classpath
    return cp.flatten
  end

  def deps_classpath
    return @deps.collect {|d| d.classpath }.flatten
  end

  def classes_folder_deps
    d = FileList.new("#{@src_folder}/**/*").to_a
    d.concat deps_classpath
    return d
  end

  def create_rake_tasks
    if FileTest::exists?(@src_folder) 
	    # puts "Creating rake tasks for #{@src_folder}"
	    file classes_folder => classes_folder_deps do
	      compile_java(@src_folder, @classes_folder, deps_classpath)
	    end
     else
	    # puts "Creating empty rake task for #{@src_folder}"
	    file classes_folder => classes_folder_deps do
	    end
     end
  end

end

#
# A java project may have:
#   production compilation unit:       'src'           <-- 'classes-src'
#   test utilities compilation unit:   'testutil'      <-- 'classes-testutil'
#   tests compilation unit:            'test'          <-- 'classes-test'
#
class Project < Buildable

  attr_accessor :prod, :testutil, :test
  attr_writer :junit_cmd

  def initialize(name)
    super(name)
    @prod = CompilationUnit.new(@name, 'src')
    @testutil = CompilationUnit.new(@name, 'testutil')
    @test = CompilationUnit.new(@name, 'test')
    @testutil.depends_on(@prod)
    @test.depends_on(@testutil)
    @junit_cmd = build_default_junit_cmd(name)
  end

  def build_default_junit_cmd(name)
    project_id = name.split('-')[1]
    testclassprefix = "org.dtangler.#{project_id}"
	  return "-DclasspathSuitePrefix=#{testclassprefix} org.junit.runner.JUnitCore org.dtangler.testcollectorrunner.ClassPathTestSuite"  
  end

  def depends_on(other_project)
    @prod.depends_on(other_project.prod)
    @testutil.depends_on(other_project.testutil) # utils may build upon others utils
  end

  def tests_depend_on(other_project)
    @test.depends_on(other_project.test)
    @testutil.depends_on(other_project.testutil) # utils may build upon others utils
  end

  def jar
    "#{BUILD_FOLDER}/#{@name}/#{@name}.jar"
  end

  def test_success
    "#{BUILD_FOLDER}/#{@name}/test_success"
  end
  
  def create_rake_tasks
    puts "Creating rake tasks for JavaProject #{@name}"

    file test_success => @test.classes_folder do
      run_junit(@name, @test.classpath, test_success, @junit_cmd)
    end

    @prod.create_rake_tasks
    @testutil.create_rake_tasks
    @test.create_rake_tasks
  end

end

