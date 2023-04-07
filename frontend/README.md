# 내가기린그림 Frontend

## 1. 발생한 이슈 및 해결 과정

### a. TenserFlow 이식 관련 이슈

이식해야 하는 Tenserflow.js 코드가 vanilla js로 되어 있었습니다.  
단순히 React에 가져와 이식하면 된다 생각했으나, 랜더링 과정에서 오류가 발생해 새롭게 바꿨어야 했습니다  
해당 과정에서 크게 두 가지의 React hook에 대한 이해를 높일 수 있었습니다

- useRef()

  - useRef를 사용하여 Tenserflow의 손가락 인식 과정에서 사용되는 video element의 변화를 감지할 수 있었습니다
  - 특정 DOM을 선택해야 하는 상황에서 필요합니다. 특히 video와 canvas element가 있을 때 필수적으로 사용되었습니다

    - video element에 적용 예시

      ```html
      <div className="container">
        <div className="video">
          <div id="video-container">
            <video
              id="pose-video"
              className="layer"
              playsinline
              ref="{video}"
            ></video>
            <canvas id="pose-canvas" className="layer" ref="{canvas}"></canvas>
            <div id="pose-result" className="layer" ref="{resultLayer}"></div>
          </div>
        </div>
      </div>
      ```

- useEffect()

  - Tenserflow의 감지 여부를 게임 결과에 따라 결정하는데, 부모 컴포넌트에서의 state 변화를 useEffect를 사용하여 video component를 제어할 수 있었습니다
  - useEffect가 여러 state에 의존하는 까다로운 상황에서는, if문을 통해 useEffect안에 있는 함수의 실행 여부를 결정하였습니다
    - useEffect 적용 예시
      ```javascript
      useEffect(() => {
        if (isGameEnd) {
          video.current.srcObject.getTracks().forEach(function (track) {
            track.stop();
          });
        }
      }, [isGameEnd]);
      ```

### d. 그림 그리기 및 canvas animation 구현

- `react-canvas-draw` 라이브러리를 활용하여 canvas animation을 구현하였습니다
- 섬네일이 필요한 경우 그렸던 data를 base64 이미지로 변환하여 저장하였습니다
- 동화책 진행 과정에서 사용자가 입력한 그림이 필요했는데, 이야기 진행 중 저장과 동시에 이미지를 불러오는 부분의 구현이 다소 어려웠습니다  
  그래서, redux-persist를 사용하여 임시로 localStorage에 저장하였습니다
- 이번 프로젝트에서는 canvas 컴포넌트가 쓰이는 페이지가 많았습니다  
  그래서 canvas 컴포넌트에 props로 다른 페이지들에 필요한 데이터를 불러와 사용할 수 있도록 수정하였습니다

### e. pageFlip animation 구현

- 한 페이지가 3d로 180도 왼쪽, 오른쪽 방향으로 넘어가는 애니메이션을 구현하였습니다
- 책이 이미 펼쳐진 상태에서 시작해야 했는데, 랜더링이 된 이후 페이지가 넘어가는 함수가 실행되면 책이 랜더링이 되는 순간 페이지가 넘어가게 되었습니다
- `uselayoutEffect`를 이용하여 랜더링 이전 페이지가 넘어갔다고 계산하여, 랜더링 때 이미 페이지가 넘어간 상태로 보이게 하였습니다
- 각 페이지가 쌓이게 되면 성능 이슈가 발생하여, map 함수와 filter를 이용해 현재 사용자가 보게 되는 타켓 page를 제외한 페이지들은 랜더링에서 제외하였습니다

### f. gif 제어 이슈

