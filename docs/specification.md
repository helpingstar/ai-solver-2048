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
•	현재 보드를 빠르게 입력한 뒤 다음 추천 방향 확인
•	특정 방향 적용 후 결과 보드를 수동으로 계속 조정
•	여러 가설 보드를 만들어 AI 추천 비교
•	잘못 입력한 값을 빠르게 수정하고 Undo로 복구

⸻

5. 핵심 기능 요약

5.1 보드 편집
•	빈 4x4 board에서 시작
•	셀 탭으로 선택
•	하단 툴바에서 값 수정
•	빠른 수정과 정확한 값 선택을 함께 지원

5.2 방향 적용
•	swipe
•	방향 버튼
•	추천 결과 row tap

위 세 방식으로 방향 적용 가능하다. 단, 셀 선택 상태에서는 모든 move trigger가 비활성화된다.

5.3 AI 추천
•	4방향 ranking 제공
•	best move 확률 제공
•	기대 최종 score 제공
•	결과는 하단 카드에 노출
•	보드 위에는 best move만 약하게 강조

5.4 Undo/Reset
•	Undo는 transaction 단위
•	Reset은 전체 board 초기화

⸻

6. 화면 구조

본 앱은 단일 화면(single workspace) 으로 구성한다.

6.1 상단 Header

포함 요소:
•	Score (read-only, session 값)
•	Best tile
•	Undo
•	Reset

제외 요소:
•	Move count
•	Score 편집

Score 정책
•	Score는 앱 내부 session 값으로 유지된다.
•	수동 보드 편집 이후에도 그대로 유지된다.
•	따라서 Score는 현재 board의 절대적 진실이라기보다, 앱 내 진행 문맥 정보로 해석한다.

Best tile 정책
•	Best tile은 현재 board 상태에서 계산되는 값이다.

6.2 중앙 Board 영역
•	4x4 board를 화면 중심에 크게 배치
•	첫 진입 시 완전히 빈 board 표시
•	셀 single tap으로 선택
•	선택된 셀은 시각적으로 강조

셀 선택 상태 규칙
셀 선택 상태에서는 아래 입력이 모두 잠긴다.
•	swipe
•	방향 버튼
•	추천 row tap

선택 해제는 바깥 영역 탭 등으로 수행한다.

6.3 하단 AI 결과 카드

하단 고정 카드로 구성한다.

포함 정보:
•	Best move 1개 강조 표시
•	4방향 ranking 리스트
•	각 방향의 best move 확률
•	각 방향의 기대 최종 score

동작 규칙:
•	row tap 시 해당 방향 즉시 적용
•	단, 셀 선택 상태에서는 row tap 비활성화
•	board 상태가 바뀌면 결과 카드는 즉시 비움
•	이후 debounce 자동 분석으로 다시 갱신
•	필요 시 manual Analyze 실행 가능

6.4 하단 컨트롤 영역

하단 컨트롤은 현재 상태에 따라 전환된다.

A. 셀 미선택 상태
노출 요소:
•	방향 버튼
•	Analyze 진입점

허용 동작:
•	swipe
•	방향 버튼
•	추천 row tap

B. 셀 선택 상태
노출 요소:
•	Clear
•	2
•	4
•	x2
•	÷2
•	More

허용 동작:
•	셀 편집만 가능
•	move 관련 조작은 전부 비활성화

⸻

7. 셀 편집 상세 설계

7.1 quick action

선택된 셀에 대해 아래 quick action 제공:
•	Clear
•	2
•	4
•	x2
•	÷2
•	More

의도
•	자주 사용하는 작은 값은 한 번에 빠르게 지정
•	기존 값 수정은 x2 / ÷2로 반복 편집 최적화
•	특수 값/큰 값은 More로 처리

7.2 More bottom sheet

More를 누르면 bottom sheet + power-of-two grid를 연다.

규칙:
•	허용 값만 grid로 표시
•	숫자 직접 입력 없음
•	invalid value는 선택 불가
•	기본 grid는 낮은 값 중심
•	더 보기를 누르면 상위 구간 확장

