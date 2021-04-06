package com.jenson.my.coin

import app.cash.turbine.test
import com.jenson.my.coin.domain.MessageVM
import com.jenson.my.coin.domain.UserVM
import com.jenson.my.coin.repository.MessageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlow
import java.net.URI
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.ExperimentalTime

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = [
            "spring.r2dbc.url=r2dbc:h2:mem:///testdb;USER=sa;PASSWORD=password"
        ]
)
class ChatKotlinApplicationTests(
        @Autowired val rsocketBuilder: RSocketRequester.Builder,
        @Autowired val messageRepository: MessageRepository,
        @LocalServerPort val serverPort: Int
) {
    val now: Instant = Instant.now()

    @InternalCoroutinesApi
    @ExperimentalTime
    @ExperimentalCoroutinesApi
    @Test
    fun `test that messages API stream latest messages`() {
        runBlocking {
            val rSocketRequester = rsocketBuilder.websocket(URI("ws://localhost:${serverPort}/rsocket"))

            rSocketRequester
                    .route("api.v1.messages.stream")
                    .retrieveFlow<MessageVM>()
                    .test {
                        assertThat(expectItem().prepareForTesting())
                                .isEqualTo(
                                        MessageVM(
                                                "*testMessage*",
                                                UserVM("test", URL("http://test.cpom")),
                                                now.minusSeconds(2).truncatedTo(ChronoUnit.MILLIS)
                                        )
                                )
                        assertThat(expectItem().prepareForTesting())
                                .isEqualTo(
                                        MessageVM(
                                                "<body><p><strong>testMessage2</strong></p></body>",
                                                UserVM("test1", URL("http://test.com")),
                                                now.minusSeconds(1).truncatedTo(ChronoUnit.MILLIS)
                                        )

                                )
                        assertThat(expectItem().prepareForTesting())
                                .isEqualTo(
                                        MessageVM(
                                                "<body><p><code>testMessage3</code></p></body>",
                                                UserVM("test2", URL("http://test.com")),
                                                now.truncatedTo(ChronoUnit.MILLIS)
                                        )
                                )

                        expectNoEvents()

                    }
        }
    }
}