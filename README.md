# Podcast API Java Library

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
implementation "com.listennotes:podcast-api:1.0.5"
```

### Maven users

Add this dependency to your project's POM:

```xml
<dependency>
  <groupId>com.listennotes</groupId>
  <artifactId>podcast-api</artifactId>
  <version>1.0.5</version>
</dependency>
```

## Usage

The library needs to be configured with your account's API key which is
available in your [Listen API Dashboard](https://www.listennotes.com/api/dashboard/#apps). Set `apiKey` to its
value:

```java
import java.util.HashMap;
import org.json.JSONObject;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.ApiResponse;
import com.listennotes.podcast_api.exception.ListenApiException;

public class PodcastApiExample {
    public static void main(String[] args) {
        try {
          // If apiKey is null or "", then we'll use mock api for test data
          String apiKey = System.getenv("LISTEN_API_KEY");
          Client objClient = new Client(apiKey);

          // All parameters can be found at
          //    https://www.listennotes.com/api/docs/
          HashMap<String, String> parameters = new HashMap<>();
          parameters.put("q", "startup");
          parameters.put("type", "podcast");

          ApiResponse response = objClient.search(parameters);

          // For successful response, you get a org.json.JSONObject
          System.out.println(response.toJSON().toString(2));

          // Some handy methods to get your account stats
          System.out.println("\n=== Some Account Information ===\n");
          System.out.println("Free Quota this month: " + response.getFreeQuota() + " requests");
          System.out.println("Usage this month: " + response.getUsage() + " requests");
          System.out.println("Next billing date: " + response.getNextBillingDate());
        } catch (ListenApiException e) {
          e.printStackTrace();
        }
    }
}
```

If `apiKey` is None, then we'll connect to a [mock server](https://www.listennotes.com/api/tutorials/#faq0) that returns fake data for testing purposes.

You can quickly run sample code using gradle:
```shell
# Use api mock server for test data
./gradlew run

# Use production server for real data
LISTEN_API_KEY=your-api-key-here ./gradlew run
```


### Handling exceptions

Unsuccessful requests raise exceptions. The class of the exception will reflect
the sort of error that occurred.

| Exception Class  | Description |
| ------------- | ------------- |
|  AuthenticationException | wrong api key or your account is suspended  |
| ApiConnectionException  | fail to connect to API servers  |
| InvalidRequestException  | something wrong on your end (client side errors), e.g., missing required parameters  |
| RateLimitException  | you are using FREE plan and you exceed the quota limit  |
| NotFoundException  | endpoint not exist, or podcast / episode not exist  |
| ListenApiException  | something wrong on our end (unexpected server errors)  |

All exception classes can be found in [this folder](https://github.com/ListenNotes/podcast-api-java/tree/main/src/main/java/com/listennotes/podcast_api/exception).

And you can see some sample code [here](https://github.com/ListenNotes/podcast-api-java/blob/main/src/main/java/com/listennotes/podcast_api/Sample.java).
