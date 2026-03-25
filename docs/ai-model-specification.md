# Game2048 모델 Import 및 앱 실행 요구 정보 응답서


본 문서는 현재 저장소의 산출물과 코드 기준으로, 앱에서 `game2048_policy_value_float32.tflite`를 안정적으로 연동하기 위해 필요한 정보를 정리한 기술 응답 문서다.


권위 기준은 아래 순서로 본다.


1. export된 실제 `.tflite` 산출물
2. `game2048/manifest.yaml`
3. 2048 환경 및 학습 코드


주의:


- 학습용 보조 코드에는 다른 `goal` 기본값이 남아 있는 파일도 있다.
- 앱 연동 시에는 export된 실제 artifact와 `game2048/manifest.yaml`을 최종 권위 기준으로 본다.


## P0 판정 요약


현재 repo 기준으로 아래 항목은 확정 가능하다.


- 모델 파일명/형식/버전
- 입력 shape/dtype/layout
- `goal=131072`, `board_goal=17`
- 입력 정규화 수식
- 출력 tensor 개수와 의미
- action index 순서
- `policy`가 raw logits인지 여부


현재 repo 기준으로 아래 항목은 아직 앱 정책이 필요하다.


- `131072` 초과 입력 처리 정책
- impossible board/invalid board 허용 정책
- all-invalid UI 정책
- `value` 노출/활용 정책
- asset 경로, delegate, thread, singleton 정책
- 로드 실패/추론 실패/timeout/fallback UI 정책
- 표시용 반올림 규칙


따라서 모델 자체 연동은 시작 가능하지만, 앱 정책이 필요한 몇 개의 P0 항목은 아직 문서만으로 완전히 확정되지는 않는다.


---


## [모델 기본 정보]


상태: 확정


답변:


```text
- model: game2048_policy_value_float32.tflite
- format: TFLite / LiteRT
- version: v1
- dtype family: float32
- quantization: none
- file size: 2,713,716 bytes (about 2.59 MiB)
- input count: 1
- output count: 2
```


근거:


- `game2048/manifest.yaml`
- `game2048/artifacts/game2048_policy_value_float32.tflite`
- 실제 TFLite interpreter metadata 확인 결과


비고:


- manifest version은 `1`이다.
- quantization parameter는 비어 있으므로 quantized model이 아니다.


---


## [입력 tensor 계약]


상태: 확정


답변:


```text
- logical input name in manifest: observation
- signature input alias: args_0
- raw TFLite tensor name: serving_default_args_0:0
- input shape: [1, 1, 4, 4]
- input dtype: float32
- input layout: NCHW
- fixed batch: true
- dynamic shape: false
- shape signature: [1, 1, 4, 4]
```


반드시 확정해야 하는 shape 판정:


```text
float32[1, 1, 4, 4]
```


근거:


- `game2048/manifest.yaml`
- 실제 TFLite interpreter `get_input_details()` 확인 결과


비고:


- 앱은 `NHWC [1, 4, 4, 1]`가 아니라 `NCHW [1, 1, 4, 4]`로 버퍼를 만들어야 한다.
- 모델 입력은 "이미 전처리/정규화가 끝난 float32"가 전제다.


---


## [입력 정규화 규칙]


상태: 확정


답변:


```text
- goal: 131072
- board_goal: 17
- model expects: exponent board normalized by board_goal
- zero meaning: empty cell
- app board literal value -> exponent conversion: required if app stores literal tiles
- exponent board -> normalized float: required
```


수식:


```text
exponent =
0                    if tile == 0
log2(tile)           otherwise


normalized =
exponent / board_goal
```


현재 export artifact의 상수:


```text
goal = 131072
board_goal = log2(131072) = 17
normalized = exponent / 17
```


입력 보드 변환 규칙:


```text
- app board value: literal tile value or exponent value 둘 다 가능
- 단, 모델은 최종적으로 exponent-normalized float를 기대한다
- literal tile value를 쓰는 앱이면 tile -> exponent -> normalized float 순서가 필요하다
- exponent value를 이미 쓰는 앱이면 exponent / 17만 하면 된다
```


배치 및 인덱스 배치 규칙:


