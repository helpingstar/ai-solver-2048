2048 온디바이스 AI 앱 기획서

1. 문서 개요

1.1 목적

본 문서는 2048 게임 보드 상태를 사용자가 직접 입력/수정하고, 온디바이스 AI가 다음에 어느 방향으로 움직이는 것이 좋은지 추천하는 Android 앱의 제품 기획을 정의한다.

이 앱의 핵심 목적은 일반적인 게임 플레이 최적화가 아니라, 보드 상태를 빠르게 모델링하고 원하는 시점에 AI 추천을 확인할 수 있는 workspace를 제공하는 데 있다.

1.2 제품 정의

이 앱은 전통적인 의미의 단순 게임 앱이 아니라, 다음 역할을 동시에 수행하는 2048 board workspace다.
•	현재 보드 상태 확인
•	셀 단위 수동 편집
•	방향 적용으로 상태 전이 모델링
•	온디바이스 AI 추천 확인
•	실수 복구를 위한 transaction 단위 Undo

1.3 플랫폼 범위
•	플랫폼: Android 단독
•	보드 규격: classic 2048, 4x4 고정
•	입력 방식: 수동 입력 중심
•	AI 실행 위치: 온디바이스

⸻

2. 제품 목표

2.1 핵심 목표
1.	사용자가 보드를 매우 빠르게 수정할 수 있어야 한다.
2.	사용자가 원하는 시점에 AI 추천 방향을 즉시 확인할 수 있어야 한다.
3.	사용자는 일반 게임 플레이처럼 앱의 흐름을 반드시 따를 필요 없이, 원하는 보드 상태를 자유롭게 모델링할 수 있어야 한다.
4.	입력 가능한 값과 조작 규칙은 명확해야 하며, invalid value는 입력 단계에서 차단되어야 한다.

2.2 비목표

다음 항목은 본 기획의 우선 범위에 포함하지 않는다.
•	스크린샷 인식/OCR 기반 보드 추출
•	온라인 기능
•	멀티플레이어
•	iOS/웹 동시 지원
•	변형 보드 크기 지원
•	게임 규칙의 완전 강제

⸻

3. 제품 원칙

3.1 Single Workspace 원칙

Edit mode와 Play mode를 나누지 않는다. 사용자는 한 화면에서 바로 수정하고, 바로 방향을 적용하고, 바로 추천을 확인한다.

3.2 편집 우선 원칙

이 앱은 고득점용 게임보다 상태 모델링 도구에 가깝다. 따라서 accidental move보다 accidental edit를 더 중요하게 방지한다.

3.3 입력 단계 차단 원칙

3 같은 invalid value는 입력 후 경고하지 않는다. 애초에 입력 UI에서 선택 불가하게 만든다.

3.4 AI 보조자 원칙

AI는 흐름 강제자가 아니다. 사용자는 항상 분석 결과를 따를 필요가 없으며, analyze 없이도 계속 상태를 수정하거나 방향을 적용할 수 있다.

3.5 부분 규칙 반영 원칙

2048의 흐름을 참고하되, 앱이 사용자의 모델링 자유도를 지나치게 제약하지 않는다.

⸻

4. 대상 사용자

4.1 주요 사용자
•	2048 보드를 수동으로 재현해 보고 싶은 사용자
•	특정 상태에서 최적 방향이 무엇인지 확인하고 싶은 사용자
•	완성된 AI 모델의 추천 품질을 UX 관점에서 체험하려는 사용자
•	실제 게임 흐름과 무관하게 특정 보드 상태를 실험하려는 사용자

4.2 주요 사용 시나리오
•	현재 보드를 빠르게 입력
•	셀을 탭해 bottom sheet를 열고 값을 수정한 뒤 다른 셀로 곧바로 이동
•	잘못 입력한 값을 Undo로 복구
•	작업 중인 보드를 Reset 후 다시 시작

⸻

5. 핵심 기능 요약

5.1 보드 편집
•	빈 4x4 board에서 시작
•	셀 탭으로 bottom sheet 열기
•	bottom sheet에서 값 선택 후 즉시 반영
•	Phase 1에서는 Clear와 power-of-two 값 선택을 지원

