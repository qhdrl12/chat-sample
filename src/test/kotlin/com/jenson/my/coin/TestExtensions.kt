package com.jenson.my.coin

import com.jenson.my.coin.domain.MessageVM
import com.jenson.my.coin.entity.Message
import java.time.temporal.ChronoUnit

fun MessageVM.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(ChronoUnit.MILLIS))

fun Message.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(ChronoUnit.MILLIS))