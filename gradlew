#!/bin/sh
# Gradle wrapper script for UNIX systems

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=$(ls -ld "$PRG")
    link=$(expr "$ls" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=$(dirname "$PRG")"/$link"
    fi
done
SAVED="$(pwd)"
cd "$(dirname "$PRG")/" >/dev/null
APP_HOME="$(pwd -P)"
cd "$SAVED" >/dev/null

# Download gradle-wrapper.jar if it doesn't exist
WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
if [ ! -e "$WRAPPER_JAR" ]; then
    echo "Downloading Gradle Wrapper..."
    mkdir -p "$APP_HOME/gradle/wrapper"
    curl -L -o "$WRAPPER_JAR" "https://github.com/gradle/gradle/raw/v8.2.0/gradle/wrapper/gradle-wrapper.jar"
fi

# Add JVM options
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Find Java
if [ -n "$JAVA_HOME" ] ; then
    JAVACMD="$JAVA_HOME/bin/java"
else
    JAVACMD="java"
fi

# Run Gradle
exec "$JAVACMD" $DEFAULT_JVM_OPTS -jar "$WRAPPER_JAR" "$@"
