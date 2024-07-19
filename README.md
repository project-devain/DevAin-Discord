# Devain Discord Wrapper

Devain Discord Wrapper은 JDA 프레임워크를 통한 Devain 프로젝트에서의 Discord 명령어 연동을 담당하는 라이브러리입니다.

리플렉션을 통해 더욱 편리한 디스코드 명령어의 제작이 가능합니다.

## 종속성 추가
build.gradle에 다음과 같이 추가합니다 :

```groovy
repositories {
   maven {
     url "https://repo.trinarywolf.net/releases" 
   }
}

depdendencies {
    implementation "skywolf46:devain-discord-jda:1.8.0"
}
```

## 사용법

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