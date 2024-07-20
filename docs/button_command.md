### [~~<⠀⠀~~ 상위 페이지로 돌아가기](../README.md)

# Devain Discord Wrapper API 문서

## 명령어 만들기 - 기초

해당 문서는 Devain Discord Wrapper를 사용하여 버튼이 포함된 디스코드 명령어를 만드는 방법을 설명합니다.

### 명령어 구현

1. EnhancedDiscordCommand를 상속받는 클래스를 생성합니다.
```kotlin
class MyDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first discord command!"
)
```


2. 사용할 파라미터를 modifyCommandData 함수를 통해 입력합니다.

```kotlin
class MyDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first discord command!"
) {
    override fun modifyCommandData(options: SlashCommandData) {
        options.addOptions(
            OptionData(OptionType.STRING, "message", "출력할 메시지입니다.", true),
        )
    }
}
```
3. 명령어의 실 작동을 onCommand 함수를 통해 구현합니다.

```kotlin
class MyDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first discord command!"
) {
    override fun modifyCommandData(options: SlashCommandData) {
        options.addOptions(
            OptionData(OptionType.STRING, "message", "출력할 메시지입니다.", true),
        )
    }

    override suspend fun onCommand(event: SlashCommandInteractionEvent) {
        // defer 인라인 함수를 통해 "명령어 처리" 표시를 보냅니다.
        event.defer { _, hook ->
            hook.sendMessage("Hello, ${event.getOption("message")!!.asString()}!")
                // queueComponents 함수는 사용시, 메시지를 보냅니다 (queue).
                .queueComponents(
                    // 이벤트 핸들러가 30초간 유지됩니다.
                    // 봇이 재시작될 시, 이 제한은 해제됩니다.
                    Lifespan.seconds(30),
                    // 이벤트 핸들러가 삭제될 떄, 메시지도 삭제합니다.
                    true,
                    // 이번 예제에서는 버튼 하나만 포함합니다.
                    TextButton(
                        // 버튼의 ID입니다.
                        "my-button-id",
                        // 버튼의 라벨입니다.
                        "Click me!",
                        // 버튼의 색상을 지정합니다.
                        ButtonStyle.PRIMARY
                    ) {
                        // 답장 대기 상태를 보냅니다..
                        it.deferReply().queue { buttonEventHook ->
                            // 버튼을 클릭했을 때, 실행될 코드입니다.
                            buttonEventHook.sendMessage("Button clicked!").queue()
                        }
                    }
                )
        }
    }
}
```

4. 명령어를 콜백과 함께 등록합니다.

```kotlin
// 추가적인 디스코드 빌더에서의 환경 설정이 필요하다면, 파라미터의 람다로 조율이 가능합니다.
DiscordWrapper.withToken("YOUR_BOT_TOKEN")
    .registerCommands(MyDiscordCommand())
    // 버튼 폴백을 지정합니다.
    // 버튼 람다는 일회성으로 제공됨으로, 재시작 후 버튼의 핸들링을 위해 폴백을 추가해야 합니다.
    .fallbackButton("my-button-id") { event ->
        event.deferReply().queue { buttonEventHook ->
            // 버튼을 클릭했을 때, 해당 버튼에 할당된 리스너가 없을 경우 실행될 코드입니다.
            buttonEventHook.sendMessage("Button clicked! (from Fallback Event)").queue()
        }
    }
    .finishSetup()
```

