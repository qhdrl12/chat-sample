package com.jenson.my.coin.extension

import com.jenson.my.coin.domain.MessageVM
import com.jenson.my.coin.domain.UserVM
import com.jenson.my.coin.entity.ContentType
import com.jenson.my.coin.entity.Message
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

fun MessageVM.asDomainObject(contentType: ContentType = ContentType.MARKDOWN): Message = Message(
        content,
        contentType,
        sent,
        user.name,
        user.avatarImageLink.toString(),
        id
)

fun MessageVM.asRender(contentType: ContentType = ContentType.MARKDOWN): MessageVM =
        this.copy(content = contentType.render(this.content))

fun Message.asViewModel(): MessageVM = MessageVM(
        contentType.render(content),
        UserVM(username, URL(userAvatarImageLink)),
        sent,
        id
)

fun Flow<Message>.mapToViewModel(): Flow<MessageVM> = map { it.asViewModel() }

fun ContentType.render(content: String): String = when (this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavor = CommonMarkFlavourDescriptor()
        HtmlGenerator(content,
                MarkdownParser(flavor).buildMarkdownTreeFromString(content),
                flavor
        ).generateHtml()
    }
}