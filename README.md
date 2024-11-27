# ITM 3학년 Mobile Programming Term Project

[figma](https://www.figma.com/proto/M76afk3g2AssHcwJyux7Ja/MP_proposal_UI%2FUX?node-id=0-1&t=sxta8GZ4YicMlArw-1)

## Project Structure

```
com/
└── example/
    └── moneyhub/
        ├── activity/
        │   ├── login/
        │   │   ├── LoginActivity.kt
        │   │   └── LoginViewModel.kt
        │   ├── signup/
        │   │   ├── SignUpActivity.kt
        │   │   └── SignUpViewModel.kt
        │   ├── main/
        │   │   ├── MainActivity.kt
        │   │   └── MainViewModel.kt
        │   ├── mypage/
        │   │   ├── MyPageActivity.kt
        │   │   └── MyPageViewModel.kt
        │   ├── board/
        │   │   ├── PostOnBoardActivity.kt
        │   │   └── PostOnBoardViewModel.kt
        │   ├── create/
        │   │   ├── CreateActivity.kt
        │   │   └── CreateViewModel.kt
        │   ├── camera/
        │   │   ├── CameraActivity.kt
        │   │   └── CameraViewModel.kt
        │   └── register/
        │       ├── RegisterDetailsActivity.kt
        │       └── RegisterDetailsViewModel.kt
        ├── adapter/
        │   ├── BoardRecyclerAdapter.kt
        │   ├── HomePagerAdapter.kt
        │   ├── MemberAdapter.kt
        │   └── TransactionRecyclerAdapter.kt
        ├── common/
        │   └── UiState.kt
        ├── data/
        │   ├── model/
        │   │   ├── Post.kt
        │   │   ├── Comment.kt
        │   │   ├── Group.kt
        │   │   ├── GroupMembership.kt
        │   │   ├── Role.kt (enum)
        │   │   ├── Transaction.kt
        │   │   └── User.kt
        │   └── repository/
        │       ├── auth/
        │       │   ├── AuthRepository.kt
        │       │   ├── AuthRepositoryImpl.kt
        │       │   └── TestAuthRepository.kt
        │       ├── group/
        │       │   ├── GroupRepository.kt
        │       │   ├── GroupRepositoryImpl.kt
        │       │   └── TestGroupRepository.kt
        │       ├── board/
        │       │   ├── BoardRepository.kt
        │       │   ├── BoardRepositoryImpl.kt
        │       │   └── TestBoardRepository.kt
        │       └── transcation/
        │           ├── TransactionRepository.kt
        │           ├── TransactionRepositoryImpl.kt
        │           └── TestTransactionRepository.kt
        ├── di/
        │   └── RepositoryModule.kt
        ├── fragments/
        │   ├── AnalysisFragment.kt
        │   ├── BoardFragment.kt
        │   ├── BudgetFragment.kt
        │   ├── CalendarFragment.kt
        │   ├── HistoryFragment.kt
        │   ├── HomeFragment.kt
        │   └── MembersFragment.kt
        └── ui/
            ├── customs/
            │   └── CustomGreyFormView.kt
            └── theme/
                ├── Color.kt
                ├── Theme.kt
                └── Type.kt
```

## Database Structure

```
├── users/ # 유저가 가입된 그룹 데이터 (나머지는 firebase로 처리)
│   └── {uid}/
│       └── groups: Map<String, String>
│
├── groups/ # 그룹 기본 데이터
│   └── {gid}/
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
│               ├── userId: String
│               ├── userName: String
│               ├── role: String (OWNER/MANAGER/REGULAR)
│               └── joinedAt: Timestamp
│
├── transactions_group/ # 내역/예산 데이터
│   └── {gid}/
│       └── transactions/
│           └── {tid}/
│               ├── name: String
│               ├── category: String
│               ├── type: Boolean (수입:true/지출:false)
│               ├── amount: Number
│               ├── content: String
│               ├── payDateEx: Timestamp (예상 결제일)
│               ├── payDate: Timestamp (실제 결제일)
│               ├── verified: Boolean (내역:true/예산:false)
│               ├── receiptUrl: String?
│               ├── authorId: String
│               ├── authorName: String
│               └── createdAt: Timestamp
│
├── posts_group/ # 게시글 데이터
│   └── {gid}/
│       └── posts/
│           └── {pid}/
│               ├── title: String
│               ├── content: String
│               ├── authorId: String
│               ├── authorName: String
│               ├── commentCount: Number
│               └── createdAt: Timestamp
│
└── comments_group/ # 댓글 데이터
    └── {gid}/
        └── comments_post/
            └── {pid}/
                └── comments/
                    └── {cid}/
                        ├── content: String
                        ├── authorId: String
                        ├── authorName: String
                        ├── replyTo: String?
                        └── createdAt: Timestamp
```