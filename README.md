# web crawler

## Requirements

* Java 8
* Linux machine (should also work on Windows through `gradlew.bat`)


## Usage

Build jar:

```
./gradlew bootJar
```

Starting app:

```
java -jar ./build/libs/web-crawler.jar <domain_URL with http or https prefix>
```

Results can be viewed in files *crawled_pages.out* and *site_map.out*.

NOTE on https vs http - those are considered two different domains. Easy to change this assumption, but be aware of it being made

Tests:

```
./gradlew test
```
Open in browser report file `build/reports/tests/test/index.html`

With coverage:
```
./gradlew test jacocoTestReport
```

Open in browser report file `build/reports/jacoco/test/html/index.html`


Enable E2E tests (might require `clean`):

1. Generate JavaDoc:
```
./gradlew javadoc
```

2. Go to `build/docs` folder and run:
```
python -m SimpleHTTPServer
```

3. Execute tests
```
./gradlew test jacocoTestReport
```

## Plan

- [x] define abstractions
- [x] implement core ones
- [x] preapre E2E correctness check
- [x] implement multithreaded version
- [ ] Dockerfile

## Considerations

* Plan on using Spring Boot from the very begining. Probably overkill for this project.

## Potential extension points

* input data validation (is argument present, is valid URL)
* Scaling through messaging solution like Kafka and putting workers on Kubernetes alike platform
* try Favicon?
* don't use Internet in tests. Serve local webpage to make exact asserts in tests
* evaluate JS on pages to get more links (through Selenium?)
* don't download big/binary files etc.
* metrics collection (througput, errors, successes etc.)
* retry / rate limitting
* try to handle not correct URLs (now workaround to skip those)
* implement infinite loop prevention (ex when pages are genrated)
* maybe some improvement can be done in concurrency scheme used here. I was able to observe stagger which resulted in longer execution



