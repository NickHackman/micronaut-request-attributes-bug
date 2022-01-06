package example.request.attribute.bug
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import example.request.attribute.bug.client.ExampleClient
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertTimeout
import java.time.Duration

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ExampleRequestAttributeBugTest : TestPropertyProvider {
    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Inject
    lateinit var client: ExampleClient

    private lateinit var mockServer: WireMockServer

    init {
        properties
    }

    @AfterAll
    fun stopMockServer() {
        mockServer.shutdownServer()
    }

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }

    @Order(1)
    @Test
    fun requestAttributeBug() {
        // setup mock server stub
        mockServer.stubFor(get(urlEqualTo("/example?metadata=1&metadata1=2"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("example")))

        val result = client.get(1, 2).block()

        assertEquals("example", result)
        assertTimeout(Duration.ofSeconds(5)) {
            mockServer.verify(1, getRequestedFor(urlEqualTo("/example?metadata=1&metadata1=2")))
        }
    }

    @Order(2)
    @Test
    fun expectedRequestAttribute() {
        // setup mock server stub
        mockServer.stubFor(get(urlEqualTo("/example"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("example")))

        val result = client.get(1, 2).block()

        assertEquals("example", result)
        assertTimeout(Duration.ofSeconds(5)) {
            mockServer.verify(1, getRequestedFor(urlEqualTo("/example")))
        }
    }

    override fun getProperties(): MutableMap<String, String> {
        mockServer = WireMockServer(WireMockConfiguration().dynamicPort())
        mockServer.start()

        return mutableMapOf(
            "micronaut.http.services.example-client.url" to "http://localhost:${mockServer.port()}/",
        )
    }
}
