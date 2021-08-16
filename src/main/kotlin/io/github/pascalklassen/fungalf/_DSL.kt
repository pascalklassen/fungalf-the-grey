@file:Suppress("unused")

package io.github.pascalklassen.fungalf

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.time.temporal.TemporalAccessor

@DslMarker
annotation class MessageDsl

@MessageDsl
open class MessageBuilderDsl(template: MessageBuilderDsl? = null): MessageBuilder(template) {
    private val embedList = mutableListOf<MessageEmbed>()

    operator fun String.unaryPlus() {
        append(this + "\n")
    }

    fun embed(template: EmbedBuilderDsl? = null, block: EmbedBuilderDsl.() -> Unit) {
        buildEmbed(template, block).build().also { embeds.add(it) }
    }

    fun assemble() {
        setEmbeds(embedList.apply { addAll(embeds) })
    }
}

@MessageDsl
open class EmbedBuilderDsl(template: EmbedBuilderDsl? = null): EmbedBuilder(template) {
    var description: String? = null
        set(value) {
            setDescription(value)
        }

    var image: String? = null
        set(value) {
            setImage(value)
        }

    var timestamp: TemporalAccessor? = null
        set(value) {
            setTimestamp(value)
        }

    var thumbnail: String? = null
        set(value) {
            setThumbnail(value)
        }

    var color: Color? = null
        set(value) {
            setColor(value)
        }

    operator fun String.unaryPlus() {
        appendDescription(this + "\n")
    }

    fun title(block: TitleBuilderDsl.() -> Unit) {
        TitleBuilderDsl().apply(block).also { setTitle(it.text, it.url) }
    }

    @MessageDsl
    class TitleBuilderDsl(var text: String? = null, var url: String? = null) {

        operator fun String.unaryPlus() {
            text = this
        }
    }

    fun field(block: FieldBuilderDsl.() -> Unit) {
        FieldBuilderDsl().apply(block).also {
            if (it.name == null && it.value == null) {
                addBlankField(it.inline)
            } else {
                addField(it.name, it.value, it.inline)
            }
        }
    }

    @MessageDsl
    class FieldBuilderDsl(var name: String? = null, var value: String? = null, var inline: Boolean = false) {

        operator fun String.unaryPlus() {
            value = this
        }
    }

    fun author(block: AuthorBuilderDsl.() -> Unit) {
        AuthorBuilderDsl().apply(block).also { setAuthor(it.name, it.url, it.icon) }
    }

    @MessageDsl
    class AuthorBuilderDsl(var name: String? = null, var url: String? = null, var icon: String? = null)

    fun footer(block: FooterBuilderDsl.() -> Unit) {
        FooterBuilderDsl().apply(block).also { setFooter(it.text, it.icon) }
    }

    @MessageDsl
    class FooterBuilderDsl(var text: String? = null, var icon: String? = null) {

        operator fun String.unaryPlus() {
            text = this
        }
    }
}

fun buildEmbed(template: EmbedBuilderDsl? = null, block: EmbedBuilderDsl.() -> Unit) =
    EmbedBuilderDsl(template).apply(block)

fun buildMessage(template: MessageBuilderDsl? = null, block: MessageBuilderDsl.() -> Unit) =
    MessageBuilderDsl(template).apply {
        block()
        assemble()
    }

fun createMessage(template: MessageBuilderDsl? = null, block: MessageBuilderDsl.() -> Unit) =
    buildMessage(template, block).build()
