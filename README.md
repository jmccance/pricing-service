pricing-service
===============

Usage
-----

The service can be started locally with `./gradlew run`. By default, the service will bind to `0.0.0.0:8080` using the rate configuration in `conf/rate-config.json`. 

To test that the service is up and running, try the following `curl` command:

```
curl "http://localhost:8080/rates?start=2015-07-01T07:00:00Z&end=2015-07-01T17:00:00Z" \
          -H 'Accept: application/json'
```

To see an example error message, try passing a nonsensical end date.

```
curl "http://localhost:8080/rates?start=2015-07-01T07:00:00Z&end=21-Bureaucracy-3184" \
          -H 'Accept: application/json'
```

You can also interact with the API using the bundled [Swagger UI](http://localhost:8080/swagger-ui/) using the "Try it out" button on each method.

To test with a different rate configuration, set the `RATE_CONFIG` environment variable when running. For example:

```
RATE_CONFIG=./conf/alt-rate-config.json gradle run
```

To run the tests, run `./gradlew check`.

Notes
-----

### Configuration

Configuration is implemented using [Lightbend Config](https://github.com/lightbend/config). Default configuration is loaded from `reference.conf`, which is found in `main/resources`. Overrides can be handled by specifying an alternate application config file with the `config.file` system property, or by using system properties to override specific configuration fields (e.g., `-Dservice.port=8081`). This makes it straightforward to version your configuration for different environments separately from the application itself.

Note, however, that Gradle really doesn't give you a great way to pass these values down to the forked JVM. At the moment, you can't simply run `gradle run -Dconfig.file=dev.conf` to try a different configuration file. To get this working, we will probably need a custom Gradle task that passes through the appropriate (or all) system properties to the forked JVM.

In the meantime, for your convenience the rates configuration can be set using the `RATE_CONFIG` environment variable.

### Dependency Injection

Using HK2 for dependency injection, though I do not believe this is a satisfactory solution. Dagger or Guice would be preferable, but Jersey made using these overly complicated. I experimented with using GuiceBridge to expose Guice dependencies to HK2, but was unable to get the Guice-provided ObjectMapper to inject into the JacksonJsonProvider.

- Attempted to use Guice, but had issues getting the injection to work as expected. Mostly in trying to inject an ObjectMapper with Guice, kept running into issues with the HK2 injection running first(?), so it would just use the default ObjectMapper provided by the JacksonJsonProvider.
- Settled on using HK2.
- Am aware of the "inhabitant" stuff that reduces boilerplate, but wanted to focus on other things first. 
- Am interested in exploring Dagger, since it's compile-time.

### API Specification

The [OpenAPI](https://openapis.org/) specification is published to [http://localhost:8080/openapi.yaml](http://localhost:8080/openapi.yaml). Swagger UI is also included, and can be viewed at [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/).

The API is not generated from the JAX-RS resources, even though that is something offered by the Swagger libraries. This was disabled by setting `readAllResources` in `openapi-configuration.yaml` to `false`. My main reason for this is that I believe the contract should determine the implementation, not the other way around. This makes it easier to rapidly prototype the API design and share it with other teams before writing any code. The API can also be maintained and designed by someone without them having to dig into a codebase and learn the Swagger annotations.

In an ideal world, I think the interfaces for the resources should be generated from the OpenAPI, but that was not something I wanted to tackle for this project. Instead, I settled for manually specifying the spec. I stuck with the swagger-jaxrs2 dependency, though, because it provides the content-negotiation piece. I'm able to write the YAML spec and let the library deal with converting that to JSON as required by the consumers of the API.

### Operational Considerations

A few choices were made in implementing this service to try to help with day-to-day operations.

- Every request is assigned a unique request id. This id is returned on any non-trivial error message (so not 404s for now, but on 500s and 400s), as well as being included in a header on all responses. This is to help with debugging, since errors can be quickly mapped back to their relevant logging.
- Error messages are structured and provide lots of context for troubleshooters, including the request id, the timestamp, the returned status code, and the endpoint that served the request. Useful when people just send you the response body they got but nothing else.
- All logs are structured using the Logstash JSON layout to help make logs searchable and make it easier to build metrics off of them.
- Each distinct request gets its own log entry with key data about its duration, endpoint, result, exceptions, etc.

Wish List
---------

Some things this project would definitely need to be more complete.

- Automated integration tests
- Additional unit testing, particularly around sound of the support classes like the RequestHandledLoggingFilter
- Better DI. HK2 is seriously sub-par. The simple "Service/Contract" approach seems underpowered compared to Spring or Guice. I have not used Dagger, but the compile-time nature of it appeals to me. Otherwise, Guice is a dependable choice.
- A Gradle task that would allow you to pass through config values with system properties.