package com.jenson.my.coin.controller

import com.jenson.my.coin.domain.MessageVM
import com.jenson.my.coin.service.MessageService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
@MessageMapping("api.v1.messages")
class MessageResource(private val messageService: MessageService) {

    @MessageMapping("stream")
    suspend fun receive(@Payload inboundMessages: Flow<MessageVM>) =
            messageService.post(inboundMessages)

    @MessageMapping("stream")
    fun send(): Flow<MessageVM> = messageService
            .stream()
            .onStart {
                emitAll(messageService.latest())
            }
}