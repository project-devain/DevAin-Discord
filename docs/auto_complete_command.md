### [~~<⠀⠀~~ 상위 페이지로 돌아가기](../README.md?tab=readme-ov-file#%EC%82%AC%EC%9A%A9%EB%B2%95)

# Devain Discord Wrapper API 문서

## 명령어 만들기 - 자동완성 명령어

해당 문서는 Devain Discord Wrapper를 사용하여 자동 완성이 지원되는 디스코드 명령어를 만드는 방법을 설명합니다.

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
        // addCompletableOption은 OptionData의 함수 아닌, 확장 함수입니다.
        options.addCompletableOption(
            // 파라미터의 이름입니다.
            "test-parameter",
            // 파라미터의 설명입니다.
            "테스트용 파라미터입니다.",
            // 이 파라미터는 반드시 입력해야 합니다.
            true
        ) {
            // 미리 기반 목록을 선언하여 편의성을 높일 수 있습니다.
            listOf("test1", "test2", "test3")
                // filter을 통해 입력된 값으로 시작되는 값만 남깁니다.
                .filter { it.startsWith(event.focusedOption.value) }
                // 디스코드는 25개 이하의 파라미터만 허용함으로, 
                // 어플리케이션 측에서 미리 25개 이하로 제한해야 합니다.
                .let { it.subList(0, it.size.coerceAtMost(25)) }
        }
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
        // addCompletableOption은 OptionData의 함수 아닌, 확장 함수입니다.
        options.addCompletableOption(
            // 파라미터의 이름입니다.
            "test-parameter",
            // 파라미터의 설명입니다.
            "테스트용 파라미터입니다.",
            // 이 파라미터는 반드시 입력해야 합니다.
            true
        ) {
            // 미리 기반 목록을 선언하여 편의성을 높일 수 있습니다.
            listOf("test1", "test2", "test3")
                // filter을 통해 입력된 값으로 시작되는 값만 남깁니다.
                .filter { it.startsWith(event.focusedOption.value) }
                // 디스코드는 25개 이하의 파라미터만 허용함으로, 
                // 어플리케이션 측에서 미리 25개 이하로 제한해야 합니다.
                .let { it.subList(0, it.size.coerceAtMost(25)) }
        }
    }

    override suspend fun onCommand(event: SlashCommandInteractionEvent) {
        // defer 인라인 함수를 통해 "명령어 처리" 표시를 보냅니다.
        event.defer { _, hook ->
            hook.sendMessage("Hello, ${event.getOption("test-parameter")!!.asString()}!").queue()
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

