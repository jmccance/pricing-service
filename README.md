pricing-service
===============

Features
--------

- API implemented with Kotlin and Jersey 2.
- API returns request ids on all responses to facilitate tracing
- API returns standardized error responses that include timestamp, request id, and the status code. (Usually easier to get people to send you the response body they got than it is to get them to send the full )

Usage
-----

The service can be started locally with `gradle run`. By default, the service will bind to `0.0.0.0:8080` using the rate configuration in `conf/rate-config.json`. 

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

Configuration
-------------

Configuration is implemented using [Lightbend Config](https://github.com/lightbend/config). Default configuration is loaded from `reference.conf`, which is found in `main/resources`. Overrides can be handled by specifying an alternate application config file with the `config.file` system property, or by using system properties to override specific configuration fields (e.g., `-Dservice.port=8081`). This makes it straightforward to version your configuration for different environments separately from the application itself.

Note, however, that Gradle really doesn't give you a great way to pass these values down to the forked JVM. At the moment, you can't simply run `gradle run -Dconfig.file=dev.conf` to try a different configuration file. To get this working, we will probably need a custom Gradle task that passes through the appropriate (or all) system properties to the forked JVM.

In the meantime, for your convenience the rates configuration can be set using the `RATE_CONFIG` environment variable.

Dependency Injection
--------------------

TODO Fill in notes on DI.

- Attempted to use Guice, but had issues getting the injection to work as expected. Mostly in trying to inject an ObjectMapper with Guice, kept running into issues with the HK2 injection running first(?), so it would just use the default ObjectMapper provided by the JacksonJsonProvider.
- Settled on using HK2.
- Am aware of the "inhabitant" stuff that reduces boilerplate, but wanted to focus on other things first. 
- Am interested in exploring Dagger, since it's compile-time.

API Specification
-----------------

The [OpenAPI](https://openapis.org/) specification is published to [http://localhost:8080/openapi.yaml](http://localhost:8080/openapi.yaml). Swagger UI is also included, and can be viewed at [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/).

### Notes

The API is not generated from the JAX-RS resources, even though that is something offered by the Swagger libraries. This was disabled by setting `readAllResources` in `openapi-configuration.yaml` to `false`. My main reason for this is that I believe the contract should determine the implementation, not the other way around. This makes it easier to rapidly prototype the API design and share it with other teams before writing any code.

In an ideal world, I think the interfaces for the resources should be generated from the OpenAPI, but that was not something I wanted to tackle for this project. Instead, I settled for manually specifying the spec. I stuck with the swagger-jaxrs2 dependency, though, because it provides the content-negotiation piece. I'm able to write the YAML spec and let the library deal with converting that to JSON as required by the consumers of the API.

TODO
----

- Write automated tests for the Paw requests you're manually testing with now.
- Add request/response logging

### Documentation

- Document how to run the tests
- Document any known issues or areas of improvement