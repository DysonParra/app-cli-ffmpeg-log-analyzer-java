# Repository naming
To get information of how this user is naming the repositories go [here](https://github.com/DysonParra#repository-naming)

# Information of the project.
This repository is responsible of do specific tasks.

# Binary and library
The operation is equal for all java repositories of this user.  
Use the command: gradlew build  
It will generate four files where project-name is the name of the repository:
- project-name-bin.jar  
  The fat jar file that include all dependencies into the same file and probably more bigger.
- project-name-bin-min.jar  
  The default jar file that only include the source code of the repository.
- project-name-lib-fat.jar  
  The fat jar that include all dependencies, but exclude the file "Application.java" (the main class) and the source files in the package "com.project.dev.tester", so you can create files in that package for tests and will not be included in the library (basically a lib is a jar withouth main class)
- project-name-lib-min.jar  
The same that the fat lib, but not include dependencies and is possibly smaller size.  
Useful if you use third party libraries in the library that you are creating. For example the library that you are creating use selenium, if you use the fat lib it is probably very big size, so in this case you can use this lib, and in the project that uses this lib import all selenium dependencies.
For use a lib simply copy the file in the folder libs in the root folder of a project.  
