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
    implementation "skywolf46:devain-discord-jda:1.12.0"
}
```

## 사용법
- [기초적인 명령어 만들기](docs/basic_command.md)
- [모달 기반 명령어 만들기](docs/modal_command.md)
- [명령어에 자동 완성 추가하기](docs/auto_complete_command.md)
- [명령어에 버튼 추가하기](docs/button_command.md)