```text
- tensor write order: row-major
- top-left: [row=0, col=0]
- row increases downward
- col increases rightward
- if app uses a flat 0..15 index:
 row = cellIndex / 4
 col = cellIndex % 4
```


근거:


- `rl-application-gym-game2048/gym-game2048/gym_game2048/envs/game2048.py`
- `rl-application-gym-game2048/gym-game2048/gym_game2048/wrappers/normalize_2048.py`
- `game2048/manifest.yaml`


비고:


- 2048 환경의 관측값은 literal tile이 아니라 exponent board다.
- 예를 들어 literal `2, 4, 8, 16`은 exponent `1, 2, 3, 4`가 된다.
- literal `131072`은 exponent `17`, normalized `1.0`이다.


---


## [지원 입력 범위]


상태: 미정


답변:


확정된 범위:


```text
- semantic training range:
 - exponent: 0..17
 - literal tile: 0 or powers of two up to 131072
- normalized float range expected by contract: 0.0..1.0
- zero is valid and means empty
- non-zero valid values are powers of two only if app stores literal tiles
```


현재 repo에서 미정인 항목:


```text
- 131072 초과 literal tile 입력 시 정책
- exponent > 17 입력 시 clamp 여부
- invalid board / impossible board 입력 허용 정책
- 음수 입력을 hard error로 볼지 여부
```


현재 repo 기준 판단:


```text
- model은 수치적으로는 shape만 맞으면 float32를 받을 수 있다
- 그러나 contract상 의미 있는 입력은 exponent/17로 정규화된 보드다
- 131072 초과 값은 training distribution 밖(out-of-distribution)이며 정책이 정의되어 있지 않다
```


권장안:


```text
- 131072 초과:
 - 권장 1순위: 앱에서 validation error 처리
 - 권장 2순위: 제품 요구로 반드시 계속 플레이를 허용해야 하면 exponent를 17로 clamp하고 OOD 상태로 로그 남김
- 음수/NaN/Inf:
 - 입력 자체를 막고 Analyze 비활성 또는 에러 처리
- impossible board:
 - 숫자상 추론은 가능하지만 품질 보장을 못 하므로 앱 입력 단계에서 차단 권장
```


근거:


- `game2048/manifest.yaml`의 `goal=131072`
- 2048 환경은 goal 도달 시 종료되므로 학습 관측 분포는 사실상 exponent `<= 17`


비고:


- 이 섹션은 앱 정책 확정이 필요하므로 P0 미완료 항목이다.


---


## [출력 tensor 계약]


상태: 확정


답변:


signature 기준 의미 매핑:


```text
- output 0:
 - name: output_0
 - meaning: policy_logits
 - shape: [1, 4]
 - dtype: float32


- output 1:
 - name: output_1
 - meaning: value
 - shape: [1, 1]
 - dtype: float32
```


raw tensor 기준 이름:


```text
- StatefulPartitionedCall:0 -> policy_logits [1, 4]
- StatefulPartitionedCall:1 -> value [1, 1]
```


근거:


- `game2048/manifest.yaml`
- 실제 TFLite signature runner 실행 결과
- 실제 TFLite interpreter output tensor 확인 결과


비고:


- Python `get_output_details()` 관찰상 리스트 순서는 내부 tensor index 때문에 `[value, policy]`처럼 보일 수 있었다.
- 앱에서는 raw output-details 순서에 의존하지 말고, signature name 또는 shape/name 매핑으로 의미를 확정하는 것이 안전하다.


---


## [출력 index -> direction 매핑]


상태: 확정


답변:


```text
0 = left
1 = right
2 = up
3 = down
```


근거:


- `rl-application-gym-game2048/gym-game2048/gym_game2048/envs/game2048.py`
- `rl-application-gym-game2048/gym-game2048/README.md`


비고:


- 앱 enum은 반드시 이 순서에 맞춰 연결해야 한다.


---


## [출력 의미와 후처리]


상태: 미정


답변:


확정된 항목:


```text
- policy output is raw logits
- policy output is NOT pre-softmax probability
- negative values can appear
- output range is not bounded
- value output is a scalar critic/state-value head
```


현재 코드 기준 추천 후처리:


