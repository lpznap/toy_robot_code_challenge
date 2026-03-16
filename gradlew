#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Gradle wrapper script for UNIX-like systems.
# To regenerate: run `gradle wrapper` in this directory.

##############################################################################
# Helper Functions
##############################################################################
die() {
    echo
    echo "ERROR: $*"
    echo
    exit 1
}

##############################################################################
# Resolve the real path of the script
##############################################################################
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# Use JAVA_HOME if set, else fall back to PATH
if [ -n "$JAVA_HOME" ]; then
    JAVA_EXE="$JAVA_HOME/bin/java"
else
    JAVA_EXE=java
fi

# Locate the wrapper jar relative to this script
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
WRAPPER_JAR="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="$SCRIPT_DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
    die "Gradle wrapper JAR not found at $WRAPPER_JAR\n" \
        "Run 'gradle wrapper' once (requires Gradle installed) to bootstrap."
fi

exec "$JAVA_EXE" \
    -classpath "$WRAPPER_JAR" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