5.2 방향 적용
Phase 1 확장 기준:
•	board swipe로 `Up / Right / Left / Down` move를 적용한다.
•	recommendation row tap으로도 동일한 move action을 적용한다.
•	별도의 move controls row와 방향 버튼은 추가하지 않는다.
•	이동 규칙은 표준 2048의 `slide + merge`를 따른다.
•	random spawn은 자동 생성하지 않는다.
•	move가 실제로 적용되면 score는 즉시 갱신된다.

5.3 AI 추천
Phase 1 기준:
•	하단 카드에 4방향 row를 항상 노출
•	현재 공용 recommendation UI 형태를 그대로 유지
•	각 row는 방향 + confidence percent 형태로 노출
•	초기 placeholder 값으로 `0.0%`를 사용
•	board에 값이 1개 이상 있으면 Analyze 버튼 활성화
•	Analyze 클릭 시 임시 난수 확률 4개를 생성해 합이 `100f`가 되게 정규화
•	리스트는 raw 확률 기준 내림차순으로 정렬
•	화면 표시는 소수 1자리까지 내림
•	Analyze 결과로 순위가 바뀌면 각 row는 이전 위치에서 새 위치로 이동 애니메이션
•	Analyze 결과의 percent 값도 부드럽게 변화
•	edit / undo / reset으로 placeholder로 돌아갈 때는 즉시 반영
•	이 임시 분석은 차후 on-device AI로 대체한다.

5.4 Undo/Reset
•	Undo는 transaction 단위
•	Reset은 전체 board 초기화

⸻

6. 화면 구조

본 앱은 단일 화면(single workspace) 으로 구성한다.

6.1 상단 Header

포함 요소:
•	Score (read-only, session 값)
•	Undo
•	Reset

제외 요소:
•	Move count
•	Score 편집

Score 정책
•	Score는 화면 상단에 read-only로 노출된다.
•	Phase 1의 수동 셀 편집은 score를 직접 변경하지 않는다.
•	Reset 시 score는 0으로 초기화된다.

6.2 중앙 Board 영역
•	4x4 board를 화면 중심에 크게 배치
•	첫 진입 시 완전히 빈 board 표시
•	셀 single tap으로 편집용 bottom sheet 열기
•	선택 강조 overlay는 사용하지 않음

보드 편집 규칙
•	아무 셀이나 탭하면 편집용 bottom sheet가 열린다.
•	bottom sheet에서 `Clear` 또는 값을 선택하면 해당 셀에 즉시 반영되고 sheet는 닫힌다.
•	sheet를 dismiss하면 보드 값은 유지되고 편집 상태만 종료된다.
•	move 애니메이션 진행 중에는 셀 탭과 bottom sheet 진입을 잠근다.

6.3 하단 AI 결과 카드

하단 고정 카드로 구성한다.

포함 정보:
•	4방향 row
•	각 방향의 confidence percent
•	초기 상태에서는 placeholder `0.0%`

동작 규칙:
•	카드는 비어 있지 않고 항상 현재 UI 구조를 유지한다.
•	row tap은 board에 값이 있고 bottom sheet가 열려 있지 않을 때 방향 입력으로 동작한다.
•	board에 값이 있으면 Analyze 버튼이 활성화된다.
•	Analyze 클릭 시 임시 난수 분석 결과로 갱신한다.
•	Analyze 결과로 순위 변경 시 row 이동 애니메이션을 적용한다.
•	Analyze 결과의 percent 값은 부드럽게 변화한다.
•	edit / undo / reset / move로 placeholder로 돌아갈 때는 애니메이션 없이 즉시 반영한다.
•	actual on-device AI 분석 및 best move 강조는 차기 단계에서 구현한다.

6.4 하단 컨트롤 영역

Phase 1에서는 별도의 edit row나 move controls row를 추가하지 않는다.

편집용 bottom sheet
노출 요소:
•	Clear
•	2, 4, 8, 16, 32, 64
•	128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768

허용 동작:
•	셀 편집은 `AisolverBottomSheet`에서만 수행한다.
•	값 선택 시 즉시 반영 후 sheet를 닫는다.
•	별도의 x2 / ÷2 / More placeholder는 노출하지 않는다.

