Development environment:

JDK 1.5.0 
=========
dtangler requires JRE 1.5.0 or newer. Therefore it is smart to use JDK1.5.0 in development.

Eclipse
=======
The subprojects contain Eclipse project files. 

Instructions for creating the Eclipse workspace:
  * Create a new empty workspace. Use an empty folder somewhere else where the code is.
  * File->Import->Existing projects into workspace
  * Select the dtangler main folder (i.e. the folder where this README.txt is.
  * Do NOT select "Copy projects into workspace"
  * Click 'Finish' to import the projects

Eclipse warning settings:

  Go to: Window->Preferences->Java->Compiler->Errors/Warnings

  Change these three settings:
  * Potential programming problems -> Serializable class without serialVersionUID: Ignore
  * Generic types -> Unchecked generic type operation: Ignore
  * Generic types -> Usage of a raw type: Ignore

  After changing those three settings there should be zero warnings.

All source code must be formatted and imports organized before committing 
to svn (use Eclipse default formatting settings). A zero warnings policy is in effect.


Optional: Ruby & Rake
=====================
The automated build runs on rake.
http://www.ruby-lang.org/
http://rake.rubyforge.org/

To run the automated build you need to have JAVA_HOME set.
then just invoke 'rake' in the dtangler-folder.
All build generated files go to folder '_build'.

