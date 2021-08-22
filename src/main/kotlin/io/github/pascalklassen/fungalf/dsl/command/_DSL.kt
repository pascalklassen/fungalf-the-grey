package io.github.pascalklassen.fungalf.dsl.command

@DslMarker
annotation class CommandDsl

@CommandDsl
open class CommandHandler() {
    private val commandList = mutableListOf<Command>()

    fun command(name: String, block: CommandBuilderDsl.() -> Unit) {
        CommandBuilderDsl(name).apply(block)
            .also { commandList.add(Command(name)) }
    }

    class Command(name: String)

    @CommandDsl
    class CommandBuilderDsl(val name: String)
}

class CommandHandlerList(vararg handlers: CommandHandler) {
}

class PokeCordCommand: CommandHandler() {

    init {
        command("test") {

        }
    }
}