예시 구조:
•	기본: Empty, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
•	확장: 2048, 4096, 8192, ...

7.3 invalid value 정책
•	3과 같은 값은 아예 입력 불가
•	유효 값은 Empty 또는 2의 거듭제곱만 허용
•	invalid board의 주요 원인은 사후 경고가 아니라 입력 경로 차단으로 줄인다

⸻

8. AI 분석 UX 정책

8.1 분석 갱신 방식
•	board 상태가 변경되면 결과 카드는 즉시 비움
•	이후 debounce 자동 분석으로 추천 결과 재생성
•	사용자는 필요 시 manual Analyze를 실행할 수 있음

8.2 분석 강제 여부
•	사용자는 항상 분석을 요구하지 않을 수 있음
•	따라서 앱은 analyze를 매 단계 강제하지 않음
•	사용자는 분석 없이도 연속 편집 및 방향 적용 가능

8.3 결과 표현 방식
•	정량 정보는 하단 카드에 집중
•	보드에는 best move만 약하게 강조
•	복잡한 수치 정보를 보드 위에 직접 과도하게 올리지 않음

⸻

9. 방향 적용 및 상태 전이 정책

9.1 방향 적용 수단

사용자는 아래 3가지 방식으로 방향을 적용할 수 있다.
•	swipe
•	방향 버튼
•	추천 row tap

9.2 방향 적용 직후 처리

방향 적용 후:
1.	board에 slide/merge 결과 반영
2.	AI 결과 카드 즉시 비움
3.	이후 자동 분석 또는 manual Analyze 가능

9.3 random spawn 정책
•	앱은 random spawn을 자동 생성하지 않음
•	spawn helper도 제공하지 않음
•	pending spawn 상태에 대한 별도 배너/칩/경고도 표시하지 않음

9.4 설계 의도

이 앱은 게임 규칙을 완전히 강제하는 UI가 아니라, 사용자가 원하는 상태를 실험할 수 있는 모델링 도구다. 따라서 move 이후 새 타일 반영은 사용자가 필요할 때 직접 수행한다.

⸻

10. Undo / Reset 정책

10.1 Undo

Undo는 transaction 단위로 동작한다.

예시:
•	셀 1회 수정 = 1 transaction
•	picker에서 값 1회 선택 = 1 transaction
•	방향 1회 적용 = 1 transaction

10.2 Reset

Reset은 전체 board를 초기화한다.

Reset 후 상태:
•	빈 4x4 board
•	결과 카드 비움
•	새 작업 시작 상태

⸻

11. 주요 사용자 흐름

Flow 1. 빈 board에서 특정 상태를 수동으로 만드는 흐름
1.	앱 진입
2.	빈 4x4 board 확인
3.	셀 탭
4.	하단 edit toolbar 표시
5.	2, 4, x2, ÷2, More, Clear로 값 설정
6.	필요한 셀 반복 수정
7.	board 변경 시 결과 카드 비움
8.	debounce 후 AI 결과 갱신

Flow 2. AI 추천을 보고 방향 적용
1.	board 상태 준비
2.	하단 결과 카드에서 4방향 ranking 확인
3.	아래 중 하나로 방향 적용
•	swipe
•	방향 버튼
•	추천 row tap
4.	board에 move 결과 반영
5.	결과 카드 비움
6.	이후 사용자가 계속 편집하거나 추가 move 수행

Flow 3. move 후 새 타일 직접 반영
1.	방향 적용
2.	board가 merge/slide 결과만 반영된 상태가 됨
3.	사용자가 빈 셀 선택
4.	2 또는 4 입력
5.	필요 시 추가 편집
6.	AI 결과 재확인

Flow 4. 큰 값 편집
1.	셀 선택
2.	quick action으로 해결 가능하면 x2 / ÷2
3.	더 큰 값이 필요하면 More
4.	bottom sheet에서 허용 값 선택
5.	board 반영
6.	분석 재실행 또는 자동 갱신 대기