- 사용자가 저장한 그림책 중 gif가 있는 경우, 그대로 재생되어 페이지 수가 많은 경우 성능 저하 이슈가 있었습니다
- `react-freezeframe` 라이브러리를 이용하여 해당 그림이 랜더링 시 멈추게 하고, hover시 gif가 재생되도록 하였습니다
- 다만 해당 라이브러리 사용시, gif가 의도했던 크기와 맞지 않는 이슈가 있어, styled-component로 하위 컴포넌트의 CSS를 제어하려고 시도하였습니다
- 하지만 해당 컴포넌트의 class가 hover시 바뀌는 경우 제어가 되지 않아, css파일을 말들어 hover 시의 component className을 지정하여 제어하였습니다

## 2. 기술 스택 및 라이브러리 선정 과정과 이유

### a. React

- 책과 같이 고정된 페이지 형식에서 내용이 바뀌게 되는 상황이, SPA를 사용하기에 적절한 예시라고 생각했습니다
- 다양한 라이브러리를 사용할 수 있어 적극적으로 기술 도입을 시도할 수 있었던 것이 매우 큰 장점이었습니다

### b. Redux, Redux-persist

- 책의 내용에서 사용자의 입력이 나중 내용에 영향을 미치는 부분에서, redux의 사용이 매우 큰 도움이 되었습니다
- 부모와 자식 컴포넌트 사이에 state가 자주 이동해야 하는 경우에도 redux를 더 사용하였으면 좋았을 것이라 생각합니다

### c. Tenserflow.js

- 손 인식 모델이 tenserflow에서 잘 구현되어 있고, 특히 js로 활용할 수 있어 프론트앤드가 활용하기 좋은 환경이었습니다 해당 이유들이 선정에 있어 주요한 요인이었습니다

### d. p5.js, react-canvas-draw

- vanilla js 및 react에서 canvas element를 사용할 수는 있으나, 사용자의 그림을 애니메이션으로 구현하거나 동작이 필요한 그림들을 구현하는 경우 라이브러리에 대한 의존도가 높아질 수 밖에 없었습니다
- 해당 라이브러리를 통해 배지어 곡선 및 주기함수를 이용하여, 컴포넌트간의 상호작용이 많아 복잡도가 높은 애니메이션도 제작이 가능했습니다

### e. styled-component

- css를 컴포넌트 단위처럼 사용할 수 있다는 점이 가장 큰 이유였습니다
- props를 이용할 수 있어, 변화되는 컴포넌트의 css를 className의 직접적인 변경 없이도 제어가 가능하다는 것이 가장 좋은 점이었습니다

## 3. 개선해야 할 문제점

### a. 컴포넌트 간 지나치게 많은 props의 이동

- 해당 이슈에 대해서는 Redux를 사용해야 한다고 생각했습니다
- 부모, 자식 컴포넌트가 세분화될수록 props의 이동이 잦아져서 중간 컴포넌트에서 징검다리 역할을 하는 기능 코드가 너무 많아졌습니다
- redux를 사용하는 경우에도, modal창과 같이 자주 쓰이는 부분들을 묶어 관리를 시도해보아야 겠다고 생각했습니다

### b. 컴포넌트 랜더링 관련 이슈

- 애니메이션 및 상호작용이 많은 서비스를 구현해 보며 랜더링 속도가 느리다고 판단해, LightHouse로 성능 측정을 시도하였습니다
- 성능 파트에서 70점대의 낮은 점수를 받아, 해당 분석 자료를 살펴보았습니다
- 차세대 이미지 포맷 도입 및 사용하지 않는 JS를 제거하라는 피드백을 받아, 해당 이슈를 해결해나갈 예정입니다

### c. 비동기 처리 분리

- axios를 활용한 통신 중, API 통신 형식이 변경됨에 따라 일괄 변경이 필요한 경우가 있었습니다
- 해당 이슈에서는 header에 사용자의 accessToken을 추가해야 했는데, 하나의 파일 또는 구성에 모든 비동기 처리를 넣으면 통합으로 관리할 수 있을 것이라는 생각이 들었습니다
- 비동기 처리를 분리해놓고, 기능 또는 형식에 따라 구분하여 컴포넌트 구조를 변경할 예정입니다
