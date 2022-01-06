package example.request.attribute.bug.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.RequestAttribute
import io.micronaut.http.client.annotation.Client
import reactor.core.publisher.Mono

@Client(id = "example-client")
interface ExampleClient {
    @Get("example")
    fun get(@RequestAttribute("metadata") metadata: Int, @RequestAttribute("metadata1") metadata1: Int): Mono<String>
}