Flow 5. 실수 복구
1.	잘못된 셀 수정 또는 방향 적용 발생
2.	Undo 탭
3.	직전 transaction 복구

⸻

12. UX 설계 결론

이 앱의 UX는 다음 문장으로 요약된다.

빈 4x4 board에서 시작하는 단일 workspace 화면에서, 사용자는 셀을 선택해 빠르게 상태를 수정하고, 선택이 해제되면 swipe·방향 버튼·추천 row tap으로 방향을 적용한다. AI는 하단 카드에서 4방향 ranking과 정량 지표를 제공하지만, 게임 흐름을 강제하지 않고 사용자의 상태 모델링 자유도를 우선한다.

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
•	선택 상태와 비선택 상태의 전환 명확성

14.2 Compose 관점의 주의점
•	board 상태, 선택 상태, 결과 카드 상태는 단일 source of truth에서 관리한다.
•	셀 선택/해제와 하단 toolbar 전환은 recomposition 비용이 낮게 유지되도록 구조화한다.
•	swipe, button, row tap은 동일한 move action으로 수렴시키고, 선택 상태에서는 공통적으로 잠근다.
•	Undo는 transaction 단위 상태 스냅샷 또는 action log 기반으로 일관되게 처리한다.

⸻

15. 최종 범위 요약

포함
•	Android 단독
•	4x4 classic 2048 board
•	단일 workspace 화면
•	수동 보드 편집
•	방향 적용
•	온디바이스 AI 추천
•	4방향 ranking
•	확률 및 기대 최종 score 표시
•	Undo / Reset

제외
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
•	루트 화면 컴포넌트: `workspace/AisolverWorkspaceScreen.kt`
•	상태 모델: `model/AisolverWorkspaceUiModel.kt`
•	샘플 상태: `AisolverWorkspaceSamples.default()`, `selected()`, `valuePicker()`

참고
•	각 UI 컴포넌트 파일에는 Compose Preview가 포함되어 있다.
•	기존 UI를 재사용할 때는 먼저 Preview와 샘플 상태를 확인하고, 그 다음 실제 화면 조합 여부를 본다.

16.3 현재 구현된 화면 조합

현재 `AisolverWorkspaceScreen`은 아래 순서로 화면을 조합한다.
1.	`AisolverWorkspaceHeader`
2.	`AisolverBoard`
3.	`AisolverBestMoveIndicator`
4.	`AisolverAnalysisResultCard`
5.	상태에 따라 `AisolverMoveControls` 또는 `AisolverEditControls`
6.	필요 시 `AisolverValuePickerBottomSheet`

즉, 이후 어떤 피쳐를 붙일 때도 우선 위 조합 구조를 기준으로 어디에 삽입할지 판단한다.

16.4 문서 섹션별 대응 UI

6.1 상단 Header 대응 UI
•	Header 전체 조합: `header/AisolverWorkspaceHeader.kt`
•	Score / Best tile badge: `scorebadge/AisolverScoreBadge.kt`
•	Undo / Reset 버튼: `actionbutton/AisolverActionButton.kt`
•	점수 증가 라벨: `scoreadditionlabel/AisolverScoreAdditionLabel.kt`

이 피쳐를 수정할 때의 기준
•	Header 레이아웃 변경은 `AisolverWorkspaceHeader`에서 시작한다.
•	Score badge 스타일이나 delta 표현 변경은 `AisolverScoreBadge`, `AisolverScoreAdditionLabel`을 우선 수정한다.
•	버튼 공통 스타일은 `AisolverActionButton`을 재사용한다.

6.2 중앙 Board 영역 대응 UI
•	Board 전체 조합: `board/AisolverBoard.kt`
•	보드 배경 surface: `boardsurface/AisolverBoardSurface.kt`
•	정적 grid slot: `boardcell/AisolverBoardCell.kt`
•	grid cell primitive: `gridcell/AisolverGridCell.kt`
•	숫자 타일: `numbertile/AisolverNumberTile.kt`

