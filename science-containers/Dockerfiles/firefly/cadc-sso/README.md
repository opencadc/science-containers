# Gradle Java Version Mismatch Issues

## **Root Cause**

When installing `gradle` on macOS using Homebrew, the `gradle` formula may not set the correct Java version for the **Launcher JVM**. This can lead to a mismatch between the **Launcher JVM** and **Daemon JVM**, causing class file version errors during the build process.

## **Problem Description**

When using Gradle, you may encounter a mismatch between the **Launcher JVM** and **Daemon JVM**, resulting in testing errors for linking the `firefly.server.security` package. The error message may look like:

```
class file has wrong version 67.0, should be 65.0
```
This typically occurs when:
- The Gradle Launcher is using a different JDK version than the Daemon.
- Gradle's Daemon JVM is configured correctly, but the Launcher JVM defaults to a globally installed JDK (e.g., OpenJDK 23 instead of OpenJDK 21).

## **Symptoms**
1. Gradle build fails with errors like:
   ```
   bad class file: <path_to_class>
   class file has wrong version 67.0, should be 65.0
   ```

    This happens because the project is build using JAVA 21, but tested using JAVA 23.

2. Running `gradle --version` shows:
   ```
   Launcher JVM:  23.0.2
   Daemon JVM:    /opt/homebrew/opt/openjdk@21 (from org.gradle.java.home)
   ```
   The **Launcher JVM** is using a different version (e.g., OpenJDK 23) than the **Daemon JVM** (e.g., OpenJDK 21).

## **Solution**

To resolve this issue, you need to ensure both the **Launcher JVM** and **Daemon JVM** are using the same version of Java (in this case, OpenJDK 21).

### **1. Set the Java Version for Gradle**
#### Option A: Set `GRADLE_OPTS` Environment Variable
Configure Gradle to use OpenJDK 21 by setting the `GRADLE_OPTS` environment variable:

```bash
export GRADLE_OPTS="-Dorg.gradle.java.home=/opt/homebrew/opt/openjdk@21/"
```
To make this permanent, add it to your shell configuration file (e.g., **~/.zshrc** or **~/.bashrc**):

```bash
echo 'export GRADLE_OPTS="-Dorg.gradle.java.home=/opt/homebrew/opt/openjdk@21/"' >> ~/.zshrc
source ~/.zshrc
```

#### Option B: Set `JAVA_HOME` Globally
Alternatively, configure `JAVA_HOME` globally to point to OpenJDK 21:

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH=$JAVA_HOME/bin:$PATH
```
To make this permanent:

```bash
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@21' >> ~/.zshrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zshrc
source ~/.zshrc
```

### **2. Verify Gradle Java Version**
After setting the environment variables, check if Gradle is using the correct Java version:

```bash
gradle --version
```
Expected output:
```bash
------------------------------------------------------------
Gradle 8.12.1
------------------------------------------------------------

Build time:    2025-01-24 12:55:12 UTC
Revision:      0b1ee1ff81d1f4a26574ff4a362ac9180852b140

Kotlin:        2.0.21
Groovy:        3.0.22
Ant:           Apache Ant(TM) version 1.10.15 compiled on August 25 2024
Launcher JVM:  21.0.6 (Homebrew 21.0.6)
Daemon JVM:    /opt/homebrew/opt/openjdk@21 (from org.gradle.java.home)
OS:            Mac OS X 14.7.1 aarch64
```

Both the **Launcher JVM** and **Daemon JVM** should now use Java 21.

### **3. Stop and Restart Gradle Daemon**
Kill any running Gradle daemons to ensure they pick up the new configuration:

```bash
gradle --stop
```
Then, rebuild the project:

```bash
gradle clean build --no-daemon
```

### **4. Rebuild the Project**
If Gradle is now using the correct Java version, the build should succeed without errors:

```bash
gradle build --no-daemon
```

## **Summary**
By ensuring both the Launcher JVM and Daemon JVM use the same Java version (OpenJDK 21), you can avoid class file version mismatch errors and ensure compatibility with your project requirements. Use the steps above to configure Gradle properly and verify your build environment.