⸻

7. 셀 편집 상세 설계

7.1 value picker bottom sheet

탭한 셀에 대해 아래 bottom sheet 항목 제공:
•	Clear
•	2
•	4
•	8
•	16
•	32
•	64
•	128
•	256
•	512
•	1024
•	2048
•	4096
•	8192
•	16384
•	32768

의도
•	Phase 1에서는 `AisolverBottomSheet` 하나로 모든 수동 편집 경로를 통일한다.
•	입력 가능한 값은 Empty 또는 2의 거듭제곱만 제공한다.
•	항목은 4x4 grid로 가운데 정렬해 노출한다.

7.2 bottom sheet 동작

Phase 1 기준:
•	Material3 `ModalBottomSheet`를 사용한다.
•	drag handle은 Material3 기본 요소를 그대로 사용한다.
•	셀 탭 시 즉시 sheet를 연다.
•	항목 탭 시 해당 셀 값을 반영하고 sheet를 닫는다.
•	scrim 또는 dismiss gesture로 닫으면 값은 변경하지 않는다.

7.3 invalid value 정책
•	3과 같은 값은 아예 입력 불가
•	유효 값은 Empty 또는 2의 거듭제곱만 허용
•	invalid board의 주요 원인은 사후 경고가 아니라 입력 경로 차단으로 줄인다

⸻

8. AI 분석 UX 정책

8.1 분석 갱신 방식
•	Phase 1에서는 on-device AI 대신 임시 난수 분석을 사용한다.
•	결과 카드는 현재 recommendation UI 구조를 계속 유지한다.
•	Analyze는 사용자가 수동으로 실행한다.
•	board 수정, Undo, Reset 시 결과는 다시 placeholder로 초기화된다.

8.2 분석 강제 여부
•	분석은 자동으로 실행하지 않는다.
•	사용자는 필요할 때만 Analyze를 누를 수 있다.
•	수동 셀 편집과 undo/reset은 계속 우선 제공한다.

8.3 결과 표현 방식
•	현재 공용 recommendation 카드의 단일 percent 표현을 유지한다.
•	표시 값은 소수 1자리까지 내림한다.
•	보드 위 best move 강조는 아직 구현하지 않는다.
•	추가 정량 정보는 차기 단계에서 별도 설계한다.

⸻

9. 방향 적용 및 상태 전이 정책

9.1 방향 적용 수단
Phase 1 확장 기준:
•	swipe 구현
•	추천 row tap 구현
•	방향 버튼은 여전히 미구현
•	swipe와 row tap은 동일 move action으로 수렴한다.

9.2 방향 적용 직후 처리
•	move가 성공하면 board와 score를 즉시 최종값으로 갱신한다.
•	recommendation은 현재 순서를 유지한 채 `0.0%` placeholder로 즉시 초기화한다.
•	move 애니메이션 중에는 board tap/swipe, recommendation row tap, Analyze, Undo, Reset, bottom sheet 진입을 모두 잠근다.
•	move가 실제로 일어나지 않으면 board, score, recommendation, undo history를 변경하지 않는다.

9.3 random spawn 정책
•	앱은 random spawn을 자동 생성하지 않음
•	spawn helper도 제공하지 않음
•	move 기능이 있더라도 spawn 관련 UI는 계속 제외한다.

9.4 설계 의도

이 앱은 게임 규칙을 완전히 강제하는 UI가 아니라, 사용자가 원하는 상태를 실험할 수 있는 모델링 도구다. 따라서 move 이후 새 타일 반영은 사용자가 필요할 때 직접 수행한다.

9.5 move 애니메이션 정책
•	일반 이동 타일은 source cell에서 target cell로 슬라이드한다.
•	merge가 발생하면 두 source tile이 같은 target cell로 모인다.
•	source tile 이동이 끝난 뒤 결과 tile이 `Merged` pop 애니메이션으로 나타난다.
•	score 표시는 move 시작 시점에 즉시 최종값으로 갱신한다.

⸻

10. Undo / Reset 정책

10.1 Undo

Undo는 transaction 단위로 동작한다.