이 피쳐를 수정할 때의 기준
•	보드 전체 배치, 타일 위치, best move 칩은 `AisolverBoard`에서 조정한다.
•	빈 셀 모양이나 선택 셀 강조는 `AisolverBoardCell`, `AisolverGridCell`을 사용한다.
•	타일 값별 색상, 글자 크기, glow, motion은 `AisolverNumberTile`을 사용한다.

6.3 하단 AI 결과 카드 대응 UI
•	결과 카드 전체: `analysisresultcard/AisolverAnalysisResultCard.kt`
•	방향 ranking row: `moverankingrow/AisolverMoveRankingRow.kt`
•	best move 요약 라벨: `bestmoveindicator/AisolverBestMoveIndicator.kt`

이 피쳐를 수정할 때의 기준
•	카드 구조, 제목, best move 강조 방식은 `AisolverAnalysisResultCard`에서 조정한다.
•	방향별 확률, 기대 score row 표현은 `AisolverMoveRankingRow`를 수정한다.
•	보드 아래의 작은 best move 요약 UI는 `AisolverBestMoveIndicator`를 재사용한다.

6.4 하단 컨트롤 영역 대응 UI
•	Move 상태 컨트롤: `movecontrols/AisolverMoveControls.kt`
•	Edit 상태 컨트롤: `editcontrols/AisolverEditControls.kt`
•	공통 버튼 스타일: `actionbutton/AisolverActionButton.kt`

이 피쳐를 수정할 때의 기준
•	셀 미선택 상태의 방향 버튼 / Analyze는 `AisolverMoveControls`를 사용한다.
•	셀 선택 상태의 Clear / 2 / 4 / x2 / ÷2 / More는 `AisolverEditControls`를 사용한다.
•	새 버튼을 추가하더라도 먼저 `AisolverActionButton`을 재사용 가능한지 확인한다.

7.2 More bottom sheet 대응 UI
•	값 선택 sheet: `valuepickerbottomsheet/AisolverValuePickerBottomSheet.kt`

이 피쳐를 수정할 때의 기준
•	power-of-two 값 선택 UI, Empty 선택, 확장 값 노출은 `AisolverValuePickerBottomSheet`를 사용한다.
•	큰 값 범위 추가나 “더 보기” 동작 조정도 같은 컴포넌트에서 수행한다.

8.3 결과 표현 방식 대응 UI
•	보드 위 약한 best move 강조: `board/AisolverBoard.kt`
•	보드 아래 best move 텍스트 요약: `bestmoveindicator/AisolverBestMoveIndicator.kt`
•	정량 정보 카드: `analysisresultcard/AisolverAnalysisResultCard.kt`

이 피쳐를 수정할 때의 기준
•	보드 위에는 정량 정보를 늘리지 말고 `AisolverBoard` 내부의 약한 강조 수준을 유지한다.
•	정량 정보 확장은 `AisolverAnalysisResultCard`와 `AisolverMoveRankingRow`에서 처리한다.

10. Undo / Reset 정책 대응 UI
•	Undo / Reset 진입 버튼: `header/AisolverWorkspaceHeader.kt`
•	실제 버튼 primitive: `actionbutton/AisolverActionButton.kt`

이 피쳐를 수정할 때의 기준
•	Undo / Reset의 노출 위치를 유지하려면 `AisolverWorkspaceHeader`를 사용한다.
•	동작 연결은 상태/이벤트 계층에서 하되, 버튼 자체를 새로 만들지 않는다.

16.5 재사용 우선순위

새 피쳐를 붙일 때는 아래 순서를 따른다.
1.	먼저 `AisolverWorkspaceUiModel`에 필요한 상태가 이미 있는지 확인한다.
2.	화면 배치 문제인지, 개별 컴포넌트 문제인지 구분한다.
3.	화면 배치 문제면 `AisolverWorkspaceScreen`을 수정한다.
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
