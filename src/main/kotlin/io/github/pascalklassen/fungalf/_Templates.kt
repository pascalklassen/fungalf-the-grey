package io.github.pascalklassen.fungalf

val TEST_TEMPLATE by lazy {
    buildMessage {
        +"Some content!"
        embed {
            title { +"Second embed!" }
            description = "Test"
            +"My test!"
            footer {
                +"Some footer!"
                icon = "https://avatars.githubusercontent.com/u/33351778?v=4"
            }
        }
    }
}
