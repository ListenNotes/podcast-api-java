# Podcast API Java Library

[![Java CI](https://github.com/ListenNotes/podcast-api-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/ListenNotes/podcast-api-java/actions/workflows/gradle.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.listennotes/podcast-api)](https://central.sonatype.com/artifact/com.listennotes/podcast-api)

The official Java client for the [Listen Notes Podcast API](https://www.listennotes.com/podcast-api/),
for JVM applications written in Java, Kotlin, Scala, Groovy, and other languages.
Search podcasts and episodes, fetch metadata, and manage playlists.
Questions? Contact [hello@listennotes.com](mailto:hello@listennotes.com).

## Installation

Version 3.0.0 requires **Java 17 or later**. It uses the JDK HTTP client and
`org.json` for JSON responses. The Gradle wrapper is included for development.

Gradle, with Maven Central enabled:

```groovy
implementation 'com.listennotes:podcast-api:3.0.0'
```

Maven:

```xml
<dependency>
  <groupId>com.listennotes</groupId>
  <artifactId>podcast-api</artifactId>
  <version>3.0.0</version>
</dependency>
```

These coordinates become installable when 3.0.0 is published to Maven Central.

## Usage

```java
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.ApiResponse;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        ApiResponse response = client.search(Map.of("q", "design"));
        System.out.println(response.toJSON().toString(2));
        System.out.println(response.getUsage());
    }
}
```

A null or blank API key selects the credential-free public mock server. Its
responses are static: playlist writes do not persist there. With an API key,
requests use production and count toward your quota. Never embed API keys in
public client applications.

Every method accepts one `Map<String, String>` containing path identifiers,
query parameters, and form fields. The client encodes and places them correctly
without modifying your map. Null values are omitted; empty strings are sent,
so `Map.of("id", playlistId, "item_id", itemId, "notes", "")` clears notes.
`justListen()`, `fetchPodcastLanguages()`, and `fetchPodcastRegions()` also
retain their existing no-argument overloads.

Clients retain their own credentials and settings and can make concurrent
requests. The default request timeout is 30 seconds and connection timeout is
5 seconds. Use `setResponseTimeoutMs(10000)` or `setUserAgent("my-app/1.0")`
to configure subsequent requests. Redirects are not followed; the SDK adds no
retry loop. For local tests, `new Client(null, "http://localhost:8000/api/v2")`
selects an explicit base URL.

## Responses and errors

`ApiResponse` retains the raw body (`toString()`), parsed JSON (`toJSON()`),
HTTP status (`getStatusCode()`), and case-insensitive headers (`getHeader`,
`getHeaders`). Quota helpers are `getFreeQuota()`, `getUsage()`,
`getNextBillingDate()`, and `getLatencySeconds()`. Missing or malformed numeric
headers return null. Empty successful responses remain accessible as raw text;
only call `toJSON()` for a JSON object response.

Catch `ListenApiException` or a specific subclass: `InvalidRequestException`
(400 or invalid local input), `AuthenticationException` (401),
`PermissionDeniedException` (403), `NotFoundException` (404),
`RateLimitException` (429), or `ApiConnectionException` (connection, timeout,
or interruption). Other non-2xx statuses also throw `ListenApiException`.
For HTTP errors, `getResponse()` exposes the API's exact error body and headers,
and `getStatusCode()` gives the status. Local/connection failures have no
response and retain their underlying cause when available. Interrupted requests
restore the thread's interrupt flag.

## Migrating from 1.x

- Upgrade the runtime from Java 8 to Java 17 or newer.
- All 25 existing method names, constructors, map arguments, and JSON response
  helpers remain available. Five playlist write methods are added below.
- Path values are now encoded and caller maps are no longer modified.
- All non-2xx responses throw, including 403 and redirects; inspect
  `exception.getResponse()` for server error details instead of matching messages.
- Invalid timeout/header configuration is rejected immediately.
- `getConnection(String)` remains available but is deprecated; endpoint methods
  now use `java.net.http.HttpClient` internally.

## Development

```sh
./gradlew check
./gradlew jar sourcesJar plainJavadocJar generatePomFileForMavenPublication
bash scripts/verify-package.sh
# Optional integration requests, only to the stateless public mock:
./gradlew integrationTest
```

Default tests use an in-process HTTP server, without production or public mock
requests. They cover every generated method, encoding, empty values, errors,
timeouts, response headers, concurrency, and client isolation. README examples
are compiled without executing them. The standalone check copies this package
outside Git and the monorepo and repeats the offline build/tests.

Endpoint wrappers, the test contract, and the two marked README sections are
generated from Listen Notes' canonical OpenAPI specification and Java registry.
Run `sync.py java` or `sync.py java --check` in the monorepo's project Python
environment. The standalone package does not require the monorepo, its database,
or the generator. Handwritten sections outside markers survive regeneration.

Dependencies are locked in `gradle.lockfile`. After a reviewed dependency update,
use `./gradlew dependencies --write-locks` and run the checks above.

## Publishing

Publishing is a separate, explicitly approved release step. The old OSSRH
service is retired; publishing now uses the Sonatype Central Portal. Set the
Central Portal user-token credentials and signing key outside the repository:
`ORG_GRADLE_PROJECT_mavenCentralUsername`, `ORG_GRADLE_PROJECT_mavenCentralPassword`,
`ORG_GRADLE_PROJECT_signingInMemoryKey`, and
`ORG_GRADLE_PROJECT_signingInMemoryKeyPassword` (if the key is encrypted).

After reviewing artifacts, CI, and the version, `./gradlew publishToMavenCentral`
uploads a signed deployment for review in the Central Portal. Release it there
only when publication is authorized. Verify Maven Central resolution with a
fresh consumer before advancing the website's recorded published version.
Prepare release notes in the standalone GitHub repository; keep publication
separate from creating the draft. Do not store credentials or signing keys in Git.

## Method index

<!-- BEGIN GENERATED METHOD INDEX -->

- [`search`](#search) — `GET /search`
- [`typeahead`](#typeahead) — `GET /typeahead`
- [`searchEpisodeTitles`](#searchepisodetitles) — `GET /search_episode_titles`
- [`spellcheck`](#spellcheck) — `GET /spellcheck`
- [`fetchRelatedSearches`](#fetchrelatedsearches) — `GET /related_searches`
- [`fetchTrendingSearches`](#fetchtrendingsearches) — `GET /trending_searches`
- [`fetchBestPodcasts`](#fetchbestpodcasts) — `GET /best_podcasts`
- [`fetchPodcastById`](#fetchpodcastbyid) — `GET /podcasts/{id}`
- [`deletePodcast`](#deletepodcast) — `DELETE /podcasts/{id}`
- [`fetchEpisodeById`](#fetchepisodebyid) — `GET /episodes/{id}`
- [`batchFetchEpisodes`](#batchfetchepisodes) — `POST /episodes`
- [`batchFetchPodcasts`](#batchfetchpodcasts) — `POST /podcasts`
- [`fetchCuratedPodcastsListById`](#fetchcuratedpodcastslistbyid) — `GET /curated_podcasts/{id}`
- [`fetchPodcastGenres`](#fetchpodcastgenres) — `GET /genres`
- [`fetchPodcastRegions`](#fetchpodcastregions) — `GET /regions`
- [`fetchPodcastLanguages`](#fetchpodcastlanguages) — `GET /languages`
- [`justListen`](#justlisten) — `GET /just_listen`
- [`fetchCuratedPodcastsLists`](#fetchcuratedpodcastslists) — `GET /curated_podcasts`
- [`fetchRecommendationsForPodcast`](#fetchrecommendationsforpodcast) — `GET /podcasts/{id}/recommendations`
- [`fetchRecommendationsForEpisode`](#fetchrecommendationsforepisode) — `GET /episodes/{id}/recommendations`
- [`submitPodcast`](#submitpodcast) — `POST /podcasts/submit`
- [`fetchPlaylistById`](#fetchplaylistbyid) — `GET /playlists/{id}`
- [`fetchMyPlaylists`](#fetchmyplaylists) — `GET /playlists`
- [`fetchAudienceForPodcast`](#fetchaudienceforpodcast) — `GET /podcasts/{id}/audience`
- [`fetchPodcastsByDomain`](#fetchpodcastsbydomain) — `GET /podcasts/domains/{domain_name}`
- [`createPlaylist`](#createplaylist) — `POST /playlists`
- [`updatePlaylist`](#updateplaylist) — `PUT /playlists/{id}`
- [`addPlaylistItem`](#addplaylistitem) — `POST /playlists/{id}/items`
- [`deletePlaylistItem`](#deleteplaylistitem) — `DELETE /playlists/{id}/items/{item_id}`
- [`updatePlaylistItemNotes`](#updateplaylistitemnotes) — `PUT /playlists/{id}/items/{item_id}`

<!-- END GENERATED METHOD INDEX -->

## API reference

<!-- BEGIN GENERATED API REFERENCE -->

Every method accepts `Map<String, String>` and returns `ApiResponse`. Set `LISTEN_API_KEY` for real API requests; without it these examples use the stateless mock server. Save an example as `Example.java`.

### search

Full-text search

`GET /search`

Full-text search on episodes, podcasts, or curated lists of podcasts.
Use the `offset` parameter to paginate through search results.
The FREE plan allows to see up to 30 search results (or `offset` < 30) per query.
The PRO plan allows to see up to 300 search results (or `offset` < 300) per query.
The ENTERPRISE plan allows to see up to 10,000 search results (or `offset` < 10000) per query.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "star wars");
        parameters.put("sort_by_date", "0");
        parameters.put("type", "episode");
        parameters.put("offset", "0");
        parameters.put("len_min", "10");
        parameters.put("len_max", "30");
        parameters.put("genre_ids", "68,82");
        parameters.put("published_before", "1580172454000");
        parameters.put("published_after", "0");
        parameters.put("only_in", "title,description");
        parameters.put("language", "English");
        parameters.put("region", "");
        parameters.put("safe_mode", "0");
        parameters.put("unique_podcasts", "0");
        parameters.put("interviews_only", "0");
        parameters.put("sponsored_only", "0");
        parameters.put("page_size", "10");
        System.out.println(client.search(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-search)

### typeahead

Typeahead search

`GET /typeahead`

Suggest search terms, podcast genres, and podcasts.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "star wars");
        parameters.put("show_podcasts", "1");
        parameters.put("show_genres", "1");
        parameters.put("safe_mode", "0");
        System.out.println(client.typeahead(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-typeahead)

### searchEpisodeTitles

Find individual episodes by searching for their titles

`GET /search_episode_titles`

Conduct targeted searches for individual episodes by title and refine results using the podcast id such as
Listen Notes Podcast ID, Apple Podcasts ID, Spotify ID, or RSS feed URL.
This endpoint is specially designed to streamline the import of specific episodes from platforms
like Apple Podcasts and Spotify into your application.
Compared to the GET /search endpoint, which performs full-text searches across multiple fields,
this endpoint focuses solely on episode titles for enhanced accuracy and performance.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "Jerusalem Demsas on The Dispossessed");
        System.out.println(client.searchEpisodeTitles(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-search_episode_titles)

### spellcheck

Spell check on a search term

`GET /spellcheck`

Suggest a list of words that correct the spelling errors of a search term. This endpoint is available only in the PRO/ENTERPRISE plan.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "microsft stock");
        System.out.println(client.spellcheck(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-spellcheck)

### fetchRelatedSearches

Fetch related search terms

`GET /related_searches`

Suggest related search terms. The results are more comprehensive than from `GET /typeahead`. This endpoint is available only in the PRO/ENTERPRISE plan.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("q", "evergrande");
        System.out.println(client.fetchRelatedSearches(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-related_searches)

### fetchTrendingSearches

Fetch trending search terms

`GET /trending_searches`

Fetch up to 10 most recent trending search terms on the Listen Notes platform.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        System.out.println(client.fetchTrendingSearches(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-trending_searches)

### fetchBestPodcasts

Fetch a list of best podcasts by genre

`GET /best_podcasts`

Get a list of curated best podcasts by genre,
which are curated by Listen Notes staffs based on various signals from the Internet, e.g.,
top charts on other podcast platforms, recommendations from mainstream media,
user activities on listennotes.com...
You can get the genre ids from `GET /genres` endpoint.
This endpoint returns same data as https://www.listennotes.com/best-podcasts/

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("genre_id", "93");
        parameters.put("page", "2");
        parameters.put("region", "us");
        parameters.put("sort", "listen_score");
        parameters.put("safe_mode", "0");
        System.out.println(client.fetchBestPodcasts(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-best_podcasts)

### fetchPodcastById

Fetch detailed meta data and episodes for a podcast by id

`GET /podcasts/{id}`

Fetch detailed meta data and episodes for a specific podcast (up to 10 episodes each time).
You can use the **next_episode_pub_date** parameter to do pagination and fetch more episodes.
During pagination with **next_episode_pub_date**, an empty **episodes** array in the response signals that no more episodes are available.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79");
        parameters.put("next_episode_pub_date", "1479154463000");
        parameters.put("sort", "recent_first");
        System.out.println(client.fetchPodcastById(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-podcasts-id)

### deletePodcast

Request to delete a podcast

`DELETE /podcasts/{id}`

Podcast hosting services can use this endpoint to streamline the process of podcast deletion on behave of their users (podcasters). We will review the deletion request within 12 hours. If the podcast is already deleted, the "status" field in the response will be "deleted". Otherwise, the status field will be "in review". If you want to get a notification once the podcast is deleted, you can configure a webhook url in the dashboard: listennotes.com/api/dashboard/#webhooks

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "4d3fe717742d4963a85562e9f84d8c79");
        parameters.put("reason", "the podcaster wants to delete it");
        System.out.println(client.deletePodcast(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#delete-api-v2-podcasts-id)

### fetchEpisodeById

Fetch detailed meta data for an episode by id

`GET /episodes/{id}`

Fetch detailed meta data for a specific episode.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "6b6d65930c5a4f71b254465871fed370");
        parameters.put("show_transcript", "1");
        System.out.println(client.fetchEpisodeById(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-episodes-id)

### batchFetchEpisodes

Batch fetch basic meta data for episodes

`POST /episodes`

Batch fetch basic meta data for up to 10 episodes. This endpoint could be used to implement custom playlists for individual episodes. For detailed meta data of an individual episode, you need to use `GET /episodes/{id}`. This endpoint is available only in the PRO/ENTERPRISE plan.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("ids", "c577d55b2b2b483c969fae3ceb58e362,0f34a9099579490993eec9e8c8cebb82");
        System.out.println(client.batchFetchEpisodes(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#post-api-v2-episodes)

### batchFetchPodcasts

Batch fetch basic meta data for podcasts

`POST /podcasts`

Batch fetch basic meta data for up to 10 podcasts.
This endpoint could be used to build something like OPML import,
allowing users to import a bunch of podcasts via rss urls.
For detailed meta data (including episodes) of an individual podcast, you need to use `GET /podcasts/{id}`. This endpoint is available only in the PRO/ENTERPRISE plan.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("ids", "3302bc71139541baa46ecb27dbf6071a,68faf62be97149c280ebcc25178aa731,37589a3e121e40debe4cef3d9638932a,9cf19c590ff0484d97b18b329fed0c6a");
        parameters.put("rsses", "https://rss.art19.com/recode-decode,https://rss.art19.com/the-daily,https://www.npr.org/rss/podcast.php?id=510331,https://www.npr.org/rss/podcast.php?id=510331");
        parameters.put("itunes_ids", "1457514703,1386234384,659155419");
        parameters.put("spotify_ids", "3DDfEsKDIDrTlnPOiG4ZF4,4qDNe5Gvl1XxdLinUGEXrC,23NZCM4ik6o3UYkM473Itz");
        parameters.put("show_latest_episodes", "1");
        parameters.put("next_episode_pub_date", "1557394247000");
        System.out.println(client.batchFetchPodcasts(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#post-api-v2-podcasts)

### fetchCuratedPodcastsListById

Fetch a curated list of podcasts by id

`GET /curated_podcasts/{id}`

Get detailed meta data of all podcasts in a specific curated list.
This endpoint returns same data as https://www.listennotes.com/curated-podcasts/

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "SDFKduyJ47r");
        System.out.println(client.fetchCuratedPodcastsListById(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-curated_podcasts-id)

### fetchPodcastGenres

Fetch a list of podcast genres

`GET /genres`

Get a list of podcast genres that are supported in Listen Notes.
The genre id can be passed to other endpoints as a parameter to get podcasts in a specific genre,
e.g., `GET /best_podcasts`, `GET /search`...
You may want to cache the list of genres on the client side.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("top_level_only", "1");
        System.out.println(client.fetchPodcastGenres(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-genres)

### fetchPodcastRegions

Fetch a list of supported countries/regions for best podcasts

`GET /regions`

It returns a dictionary of country codes (e.g., us, gb...) & country names (United States, United Kingdom...). The country code is used in the query parameter **region** of `GET /best_podcasts`.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        System.out.println(client.fetchPodcastRegions(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-regions)

### fetchPodcastLanguages

Fetch a list of supported languages for podcasts

`GET /languages`

Get a list of languages that are supported in Listen Notes database. You can use the language string as query parameter in `GET /search`.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        System.out.println(client.fetchPodcastLanguages(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-languages)

### justListen

Fetch a random podcast episode

`GET /just_listen`

Recently published episodes are more likely to be fetched. Good luck!

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        System.out.println(client.justListen(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-just_listen)

### fetchCuratedPodcastsLists

Fetch curated lists of podcasts

`GET /curated_podcasts`

A bunch of curated lists from online media. For each list, you'll get basic info of up to 5 podcasts. To get detailed meta data of all podcasts in a specific list, you need to use `GET /curated_podcasts/{id}`. We add new curated lists to the database on a daily basis.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("page", "2");
        System.out.println(client.fetchCuratedPodcastsLists(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-curated_podcasts)

### fetchRecommendationsForPodcast

Fetch recommendations for a podcast

`GET /podcasts/{id}/recommendations`

Fetch up to 8 podcast recommendations based on the given podcast id.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "25212ac3c53240a880dd5032e547047b");
        parameters.put("safe_mode", "0");
        System.out.println(client.fetchRecommendationsForPodcast(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-podcasts-id-recommendations)

### fetchRecommendationsForEpisode

Fetch recommendations for an episode

`GET /episodes/{id}/recommendations`

Fetch up to 8 episode recommendations based on the given episode id.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "254444fa6cf64a43a95292a70eb6869b");
        parameters.put("safe_mode", "0");
        System.out.println(client.fetchRecommendationsForEpisode(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-episodes-id-recommendations)

### submitPodcast

Submit a podcast to Listen Notes database

`POST /podcasts/submit`

Podcast hosting services can use this endpoint to help your users directly submit a new podcast to Listen Notes database. If the podcast doesn't exist in the database, "status" in the response will be "in review", and we'll review it within 12 hours. If the podcast exists, "status" in the response will be "found". If this submission is rejected, "status" in the response will be "rejected". You can use `POST /podcasts` to check if multiple podcasts exist in the database. If you want to get a notification once the podcast is accepted, you can either specify the "email" parameter or configure a webhook url in the dashboard: listennotes.com/api/dashboard/#webhooks

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("rss", "https://feeds.megaphone.fm/committed");
        parameters.put("email", "hello@example.com");
        System.out.println(client.submitPodcast(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#post-api-v2-podcasts-submit)

### fetchPlaylistById

Fetch a playlist's info and items (i.e., episodes or podcasts).

`GET /playlists/{id}`

A playlist can contain both episodes and podcasts, shown in separate views,
just like playlists created via listennotes.com/listen/.
This endpoint fetches items from the saved default view unless **type** is specified.
The response type and listennotes_url describe the selected view.
You can use the **last_pub_date_ms** parameter to do pagination and fetch more items.
A playlist can be **public** (discoverable on ListenNotes.com),
**unlisted** (accessible to anyone who knows the playlist id),
or **private** (accessible when the API admin has active playlist membership).
Public and unlisted playlists can also be fetched by ID regardless of their owner.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "m1pe7z60bsw");
        parameters.put("type", "episode_list");
        parameters.put("last_timestamp_ms", "0");
        parameters.put("sort", "recent_added_first");
        System.out.println(client.fetchPlaylistById(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-playlists-id)

### fetchMyPlaylists

Fetch a list of your playlists.

`GET /playlists`

This endpoint lists playlists with an active membership for the API admin, including playlists they created or joined.
Each playlist includes its saved default **type** and a **listennotes_url** for that view.
You can use the **page** parameter to do pagination and fetch more playlists.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sort", "recent_added_first");
        parameters.put("page", "1");
        System.out.println(client.fetchMyPlaylists(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-playlists)

### fetchAudienceForPodcast

Fetch audience demographics for a podcast

`GET /podcasts/{id}/audience`

Fetch audience demographics for a podcast - 1) directly measured on the Listen Notes platform; 2) only supports audience breakdown by regions for now; 3) not every podcast has data.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "25212ac3c53240a880dd5032e547047b");
        System.out.println(client.fetchAudienceForPodcast(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-podcasts-id-audience)

### fetchPodcastsByDomain

Fetch podcasts by a publisher's domain name

`GET /podcasts/domains/{domain_name}`

Fetch podcasts by a publisher's domain name, e.g., nytimes.com, wondery.com, npr.org...
Each request will return up to 10 podcasts. You can use the `page` parameter to paginate.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("domain_name", "nytimes.com");
        parameters.put("page", "1");
        System.out.println(client.fetchPodcastsByDomain(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#get-api-v2-podcasts-domains-domain_name)

### createPlaylist

Create a playlist.

`POST /playlists`

Create an empty playlist owned by the API admin. Name is required; description defaults to an empty string, visibility defaults to public, and type defaults to episode_list. Set type to podcast_list to make podcasts the default view. The response includes the saved type and its listennotes_url.

Only playlists owned by your admin API account can be modified; contributor membership does not grant write access.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "My favorite podcasts");
        parameters.put("description", "Podcasts and episodes to revisit.");
        parameters.put("visibility", "public");
        parameters.put("type", "episode_list");
        System.out.println(client.createPlaylist(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#post-api-v2-playlists)

### updatePlaylist

Update playlist metadata.

`PUT /playlists/{id}`

Update any subset of name, description, visibility, and type. Omitted fields remain unchanged; at least one field is required. Switching to private rotates the playlist RSS secret. Type selects the saved default view (episode_list or podcast_list) and the returned listennotes_url; changing it preserves all existing episodes and podcasts.

Only playlists owned by your admin API account can be modified; contributor membership does not grant write access.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "m1pe7z60bsw");
        parameters.put("name", "My favorite podcasts");
        parameters.put("description", "Podcasts and episodes to revisit.");
        parameters.put("visibility", "public");
        parameters.put("type", "podcast_list");
        System.out.println(client.updatePlaylist(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#put-api-v2-playlists-id)

### addPlaylistItem

Add an episode or podcast to a playlist.

`POST /playlists/{id}/items`

Provide exactly one non-empty episode_id or podcast_id; an empty unused ID field is ignored. Invalid ID formats return 400 and identify the field. A missing episode or podcast returns 404 with an error such as "Episode not found: {episode_id}." or "Podcast not found: {podcast_id}.". Existing active items are reused (200); new or restored items return 201. Omitted notes preserve existing notes, including when restoring a deleted item; supplied notes replace them.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "m1pe7z60bsw");
        parameters.put("episode_id", "e53e6992a5b7492f9ea6fcd85d9ad95f");
        parameters.put("notes", "Worth a listen.");
        System.out.println(client.addPlaylistItem(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#post-api-v2-playlists-id-items)

### deletePlaylistItem

Remove an item from a playlist.

`DELETE /playlists/{id}/items/{item_id}`

Delete a playlist item. Repeating deletion of the same item succeeds. This does not delete the episode or podcast from the podcast database.

Only playlists owned by your admin API account can be modified; contributor membership does not grant write access.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "m1pe7z60bsw");
        parameters.put("item_id", "23");
        System.out.println(client.deletePlaylistItem(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#delete-api-v2-playlists-id-items-item_id)

### updatePlaylistItemNotes

Update notes for a playlist item.

`PUT /playlists/{id}/items/{item_id}`

Replace item notes, or send an empty string to clear them. The item ID and added_at_ms remain unchanged.

Only playlists owned by your admin API account can be modified; contributor membership does not grant write access.

```java
import java.util.HashMap;
import java.util.Map;
import com.listennotes.podcast_api.Client;
import com.listennotes.podcast_api.exception.ListenApiException;

public class Example {
    public static void main(String[] args) throws ListenApiException {
        Client client = new Client(System.getenv("LISTEN_API_KEY"));
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", "m1pe7z60bsw");
        parameters.put("item_id", "23");
        parameters.put("notes", "");
        System.out.println(client.updatePlaylistItemNotes(parameters).toJSON().toString(2));
    }
}
```

[Full API documentation](https://www.listennotes.com/api/docs/#put-api-v2-playlists-id-items-item_id)

<!-- END GENERATED API REFERENCE -->

## License

[MIT](LICENSE)
