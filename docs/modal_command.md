### [~~<⠀⠀~~ 상위 페이지로 돌아가기](../README.md?tab=readme-ov-file#%EC%82%AC%EC%9A%A9%EB%B2%95)

# Devain Discord Wrapper API 문서

## 명령어 만들기 - 기초

해당 문서는 Devain Discord Wrapper를 사용하여 모달 기반 디스코드 명령어를 만드는 방법을 설명합니다.

### 모달 기반 명령어 구현

1. EnhancedDiscordCommand를 상속받는 클래스를 생성합니다.
```kotlin
class MyModalDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-modal-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first modal based discord command!",
    // modalId 파라미터는 모달의 고유 ID입니다. 이 값이 null이 아닌 경우, 모달을 사용합니다.
    modalId = "my-unique-modal-id".toOption()
)
```

2. 필요하다면, 사용할 파라미터를 modifyCommandData 함수를 통해 입력합니다.

```kotlin
class MyModalDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-modal-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first modal based discord command!",
    // modalId 파라미터는 모달의 고유 ID입니다. 이 값이 null이 아닌 경우, 모달을 사용합니다.
    modalId = "my-unique-modal-id".toOption()
) {
    override fun modifyCommandData(options: SlashCommandData) {
        options.addOptions(
            OptionData(OptionType.STRING, "suffix", "메시지 끝에 추가될 내용입니다..", true),
        )
    }
}
```
3. 명령어의 실 작동을 onCommand 함수를 통해 구현합니다.
```kotlin
class MyModalDiscordCommand : EnhancedDiscordCommand(
    // 첫번째 파라미터는 명령어 이름입니다. 
    "my-modal-discord-command",
    // 두번째 파라미터는 명령어 설명입니다.
    "My first modal based discord command!",
    // modalId 파라미터는 모달의 고유 ID입니다. 이 값이 null이 아닌 경우, 모달을 사용합니다.
    modalId = "my-unique-modal-id".toOption()
) {
    override fun modifyCommandData(options: SlashCommandData) {
        options.addOptions(
            OptionData(OptionType.STRING, "suffix", "메시지 끝에 추가될 내용입니다..", true),
        )
    }

    override suspend fun onCommand(event: SlashCommandInteractionEvent) {
        event.listenModal(createModal("Modal Title") {
            // 모달에 텍스트 필드를 추가합니다.
            it.addActionRow(TextInput.create(
                // 모달 컴포넌트의 ID입니다.
                "message",
                // 모달 콤포넌트의 제목입니다.
                "Message", 
                // 모달 컴포넌트의 타입입니다.
                TextInputStyle.PARAGRAPH).build())
        }) {
            // 사용자가 입력을 보류하고 모달을 닫은 경우, 이 코드가 실행됩니다.
            if (it.interaction.getValue("message") == null) {
                return@listenModal
            }
            it.defer { modalEvent, hook ->
                // Modal의 이벤트와 SlashCommand의 이벤트는 별개의 존재입니다.
                hook.sendMessage(
                    "Hello, ${modalEvent.interaction.getValue("message")!!.asString()}!" + 
                            (event.getOption("suffix")?.asString() ?: "") 
                ).queue()
            }
        }
    }
    
}
```
4. 명령어를 등록합니다.

```kotlin
// 추가적인 디스코드 빌더에서의 환경 설정이 필요하다면, 파라미터의 람다로 조율이 가능합니다.
DiscordWrapper.withToken("YOUR_BOT_TOKEN")
    .registerCommands(MyModalDiscordCommand ())
    .finishSetup()
```