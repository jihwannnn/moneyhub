pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}


//Kotlin DSL로 작성된 Gradle 설정 코드.
// JitPack이라는 Maven 저장소를 프로젝트의 의존성 저장소 목록에 추가하는 코드
//url = uri("https://jitpack.io"): 해당 Maven 저장소의 URL을 지정
//JitPack은 GitHub 프로젝트들을 Maven 의존성으로 사용할 수 있게 해주는 서비스입니다.

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }  // 이 줄을 추가
    }
}



rootProject.name = "MoneyHub"
include(":app")