예시:
•	셀 1회 수정 = 1 transaction
•	Reset 1회 = 1 transaction
•	swipe 1회 성공 = 1 transaction
•	recommendation row tap 1회 성공 = 1 transaction

추가 규칙:
•	Undo는 board와 score 같은 domain 상태를 복구한다.
•	Undo 후 편집용 bottom sheet는 닫힌 상태를 유지한다.

10.2 Reset

Reset은 전체 board를 초기화한다.

Reset 후 상태:
•	빈 4x4 board
•	score = 0
•	결과 카드는 `0.0%` placeholder 상태 유지
•	새 작업 시작 상태
•	편집용 bottom sheet 닫힘

Reset 자체도 Undo 가능한 transaction으로 취급한다.

⸻

11. 주요 사용자 흐름

Flow 1. 빈 board에서 특정 상태를 수동으로 만드는 흐름
1.	앱 진입
2.	빈 4x4 board 확인
3.	셀 탭
4.	bottom sheet 표시
5.	Clear 또는 원하는 값 선택
6.	필요한 셀 반복 수정
7.	추천 카드는 placeholder 상태 유지

Flow 2. 다른 셀로 편집 대상 전환
1.	한 셀 탭
2.	bottom sheet에서 값 설정
3.	다른 셀 탭
4.	새 bottom sheet가 열림
5.	새 값 설정

Flow 3. 실수 복구
1.	잘못된 셀 수정 발생
2.	Undo 탭
3.	직전 transaction 복구
4.	편집용 bottom sheet는 닫힌 상태 유지

Flow 4. 전체 초기화
1.	보드 편집 후 Reset 탭
2.	board와 score가 초기화됨
3.	필요 시 Undo로 reset 이전 상태 복구

⸻

12. UX 설계 결론

이 앱의 UX는 다음 문장으로 요약된다.

빈 4x4 board에서 시작하는 단일 workspace 화면에서, 사용자는 셀을 탭해 bottom sheet에서 원하는 값을 바로 넣고, Undo / Reset으로 작업을 복구하거나 초기화한다. 또한 board swipe 또는 recommendation row tap으로 `slide + merge` move를 적용하고, 필요할 때 Analyze로 임시 AI 확률을 다시 계산한다. 실제 on-device AI는 다음 단계로 분리한다.

⸻

13. 향후 확장 후보

본 문서 범위에는 포함하지 않지만, 향후 확장 후보는 아래와 같다.
•	스크린샷 기반 보드 인식
•	보드 import/export
•	추천 이유 설명 텍스트
•	추천 결과 히스토리 비교
•	여러 보드 저장/불러오기
•	실험용 variant 지원

⸻

14. 구현 시 유의사항

14.1 UX 우선순위

구현 시 solver보다 먼저 검증해야 할 것은 아래다.
•	셀 수정 속도
•	accidental move 방지
•	결과 카드의 가독성
•	bottom sheet 편집 흐름의 명확성

14.2 Compose 관점의 주의점
•	board 상태, bottom sheet 편집 상태, 결과 카드 상태는 단일 source of truth에서 관리한다.
•	셀 탭과 bottom sheet 열림/닫힘은 recomposition 비용이 낮게 유지되도록 구조화한다.
•	swipe와 row tap은 동일한 move action으로 수렴시킨다.
•	Undo는 transaction 단위 상태 스냅샷 또는 action log 기반으로 일관되게 처리한다.

⸻

15. 최종 범위 요약

포함
•	Android 단독
•	4x4 classic 2048 board
•	단일 workspace 화면
•	수동 보드 편집
•	swipe / recommendation row tap 기반 방향 적용
•	slide + merge
•	Undo / Reset
•	Material3 bottom sheet 기반 셀 편집
•	수동 Analyze 버튼
•	임시 난수 recommendation 결과
•	4방향 row 유지
•	confidence percent 소수 1자리 표시

제외
•	실제 on-device AI 분석
•	자동 spawn
•	spawn helper
•	pending spawn UI 표시
•	Score 수정
•	Move count
•	invalid 숫자 직접 입력
•	mode 분리
•	스크린샷 인식
•	멀티플랫폼 지원

⸻

16. 구현된 UI 매핑 및 재사용 가이드

16.1 목적

