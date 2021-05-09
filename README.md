# Podcast API Go Library

[![Build Status](https://travis-ci.com/ListenNotes/podcast-api-java.svg?branch=main)](https://travis-ci.com/ListenNotes/podcast-api-java)

The Podcast API Java library provides convenient access to the [Listen Notes Podcast API](https://www.listennotes.com/api/) from
applications written in the Java language.

Simple and no-nonsense podcast search & directory API. Search the meta data of all podcasts and episodes by people, places, or topics. It's the same API that powers [the best podcast search engine Listen Notes](https://www.listennotes.com/).

If you have any questions, please contact [hello@listennotes.com](hello@listennotes.com?subject=Questions+about+the+Java+SDK+of+Listen+API)

<a href="https://www.listennotes.com/api/"><img src="https://raw.githubusercontent.com/ListenNotes/ListenApiDemo/master/web/src/powered_by_listennotes.png" width="300" /></a>

## Installation

### Requirements

- Java 1.8 or later

### Gradle users

Add this dependency to your project's build file:

```groovy
implementation "com.listennotes:podcast-api:1.0.4"
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.listennotes</groupId>
  <artifactId>podcast-api</artifactId>
  <version>1.0.4</version>
</dependency>
```

## Development

```sh
# Run sample app
./gradlew run -q

# Run all unit tests
./gradlew test

# Run a specific test case
./gradlew test --tests ClientTest.testSearch
```