```text
1. Run model and read policy_logits [1, 4]
2. Compute action mask from current board
3. Apply invalid action mask
4. Softmax over valid actions only
5. Invalid actions become 0f
6. Convert probability to percent by prob * 100f
7. Sort descending for UI
```


왜 이렇게 보는가:


```text
- 학습 코드에서는 invalid action을 logits 단계에서 -1e8로 치환한 뒤 categorical distribution을 만들었다
- 따라서 앱에서 추천 확률을 보여주려면 "mask 후 softmax"가 학습 의도와 가장 가깝다
```


현재 repo에서 미정인 항목:


```text
- final UI rounding rule
- 표시에 사용할 decimal 자리수
- exact sort tie-breaker
```


권장안:


```text
- 내부 계산: float 유지
- UI 표시: 소수점 1자리 반올림 또는 정수 반올림 중 하나를 앱에서 고정
- tie-breaker: index order(left, right, up, down) 유지
```


근거:


- `rl-application-gym-game2048/game2048_network.py`
- root `README.md`의 "Action masking is outside this repository's model contract."


비고:


- 모델 contract는 raw logits까지만 포함하고, UI 표시는 앱 policy 영역이다.


---


## [all-invalid 처리]


상태: 미정


답변:


확정된 사실:


```text
- dead board에서는 legal move/action mask가 [0, 0, 0, 0]이 될 수 있다
- repo에는 all-invalid UI 규칙이 정의되어 있지 않다
```


권장 기본값:


```text
all invalid -> return 4 directions with 0f
```


권장 표시:


```text
- 4개 row 유지
- left/right/up/down 모두 0%
- 추가 텍스트가 가능하면 "No valid move" 또는 "Game over" 표시
```


근거:


- `rl-application-gym-game2048/gym-game2048/gym_game2048/envs/game2048.py`
- root `README.md`의 action masking outside-contract note


비고:


- 이 항목은 앱 UX 정책이므로 모델 repo만으로 확정할 수 없다.


---


## [value 출력 사용 정책]


상태: 미정


답변:


확정된 사실:


```text
- value output exists
- shape is [1, 1]
- dtype is float32
- critic/state value 의미의 scalar다
```


현재 repo에서 미정인 항목:


```text
- 앱 UI에 value를 노출할지 여부
- 추천 순위 계산에 value를 섞을지 여부
- 로그/디버깅 전용으로만 쓸지 여부
```


권장 기본값:


```text
value is not used for ranking in current app
```


권장 사용 방식:


```text
- 1단계: ranking에는 policy만 사용
- value는 debug log 또는 hidden diagnostics로만 저장
```


근거:


- export model 구조상 actor/policy head + critic/value head
- 앱 contract는 현재 별도로 정의되어 있지 않음


비고:


- value는 scale이나 해석이 앱 사용자에게 바로 직관적이지 않을 수 있다.


---


## [로드/실패 처리]


상태: 미정


답변:


repo 기준으로 확정 가능한 최소 사실:


```text
- export artifact path in repo:
 game2048/artifacts/game2048_policy_value_float32.tflite
```


현재 repo에 없는 항목:


```text
- 앱 asset 경로
- noCompress "tflite" 적용 여부
- CPU/GPU/NNAPI delegate 정책
- thread 수 정책
- Interpreter singleton 여부
- load failure 시 UX
- inference failure 시 UX
- timeout 정책
- output shape mismatch 시 fallback 정책
```


권장 기본값:


```text
- asset path: assets/ml/game2048_policy_value_float32.tflite
- noCompress "tflite": true
- delegate: CPU/XNNPACK first
- threads: 1 or 2
- interpreter lifecycle: lazy singleton
- load failure: Analyze disabled + error log
- inference failure: current result clear or 0f fallback + error log
- timeout: cancel current analyze and show unavailable state
- shape mismatch: hard fail + error log
```


근거:


- 현재 저장소는 export sandbox이며 Android app packaging policy는 포함하지 않는다


비고:


- 이 섹션은 앱 구현 저장소에서 별도 확정이 필요하다.


---


## [golden sample]


상태: 확정


답변:


현재 repo에 commit된 golden artifact는 "정규화된 synthetic input + reference outputs"까지만 포함한다.


```text
- committed artifact:
 - game2048/golden/reference_outputs.npz
 - contains: obs, policy_logits, value
 - note: obs는 valid board에서 생성된 샘플이 아니라 synthetic normalized sample이다
```


