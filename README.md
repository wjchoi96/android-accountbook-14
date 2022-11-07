# android-accountbook-14

### Account Book - Kotlin

|     Sample     | Description |
| ------------- | ------------- |
| [main](https://github.com/woowa-techcamp-2022/android-accountbook-14/tree/main) | 코드 프리징 |
| [dev](https://github.com/woowa-techcamp-2022/android-accountbook-14/tree/dev) | 개발용 |
| feature | 기능 개발 |
| refactor | 리팩토링 |
| fix | 문제 수정 |

### 프로젝트 목표

- SQLiteOpenHelper로 DB 구축
- Jetpack Compose로 일부 View 구성
- 의존성 프레임워크 적용(Hilt)
- DummyData로 시작해서, DataSource구현체를 DataBase로 변경만 해도 잘 작동되도록 layer분리
- TestCode 작성


### Tech Stack

- Minumum SDK 21
- Kotlin
- Coroutines
- Dagger-Hilt
- AndroidX Jetpack
    - LiveData
- Material Components
- Timber
- Jetpack Compose

### DB
- [Wiki](https://github.com/woowa-techcamp-2022/android-accountbook-14/wiki/DataBase)


### Package

```
📂app
 ┣ 📂application
 ┣ 📂data
 ┃ ┣ 📂datasource
 ┃ ┣ 📂entity
 ┃ ┗ 📂repository
 ┣ 📂di
 ┣ 📂domain
 ┃ ┣ 📂datasource
 ┃ ┣ 📂model
 ┃ ┣ 📂repsotiroy
 ┃ ┗ 📂usecase
 ┣ 📂presentaion
 ┃ ┣ 📂activity
 ┃ ┣ 📂compose
 ┃ ┣ 📂fragment
 ┃ ┗ 📂viewmodel
 ┗ 📂util
```
