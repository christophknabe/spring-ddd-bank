# Maven and IDE Integration
## Overcoming versioning problems with JDK and Maven

If you experience problems which are related to the versions of JDK and Maven you can achieve usage of the same versions as this project was developed on as follows:

* Make the required JDK version available on your computer: I recommend to use https://sdkman.io/ for Unix-like systems.
  Install it like there described. List the available JDKs by\
  `sdk list java`\
  For installation of the HotSpot variant of JDK 8 of provider AdoptOpenJDK use the listed identifier e.g. `8.0.292.hs-adpt` in the command\
  `sdk install java 8.0.292.hs-adpt`\
  If you do not want that it becomes the default Java version, answer in the end with `n`.
* Make that the required Java version is used by your project:
  You can do this for the current command window by\
  `sdk use java 8.0.292.hs-adpt`\
  You can do this permanently by\
  `sdk default java 8.0.292.hs-adpt`\
  Or you define the environment variable `JAVA_HOME` as the path of your JDK installation, on my computer it is e.g.\
  `/home/knabe/.sdkman/candidates/java/8.0.292.hs-adpt`\
  How to define environment variables and for which lifetime depends on your operating system and is beyond the scope of this introduction.
  If you want to use JDK 8 only for this project you can define a script which defines JAVA_HOME and then runs Maven, e.g. on Linux: File `mymvn.sh` with content\
  `JAVA_HOME=/home/knabe/.sdkman/candidates/java/8.0.292.hs-adpt ./mvnw "$@"`\
  Then\
  `./mymvn.sh clean test`\
  This runs the Maven Wrapper of the project with the given JDK.
  I recommend to .gitignore this script, as other developers may have their JDK at another location.

## IDE Integration

In order to develop, test, or study this project the usage of a Java IDE is recommended.

The [Spring Tool Suite (STS)](https://spring.io/tools) is preferred, as it already includes the AspectJ weaving for the compile phase.

[The AspectJ plugin for IntelliJ IDEA](https://plugins.jetbrains.com/plugin/4679-aspectj/versions) is only working for the Ultimate (commercial) edition, which I did not test.

For using this project with **Java 8** as of 2021-11-12 the last STS 3 is 3.9.12 according to [STS Download Versions](https://dist.springsource.com/release/STS/index.html). Download spring-tool-suite-3.9.12.RELEASE-e4.15.0 for your platform, unpack it, and run the program `STS` or `STS.exe` contained in the sts-... folder. You may run the STS on Java 11 or Java 8, but the test suite or generated program has to be run on a Java 8 JVM.

### Import into the STS
Click File > Import > Maven > Existing Maven Project. The dialog "Import Maven Projects" appears. Select as Root Directory: .../spring-ddd-bank. It should detect the contained `pom.xml`

### Run the REST Server
Navigate to file `src/main/java/.../Application.java`. By MouseRight choose "Run as Java Application".

If this fails with e.g. "ClassNotFoundException: javax.xml.bind.Validation" this is a symptom that the application was executed on a JVM > 8. Please check in the title line of the Console window.

For the current version of Spring DDD Bank it is important, that it is executed on a Java 8 VM. So at first go into the dialog Window > Preferences. In the left-hand tree choose Java > Installed JREs. If a Java 8 VM is not listed, click Add..., select Installed JRE Type as Standard VM and select a Directory... of an installed JVM 8. 

The JVM 8 does not have to become the default JVM for new projects, but has to be the JVM on which the Application and the test suite are executed in the STS.