앱 구현 검증용으로는 아래 valid-board golden sample을 권장한다.


### Golden Sample A: 앱 연동 검증용


literal board:


```text
[
 [2, 4, 8, 16],
 [32, 64, 128, 256],
 [512, 1024, 0, 0],
 [0, 0, 0, 0]
]
```


exponent board:


```text
[
 [1, 2, 3, 4],
 [5, 6, 7, 8],
 [9, 10, 0, 0],
 [0, 0, 0, 0]
]
```


normalized tensor (`board_goal = 17`):


```text
[
 [
   [
     [0.05882353, 0.11764706, 0.1764706,  0.23529412],
     [0.29411766, 0.3529412,  0.4117647,  0.47058824],
     [0.5294118,  0.5882353,  0.0,        0.0],
     [0.0,        0.0,        0.0,        0.0]
   ]
 ]
]
```


raw model output:


```text
policy_logits = [13.144348, -30.904238, -1.9672165, -4.05033]
value = [-1.4313726]
```


action mask:


```text
[0, 1, 0, 1]
```


direction mapping:


```text
0=left, 1=right, 2=up, 3=down
```


valid-only softmax:


```text
valid logits = [-30.904238, -4.05033]
valid probs  = [2.1751826e-12, 1.0]
```


final UI percent example:


```text
left  = 0.0%
right = ~0.0%
up    = 0.0%
down  = 100.0%
```


정렬 결과 예시:


```text
down > right > left = up
```


### Golden Sample B: repo committed synthetic sample


normalized input:


```text
obs shape = [1, 1, 4, 4]
obs =
[[[[0.6369617,  0.26978672, 0.04097353, 0.01652764],
  [0.8132702,  0.91275555, 0.60663575, 0.72949654],
  [0.543625,   0.9350724,  0.81585354, 0.0027385 ],
  [0.8574043,  0.03358557, 0.72965544, 0.17565562]]]]
```


reference outputs:


```text
policy_logits = [[13.570569, -57.13689, 2.0013058, -4.7452374]]
value         = [[-7.9127827]]
```


근거:


- `game2048/golden/reference_outputs.npz`
- exported TFLite model 실행 결과


비고:


- Golden Sample A는 실제 앱 입력 파이프라인 검증용이다.
- Golden Sample B는 repo가 현재 보유한 최소 regression artifact다.


---


## 구현자가 바로 답할 수 있어야 하는 10개 질문에 대한 상태


```text
1. 앱은 어떤 shape와 dtype으로 입력 버퍼를 만들어야 하는가
  -> 확정: float32 [1, 1, 4, 4]


2. 보드 값을 어떤 수식으로 정규화해야 하는가
  -> 확정: exponent / 17


3. 출력 tensor 각각은 어떤 의미인가
  -> 확정: policy_logits [1,4], value [1,1]


4. 출력 index는 어떤 방향을 의미하는가
  -> 확정: 0 left, 1 right, 2 up, 3 down


5. 앱은 softmax를 직접 해야 하는가
  -> 확정에 가까운 권장: yes, invalid mask 적용 후 valid action에 대해서만 softmax


6. invalid action은 어떻게 처리해야 하는가
  -> 미정: repo outside-contract, 권장안은 invalid=0f


7. 최종 UI percent는 어떻게 계산해야 하는가
  -> 미정: prob * 100f는 권장 가능, 반올림 규칙은 앱 정책 필요


8. dead board에서는 무엇을 보여줘야 하는가
  -> 미정: 권장안은 4방향 모두 0%


9. value 출력은 써야 하는가
  -> 미정: 권장안은 ranking 미사용, debug-only


10. 구현이 맞는지 무엇으로 검증할 수 있는가
  -> 확정: Golden Sample A/B로 검증 가능
```


---


## 최종 판정


현재 문서로 모델 import와 기본 추론 구현은 가능하다.


다만 아래 항목은 앱 정책 확정이 추가로 필요하다.


- `131072` 초과 입력 처리
- impossible board 처리
- all-invalid UI 정책
- `value` 활용 여부
- load/failure/timeout/fallback 정책
- percent 표시 반올림 규칙







