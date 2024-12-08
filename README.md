# ITM 3학년 Mobile Programming Term Project - MoneyHub

가계부/예산 관리를 위한 그룹 기반 안드로이드 애플리케이션입니다.

[UI/UX Prototype](https://www.figma.com/proto/M76afk3g2AssHcwJyux7Ja/MP_proposal_UI%2FUX?node-id=0-1&t=sxta8GZ4YicMlArw-1)

## Features

- 👥 그룹 기반 가계부 관리
- 📊 수입/지출 분석
- 📱 영수증 OCR 인식
- 📅 달력 기반 내역 조회
- 📝 게시판 기능
- 👤 멤버 권한 관리

## Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM
- **DI:** Hilt
- **Async:** Coroutines, Flow
- **Database:** Firebase Firestore
- **OCR:** Clova OCR API
- **Libraries:**
    - ViewBinding
    - Material Design Components
    - RecyclerView
    - Glide

## Project Structure

```
com/
└── example/
    └── moneyhub/
        ├── activity/
        │   ├── login/
        │   │   ├── LoginActivity.kt
        │   │   └── LoginViewModel.kt
        │   │
        │   ├── signup/
        │   │   ├── SignUpActivity.kt
        │   │   └── SignUpViewModel.kt
        │   │
        │   ├── main/
        │   │   ├── MainActivity.kt
        │   │   └── MainViewModel.kt
        │   │
        │   ├── mypage/
        │   │   ├── MyPageActivity.kt
        │   │   └── MyPageViewModel.kt
        │   │
        │   ├── board/
        │   │   ├── PostOnBoardActivity.kt
        │   │   ├── ViewOnBoardActivity.kt
        │   │   ├── PostOnBoardViewModel.kt
        │   │   └── ViewOnBoardViewModel.kt
        │   │
        │   ├── creategroup/
        │   │   ├── CreateActivity.kt
        │   │   └── CreateViewModel.kt
        │   │
        │   ├── camera/
        │   │   ├── CameraActivity.kt
        │   │   └── CameraViewModel.kt
        │   │
        │   └── registerdetails/
        │       ├── RegisterDetailsActivity.kt
        │       └── RegisterDetailsViewModel.kt
        │
        ├── adapter/
        │   ├── BoardRecyclerAdapter.kt
        │   ├── HomePagerAdapter.kt
        │   ├── GroupAdapter.kt
        │   ├── MemberAdapter.kt
        │   └── TransactionAdapter.kt
        │
        ├── api/
        │   └── clovaocr/
        │       ├── ClovaOcrApi.kt
        │       ├── OcrResponse.kt
        │       └── RetrofitClient.kt
        │
        ├── common/
        │   └── UiState.kt
        │
        ├── data/
        │   └── repository/
        │       ├── auth/
        │       │   ├── AuthRepository.kt
        │       │   ├── AuthRepositoryImpl.kt
        │       │   └── TestAuthRepository.kt
        │       │
        │       ├── group/
        │       │   ├── GroupRepository.kt
        │       │   ├── GroupRepositoryImpl.kt
        │       │   └── TestGroupRepository.kt
        │       │
        │       ├── board/
        │       │   ├── BoardRepository.kt
        │       │   ├── BoardRepositoryImpl.kt
        │       │   └── TestBoardRepository.kt
        │       │
        │       ├── camera/
        │       │   ├── OcrRepository.kt
        │       │   └── OcrRepositoryImpl.kt
        │       │
        │       └── transaction/
        │           ├── TransactionRepository.kt
        │           ├── TransactionRepositoryImpl.kt
        │           └── TestTransactionRepository.kt
        │
        ├── model/
        │   ├── sessions/
        │   │   ├── CurrentUserSession.kt
        │   │   ├── PostSession.kt
        │   │   └── RegisterTransactionSession.kt
        │   │
        │   ├── Post.kt
        │   ├── Comment.kt
        │   ├── Group.kt
        │   ├── GroupItem.kt
        │   ├── Member.kt
        │   ├── Membership.kt
        │   ├── Role.kt
        │   ├── Transaction.kt
        │   ├── Category.kt
        │   ├── CurrentUser.kt
        │   └── UserGroup.kt
        │
        ├── di/
        │   └── RepositoryModule.kt
        │
        ├── fragments/
        │   ├── analysis/
        │   │   ├── AnalysisFragment.kt
        │   │   └── AnalysisViewModel.kt
        │   │
        │   ├── board/
        │   │   ├── BoardFragment.kt
        │   │   └── BoardViewModel.kt
        │   │
        │   ├── home/
        │   │   ├── HomeFragment.kt
        │   │   └── HomeViewModel.kt
        │   │
        │   ├── member/
        │   │   ├── MembersFragment.kt
        │   │   └── MembersViewModel.kt
        │   │
        │   ├── BudgetFragment.kt
        │   ├── CalendarFragment.kt
        │   ├── HistoryFragment.kt
        │   └── SharedTransactionViewModel.kt
        │
        ├── utils/
        │   └── DateUtils.kt
        │
        └── MoneyHub.kt
```

## Database Structure

```
├── users/ # 유저가 가입된 그룹 데이터 (나머지는 firebase로 처리)
│   └── {uid}/
│       ├── uid: String
│       └── groups: Map<String, String>
│
├── groups/ # 그룹 기본 데이터
│   └── {gid}/
│       ├── gid: String
│       ├── name: String
│       ├── inviteCode: String
│       ├── ownerId: String
│       ├── ownerName: String
│       ├── memberCount: Number
│       └── createdAt: Timestamp
│
├── members_group/ # 그룹 멤버 데이터
│   └── {gid}/
│       └── members/
│           └── {uid}/
│               ├── uid: String
│               ├── gid: String
│               ├── userName: String
│               └── role: String (OWNER/MANAGER/REGULAR)
│
├── transactions_group/ # 내역/예산 데이터
│   └── {gid}/
│       └── transactions/
│           └── {tid}/
│               ├── tid: String
│               ├── gid: String
│               ├── name: String
│               ├── category: String
│               ├── type: Boolean
│               ├── amount: Number
│               ├── content: String
│               ├── payDateEx: Timestamp
│               ├── payDate: Timestamp
│               ├── verified: Boolean
│               ├── receiptUrl: String?
│               ├── authorId: String
│               ├── authorName: String
│               └── createdAt: Timestamp
│
├── posts_group/ # 게시글 데이터
│   └── {gid}/
│       └── posts/
│           └── {pid}/
│               ├── pid: String
│               ├── gid: String
│               ├── title: String
│               ├── content: String
│               ├── authorId: String
│               ├── authorName: String
│               ├── imageUrl: String?
│               ├── commentCount: Number
│               └── createdAt: Timestamp
│
├── comments_group/ # 댓글 데이터
│   └── {gid}/
│       └── comments_post/
│           └── {pid}/
│               └── comments/
│                   └── {cid}/
│                       ├── cid: String
│                       ├── pid: String
│                       ├── gid: String
│                       ├── content: String
│                       ├── authorId: String
│                       ├── authorName: String
│                       ├── replyTo: String?
│                       └── createdAt: Timestamp
│
└── categories/ # 그룹 카테고리 데이터
    └── {gid}/
        ├── gid: String
        └── category: List<String>
```