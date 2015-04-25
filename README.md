# Body Tracker
This is the respository for the Java portion of the body tracker project.

## Getting Started

### Step 1: Download
Clone the repo to your computer.

`git clone git@github.com:nirrek/bodyTracker.git`

### Step 2: IDE Files
Generate the configuration files for your IDE of choice

```
cd bodyTracker

# If using eclipse
./gradlew eclipse

# If using intellij idea
./gradlew idea
```

`Note for Windows users`: You need to use the `gradlew.bat` file rather than the `gradlew` file. I don't use Windows, but I assume .bat files are just a file you can easily execute on the command line.

An example of doing this for eclipse is shown below:
![Setup an eclipse project](https://s3.amazonaws.com/f.cl.ly/items/2o0d1u1w0G0o1r0k1r0r/Image%202015-04-24%20at%209.43.03%20pm.png)

### Step 3: Project Import
Import the project into your IDE in the way you typically would.

--------

## Building/Running The Project
Gradle should be used for all this stuff.

```bash
# build the project
./gradlew build

# run the tests
./gradlew test
```

Note that we are using `./gradlew build` rather than `gradle build`. This is because we are using the __gradle wrapper__ this ensures that everyone will be executing the build script using the same version of gradle (rather than whatever the version of their system-installed binary is)

