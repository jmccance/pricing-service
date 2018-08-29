pricing-service
===============

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

- Document curl requests for validation
- Document how to run with a different JSON config
- Document how to run the tests
- Document any known issues or areas of improvement