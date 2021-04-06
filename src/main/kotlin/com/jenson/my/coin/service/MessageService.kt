package com.jenson.my.coin.service

import com.jenson.my.coin.domain.MessageVM
import kotlinx.coroutines.flow.Flow


interface MessageService {
    fun latest(): Flow<MessageVM>

    fun after(lastMessageId: String): Flow<MessageVM>

    fun stream(): Flow<MessageVM>

    suspend fun post(message: Flow<MessageVM>)
}