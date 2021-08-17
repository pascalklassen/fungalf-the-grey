@file:Suppress("unused")

package io.github.pascalklassen.fungalf

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Emoji
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.*
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu
import java.awt.Color
import java.time.temporal.TemporalAccessor

@DslMarker
annotation class MessageDsl

typealias ClickHandler = (ButtonClickEvent) -> Unit

@MessageDsl
class MessageBuilderDsl(template: MessageBuilderDsl? = null): MessageBuilder(template) {
    private val embedList = mutableListOf<MessageEmbed>()
    private val actionRowList = mutableListOf<ActionRow>()

    operator fun String.unaryPlus() {
        append(this + "\n")
    }

    fun embed(template: EmbedBuilderDsl? = null, block: EmbedBuilderDsl.() -> Unit) {
        buildEmbed(template, block).build().also { embeds.add(it) }
    }

    fun code(language: String, block: CodeBlockBuilderDsl.() -> Unit) {
        CodeBlockBuilderDsl(language).apply(block).also { appendCodeBlock(it.text, language) }
    }

    @MessageDsl
    class CodeBlockBuilderDsl(var language: String, var text: String? = null) {

        operator fun String.unaryPlus() {
            text = this
        }
    }

    fun actionRow(block: ActionRowBuilderDsl.() -> Unit) {
        ActionRowBuilderDsl().apply(block).also { actionRowList.add(ActionRow.of(it.components)) }
    }

    @MessageDsl
    class ActionRowBuilderDsl(val components: MutableList<Component> = mutableListOf()) {

        fun button(id: String, block: ButtonBuilderDsl.() -> Unit) {
            ButtonBuilderDsl(id).apply(block).also {
                val url = it.url
                var button = Button.of(
                    it.style,
                    it.id,
                    it.label,
                    it.emoji
                ).withDisabled(it.disabled)

                if (url != null) {
                    button = button.withUrl(url)
                }

                ButtonHandlerList.addHandler(it.id, it.onClick)

                components.add(button)
            }
        }

        @MessageDsl
        class ButtonBuilderDsl(
            val id: String,
            var style: ButtonStyle = ButtonStyle.PRIMARY,
            var emoji: Emoji? = null,
            var label: String? = null,
            var url: String? = null,
            var disabled: Boolean = false,
            var onClick: ClickHandler = {}
        )

        fun selectMenu(id: String, block: SelectMenuBuilderDsl.() -> Unit) {
            SelectMenuBuilderDsl(id).apply(block).also {
                components.add(
                    SelectionMenu.create(id)
                        .setPlaceholder(it.placeholder)
                        .setRequiredRange(it.range.first, it.range.last)
                        .addOptions(it.options)
                        .build()
                )
            }
        }

        @MessageDsl
        class SelectMenuBuilderDsl(
            val id: String,
            var placeholder: String? = null,
            var range: IntRange = 1..1) {
            val options = mutableListOf<SelectOption>()

            fun option(value: String, label: String, block: SelectOptionBuilderDsl.() -> Unit) {
                SelectOptionBuilderDsl(value, label).apply(block).also {
                    options.add(
                        SelectOption.of(label, value)
                            .withEmoji(it.emoji)
                            .withDefault(it.default)
                            .withDescription(it.description)
                    )
                }
            }

            @MessageDsl
            class SelectOptionBuilderDsl(
                val value: String,
                val label: String,
                var description: String? = null,
                var default: Boolean = false,
                var emoji: Emoji? = null
            )
        }
    }

    fun assemble() {
        setEmbeds(embedList.apply { addAll(embeds) })
        setActionRows(actionRowList)
    }
}

@MessageDsl
class EmbedBuilderDsl(template: EmbedBuilderDsl? = null): EmbedBuilder(template) {
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

object ButtonHandlerList: ListenerAdapter() {
    private val handlers = mutableMapOf<String, ClickHandler>()

    fun ButtonClickEvent.removeHandler() {
        handlers.remove(componentId)
    }

    fun addHandler(id: String, handler: ClickHandler) {
        handlers[id] = handler
    }

    override fun onButtonClick(event: ButtonClickEvent) {
        handlers[event.componentId]?.let { it(event) }
    }
}