본 절은 현재 코드베이스에 이미 구현된 UI를 다시 찾기 위한 인벤토리다.

이후 피쳐 구현 시 같은 역할의 UI를 새로 만들기보다, 먼저 본 절에 적힌 컴포넌트를 찾아 재사용하거나 확장하는 것을 기본 원칙으로 한다.

16.2 탐색 시작점
•	UI 컴포넌트 루트: `ui/src/main/java/io/github/helpigstar/aisolver2048/ui/platform/components`
•	앱 테마 루트: `app/src/main/java/io/github/helpigstar/aisolver2048/ui/theme`
•	루트 화면 컴포넌트: `app/src/main/java/io/github/helpigstar/aisolver2048/ui/workspace/feature/workspace/WorkspaceScreen.kt`
•	상태/액션 모델: `app/src/main/java/io/github/helpigstar/aisolver2048/ui/workspace/feature/workspace/WorkspaceViewModel.kt`
•	workspace domain manager: `app/src/main/java/io/github/helpigstar/aisolver2048/data/workspace/manager/WorkspaceManager.kt`
•	route 진입점: `app/src/main/java/io/github/helpigstar/aisolver2048/ui/workspace/feature/workspace/WorkspaceNavigation.kt`

참고
•	각 UI 컴포넌트 파일에는 Compose Preview가 포함되어 있다.
•	기존 UI를 재사용할 때는 먼저 Preview와 샘플 상태를 확인하고, 그 다음 실제 화면 조합 여부를 본다.

16.3 현재 구현된 화면 조합

현재 `WorkspaceScreen`은 아래 순서로 화면을 조합한다.
1.	`WorkspaceStatusSection`
2.	`AisolverBoard`
3.	`AisolverRecommendationCard`
4.	필요 시 `AisolverBottomSheet` host

즉, 이후 어떤 피쳐를 붙일 때도 우선 위 조합 구조를 기준으로 어디에 삽입할지 판단한다.

16.4 문서 섹션별 대응 UI

6.1 상단 Header 대응 UI
•	Header 조합: `WorkspaceScreen.kt` 내부 `WorkspaceStatusSection`
•	Score 카드: `AisolverScoreCard.kt`
•	Undo / Reset 버튼: `AisolverGameActions.kt`

이 피쳐를 수정할 때의 기준
•	Header 레이아웃 변경은 `WorkspaceScreen.kt`의 `WorkspaceStatusSection`에서 시작한다.
•	Score 스타일 변경은 `AisolverScoreCard.kt`를 우선 수정한다.
•	Undo / Reset 버튼 동작 및 상태는 `AisolverGameActions.kt`와 `WorkspaceViewModel.kt`를 함께 본다.

6.2 중앙 Board 영역 대응 UI
•	Board 전체 조합: `AisolverBoard.kt`
•	숫자 타일: `AisolverTile.kt`
•	셀 tap 처리, swipe 처리: `AisolverBoard.kt`

이 피쳐를 수정할 때의 기준
•	보드 전체 배치와 타일 위치는 `AisolverBoard.kt`에서 조정한다.
•	타일 값별 색상과 글자 크기는 `AisolverTile.kt`를 사용한다.
•	slide / merge motion과 swipe gesture는 `AisolverBoard.kt`, `WorkspaceViewModel.kt`, `WorkspaceManager.kt`를 함께 본다.

6.3 하단 AI 결과 카드 대응 UI
•	결과 카드 전체: `AisolverRecommendationCard.kt`
•	카드 헤더 및 Analyze 버튼: `AisolverRecommendationHeader.kt`
•	방향 row 리스트: `AisolverRecommendationList.kt`
•	개별 row: `AisolverRecommendationItem.kt`

이 피쳐를 수정할 때의 기준
•	카드 구조와 row UI는 공용 컴포넌트 원형을 유지한다.
•	Phase 1에서는 `WorkspaceManager.kt`가 임시 난수 결과를 생성하고 `WorkspaceViewModel.kt`가 이를 상태에 반영한다.
•	순위 이동 애니메이션과 값 변화 애니메이션은 `AisolverRecommendationList.kt`, `AisolverRecommendationItem.kt`에서 처리한다.
•	row tap을 통한 방향 적용 연결은 `WorkspaceScreen.kt`와 `WorkspaceViewModel.kt`에서 제어한다.
•	카드의 활성/비활성 상태만 화면 조합 쪽에서 제어한다.

