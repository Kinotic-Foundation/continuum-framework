# Continuum
Contains all of the sub-projects under the continuum umbrella. All projects are Spring Boot projects and new projects should be created using the spring initializer.

Version 1.0-SNAPSHOT

## Table of Contents
- [System Requirements](#requirements)
- [Vocabulary](#vocabulary)
- [Creating a New Continuum Application](#updating-to-new-releases)
- [Gotchas](#gotchas)

## System Requirements
1. Install SdkMan https://sdkman.io/
2. Install Java 1.15 with SkdMan
4. Install Gradle 6.8.9 with SdkMan
5. Install Yarn https://yarnpkg.com/en/


## Vocabulary


## Creating a New Continuum Application
- Create a new Spring Boot Application using Spring Initializer
- Modify the build.gradle to include any needed Continuum Dependencies

## Gotchas
* When modifying the build for dependent modules such as continuum-core the application projects do not see the change.
    * You must go to the gradle tool window and press the "Refresh All Gradle Projects" button.
