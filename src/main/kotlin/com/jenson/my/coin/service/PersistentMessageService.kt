package com.jenson.my.coin.service

import com.jenson.my.coin.domain.MessageVM
import com.jenson.my.coin.extension.asDomainObject
import com.jenson.my.coin.extension.asRender
import com.jenson.my.coin.extension.mapToViewModel
import com.jenson.my.coin.repository.MessageRepository
import kotlinx.coroutines.flow.*
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class PersistentMessageService(val messageRepository: MessageRepository) : MessageService {
    val sender: MutableSharedFlow<MessageVM> = MutableSharedFlow()

    override fun latest(): Flow<MessageVM> =
            messageRepository.findLatest().mapToViewModel()

    override fun after(lastMessageId: String): Flow<MessageVM> =
            messageRepository.findLatest(lastMessageId).mapToViewModel()

    override fun stream(): Flow<MessageVM> = sender

    override suspend fun post(messages: Flow<MessageVM>) =
            messages
                    .onEach { sender.emit(it.asRender()) }
                    .map { it.asDomainObject() }
                    .let { messageRepository.saveAll(it) }
                    .collect()
}