6.4 하단 컨트롤 영역 대응 UI
•	편집용 bottom sheet: `AisolverBottomSheet.kt`

이 피쳐를 수정할 때의 기준
•	Phase 1에서는 별도 move controls row와 edit row가 없다.
•	수동 보드 편집은 `AisolverBottomSheet.kt` 하나로만 수행한다.
•	sheet 열림/닫힘 상태와 반영 동작은 `WorkspaceScreen.kt`, `WorkspaceViewModel.kt`를 함께 본다.

7.2 bottom sheet 대응 UI
•	편집용 grid sheet: `AisolverBottomSheet.kt`

이 피쳐를 수정할 때의 기준
•	편집 흐름을 바꿀 때는 먼저 `WorkspaceState`와 `WorkspaceAction`의 sheet 상태를 확인한다.
•	그 다음 `WorkspaceScreen.kt`의 bottom sheet host와 `AisolverBottomSheet.kt`를 함께 수정한다.

8.3 결과 표현 방식 대응 UI
•	정량 정보 카드: `AisolverRecommendationCard.kt`
•	방향별 percent row: `AisolverRecommendationItem.kt`

이 피쳐를 수정할 때의 기준
•	Phase 1에서는 보드 위 best move 강조가 없다.
•	현재 percent 기반 row 형태는 유지하고, 표시 값은 소수 1자리까지 내림한다.
•	추가 확장이 필요하면 별도 설계 합의 후 진행한다.

10. Undo / Reset 정책 대응 UI
•	Undo / Reset 진입 버튼: `WorkspaceScreen.kt` 내부 `WorkspaceStatusSection`
•	실제 버튼 UI: `AisolverGameActions.kt`

이 피쳐를 수정할 때의 기준
•	Undo / Reset의 노출 위치를 유지하려면 `WorkspaceStatusSection`을 수정한다.
•	동작 연결은 `WorkspaceViewModel.kt`와 `WorkspaceManager.kt`를 기준으로 본다.
•	move animation 중 입력 잠금 정책을 바꿀 때는 Header 버튼, Board, Recommendation card를 함께 본다.

16.5 재사용 우선순위

새 피쳐를 붙일 때는 아래 순서를 따른다.
1.	먼저 `WorkspaceState`와 `WorkspaceAction`에 필요한 상태가 이미 있는지 확인한다.
2.	화면 배치 문제인지, 개별 컴포넌트 문제인지 구분한다.
3.	화면 배치 문제면 `WorkspaceScreen.kt`를 수정한다.
4.	개별 영역 문제면 해당 영역 컴포넌트를 수정한다.
5.	색상/타이포/크기 같은 공통 스타일 문제면 먼저 `app/ui/theme`와 `foundation/AisolverDesignSystem.kt`를 수정한다.

16.6 Preview / Demo 전용 구현

아래 UI는 구현되어 있지만 현재는 데모/재사용 관점에서 먼저 존재하는 컴포넌트다.
•	게임 종료/승리 오버레이: `gamemessageoverlay/AisolverGameMessageOverlay.kt`

주의
•	이 오버레이는 현재 Preview와 개별 컴포넌트 재사용 기준으로 구현되어 있다.
•	실제 workspace 화면에 바로 붙어 있지 않으므로, 관련 피쳐를 구현할 때는 이 컴포넌트를 연결해서 사용한다.

16.7 현재 구현 기준에 대한 해석

현재 코드베이스의 UI는 원본 2048 웹 화면을 그대로 복제한 구조가 아니라, 본 문서의 single workspace 구조를 기준으로 구현되어 있다.

따라서 이후 작업에서는 아래처럼 해석한다.
•	workspace 레이아웃 재사용 기준 문서: 본 문서
•	색감, 타일 스타일, 전반적 시각 언어 참고 문서: `docs/ui-specification.md`
•	실제 재사용 대상 UI 기준: 본 절에 적힌 `Aisolver*` 컴포넌트들
