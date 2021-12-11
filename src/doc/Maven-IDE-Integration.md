# Maven and IDE Integration
## Overcoming versioning problems with JDK and Maven

If you experience problems which are related to the versions of JDK and Maven you can achieve usage of the same versions as this project was developed on as follows:

* Make the required JDK version available on your computer: I recommend to use https://sdkman.io/ for Unix-like systems.
  Install it like there described. List the available JDKs by\
  `sdk list java`\
  For installation of the HotSpot variant of JDK 11 of provider AdoptOpenJDK use the listed identifier e.g. `11.0.11.hs-adpt` in the command\
  `sdk install java 11.0.11.hs-adpt`\
  If you do not want that it becomes the default Java version, answer in the end with `n`.
* Make that the required Java version is used by your project:
  You can do this for the current command window by\
  `sdk use java 11.0.11.hs-adpt`\
  You can do this permanently by\
  `sdk default java 11.0.11.hs-adpt`\
  Or you define the environment variable `JAVA_HOME` as the path of your JDK installation, on my computer it is e.g.\
  `/home/knabe/.sdkman/candidates/java/11.0.11.hs-adpt`\
  How to define environment variables and for which lifetime depends on your operating system and is beyond the scope of this introduction.
  If you want to use JDK 11 only for this project you can define a script which defines JAVA_HOME and then runs Maven, e.g. on Linux: File `mymvn.sh` with content\
  `JAVA_HOME=/home/knabe/.sdkman/candidates/java/11.0.11.hs-adpt ./mvnw "$@"`\
  Then\
  `./mymvn.sh clean test package`\
  This runs the Maven Wrapper of the project with the given JDK.
  I recommend to .gitignore this script, as other developers may have their JDK at another location.

## IDE Integration

In order to develop, test, or study this project the usage of a Java IDE is recommended.

The [Spring Tools](https://spring.io/tools) may be preferred, as it is Eclipse configured by the Spring authors.

[IntelliJ IDEA](https://www.jetbrains.com/idea/) works equally well now that we no longer rely on AspectJ.

For using this project with **Java 11** as of 2021-12-07 the last Spring Tools are 4.12.1 according to [Spring Tools 4](https://spring.io/tools). Download spring-tool-suite-4-4.12.1.RELEASE-e4.21.0 for your platform, unpack it, and run the program `SpringToolSuite4` or `SpringToolSuite4.exe` contained in the sts-... folder. You may run the STS on Java 11 or later, but the test suite or generated program has to be run on a Java 11 JVM.

### Import into the Spring Tools
Click File > Import > Maven > Existing Maven Project. The dialog "Import Maven Projects" appears. Select as Root Directory: .../spring-ddd-bank. It should detect the contained `pom.xml`

### Run the REST Server
Navigate to file `src/main/java/.../Application.java`. By MouseRight choose "Run as Java Application".

For the current version of Spring DDD Bank it is important, that it is executed on a Java 11 VM. Please check in the title line of the Console window. 
If you have to correct, go into the dialog Window > Preferences. In the left-hand tree choose Java > Installed JREs. 
If a Java 11 VM is not listed, click Add..., select Installed JRE Type as Standard VM and select a Directory... of an installed JVM 11. 

The JVM 11 does not have to become the default JVM for new projects, but has to be the JVM on which the Application and the test suite are executed in the Spring Tools.

