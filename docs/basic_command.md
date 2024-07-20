### [~~<⠀⠀~~ 상위 페이지로 돌아가기](../README.md)
# Devain Discord Wrapper API 문서

## 명령어 만들기 - 기초

해당 문서는 Devain Discord Wrapper를 사용하여 기초적인 디스코드 명령어를 만드는 방법을 설명합니다.

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
            hook.sendMessage("Hello, ${event.getOption("message")!!.asString()}!").queue()
        }
    }
    
}
```

4. 명령어를 등록합니다.

```kotlin
// 추가적인 디스코드 빌더에서의 환경 설정이 필요하다면, 파라미터의 람다로 조율이 가능합니다.
DiscordWrapper.withToken("YOUR_BOT_TOKEN")
    .registerCommands(MyDiscordCommand())
    .finishSetup()
```

