# Backend

## 아키텍처 패턴

내가 기린 그림의 백엔드는 MSA 아키텍처 패턴을 일부 적용하고 있고 서비스 단위로 나뉘어진 작은 프로젝트 여러 개로 이루어져 있습니다.

서비스 구성은 다음과 같습니다.
``` text
├─ auth  : 소셜 로그인 인증을 담당하는 서버입니다. Spring Security와 JWT를 사용해 Access-Token을 클라이언트에 제공합니다. 
│          Openfeign Client를 사용하여 user 서버와 통신합니다.
│          MariaDB와 연동하여 서비스됩니다.
│
├─ user  : 회원 계정과 관리와 회원이 가진 컨텐츠, 결제 코인(열쇠) 관리를 담당하는 서버입니다. 
│          Okhttp3 Client를 사용하여 store 서버와 통신합니다.
│          MariaDB와 연동하여 서비스됩니다.     
│
├─ books : 메인 서비스 컨텐츠 관리를 담당하는 서버입니다.
│          Okhttp3 Client를 사용하여 store 서버와 통신합니다.
│          MariaDB와 연동하여 서비스됩니다.    
│
└─ store : 이미지 파일을 관리하는 스토리지 서버입니다.
           MongoDB와 연동하여 서비스됩니다. 
```
<br/>

## 프로젝트 디렉토리 컨벤션

협업 시 편의성과 유지보수성을 높이기 위해 각 서비스의 디렉토리 컨벤션을 정하였습니다.

각 서비스의 디렉토리 구조는 다음과 같습니다.

``` text
├─ api
│   ├── controller
│   ├── dto                 
│   │   ├── request
│   │   └── response
│   ├── service
│   │   └── impl
│   └── util
│
├─ db
│   ├── entity
│   └── repository
│
├─ exception 
│   └── handler
│
├─ logger
│
...

```
 


# 기술 스택 및 버전 선정 근거

날짜: 2023년 3월 9일
참여자: 윤태준, 이방환

<details open>
  <summary> <b style="font-size:18px;"> JDK version - 8 vs 17 </b></summary>

> 💡 현재 공식적인 자바의 최신 버전은 Java 19입니다. 하지만 현업에서는 아직 Java 8이나 11을 쓰는 케이스가 많습니다. <br/>
> 최근 여러 검색 자료에서 Java 8/11의 지원이 끝난 이후에 발생하는 마이그레이션 비용을 줄이기 위해 소수지만 Java 17을 도입한다는 내용을 볼 수 있는데, 우리 프로젝트에선 어떤 버전을 사용하는 것이 좋을까요?

- 8 or 11
- 17 ✅

[Java version history](https://en.wikipedia.org/wiki/Java_version_history)

### 결론

프로젝트 자체로만 보면 굳이 새 프로젝트를 Java 8으로 생성할 필요는 없어 보이며, Java 17을 사용해도 무방할 것 같습니다. 그리고 SpringBoot 3부터는 Java 17 이상만 지원하지만, SpringBoot 2.x 버전에서도 Java 17 의 사용이 가능합니다.

개발자로서 새로운 것을 찾아서 적용해보고 학습하는 것에 가중치를 두어 Java 17 을 사용 하려고 합니다.

또한, 이전버전들의 LTS 종료 이후 Future Release 제품을 사용하게 되었을 때를 대비하여 미리 경험해 보고자 합니다.

</details>
<br/>

<details open>
  <summary> <b style="font-size:18px;"> JDK vendor ? </b></summary>

<aside>
💡 openJDK Vendor들이 굉장히 많은데, 종류별로 차이는 없는지, 어떤 것을 쓰는 것이 좋을까?

</aside>

- Amazon  ✅
- GraalVM
- Azul
- etc..

### 결론

 어느걸 써도 무방! 큰 차이가 없음.

</details>
<br/>

<details open>
  <summary> <b style="font-size:18px;"> SpringBoot version - 2.x  vs  3.0.x </b></summary>

> 💡 SpringBoot 3.0.0 이후부터 Java 17 이상 JDK들만 지원한다고 합니다. SpringBoot의 각 버전은 그리 공식 지원기간이 길지 않아서 2.7.x 버전도 현재 2023년 올해 support가 끊긴다고 합니다. <br/>
> 현업에서는 아직 2.x 버전을 사용하는 사례가 많고, SpringBoot 3는 아직 실험적이라고 보는 경향이 있는데, 어떤 버전을 사용하는 것이 좋을까요?



- SpringBoot 2.x
- SpringBoot 3.0.x ✅

### 결론

SpringBoot 3에서는 REST API의 표준화 운동이라고 볼 수 있는 `RFC 7807 - Problem Detail의 적용`을 지원하고 있어 좀더 엄격한 REST API의 설계가 가능합니다. 또한 [경로 조작](https://2-juhyun-2.tistory.com/496)에 대한 보안 이슈가 강화되기도 했습니다. (`api/` 와 `api` 가 더이상 같지 않습니다)

프레임워크의 버전을 바꾸는 것은 많은 차이점들과 호환성 문제들에 대한 우려가 있습니다. 하지만 최근에는 third party 애플리케이션이나 의존성 패키지들의 업데이트 주기가 상당히 빠른 편이기 때문에 critical한 문제는 되지 않으며, gradle을 통한 호환성 문제 해결도 어느정도 기대할 수 있다고 합니다.

때문에 JDK 버전 선택과 마찬가지로 지속적으로 변화하는 환경에 유연하게 대처하는 자세를 길러보고자 SpringBoot 3 을 사용해 보고자 합니다.

</details>
<br/>

<details open>
  <summary> <b style="font-size:18px;"> API Docs </b></summary>

> 💡 SpringBoot version 에 따른 이슈

- Swagger 3 ✅
    - SpringBoot 3 환경에서 실패 사례가 많다.
    - 하지만 Spring Docs와 결합하여 사용하면 된다고 한다.
- Spring Rest Docs
    - ui가 구리며, 실제 요청을 보낼때는 postman 등의 외부 프로그램을 사용해야 한다는 단점이 있다.

### 결론

한 페이지에서 요청도 보낼 수 있는 Spring Docs + Swagger 3 을 쓰려고 합니다.

</details>
<br/>


<details open>
  <summary> <b style="font-size:18px;"> Build tool </b></summary>

> 💡 유연성, 성능차이, 사용자 경험을 토대로 선정

- Maven
    - Maven은 사용자 지정을 때로는 불가능하게 하는 염격한 모델
    - Maven 빌드를 더 쉽게 이해할 수 있지만, 많은 자동화 문제에 적합하지 않음
    - Maven의 더 긴 임기는 IDE를 통한 Maven 지원이 많은 사용자에게 더 좋을 수 있음
- Gradle ✅
    - Google은 Android용 공식 빌드 도구는 Gradle을 선택
    - Gradle의 모델은 또한 C/C++로 기본 개발에 사용할 수 있음
    - Gradle은 Tooling API를 사용하여 임베딩을 염두에 두고 설계
    - Gradle의 빌드 캐시를 활용하여 성능 향상

[Gradle | Gradle vs Maven Comparison](https://gradle.org/maven-vs-gradle/)

### 결론

gradle을 쓰려고 합니다. 현재 모든 프로그래밍 진영에서 XML을 없애려는 움직임이 일어나고 있습니다.

배포 시에 도커에서 빌드하지 않도록 주의하면 빠른 지속 배포가 가능할 것으로 기대됩니다.

</details>
<br/>


<details open>
  <summary> <b style="font-size:18px;"> Coding Convention </b></summary>

> 💡 큰 차이가 없음! 어느걸 써도 무방

1. Google ✅
2. Naver
3. Oracle

</details>
<br/>

<details open>
  <summary> <b style="font-size:18px;"> RDB - Maria? MySQL </b></summary>

💡 MariaDB와 MySQL은 문법도 동일하고 모두 InnoDB를 사용할 수 있는데, 둘 간의 차이점은 무엇이고 저희 서비스에는 어떤 제품이 적합할까요?

1. MySQL
2. MariaDB ✅

| Parameter | MariaDB | MySQL |
| --- | --- | --- |
| 스토리지 엔진 | MariaDB에는 MySQL에서 찾을 수 없는 12개의 새로운 스토리지 엔진이 있습니다. | MariaDB에 비해 스토리지 옵션이 적습니다. |
| 속도 개선 | MariaDB는 MySQL에 비해 향상된 속도를 보여줍니다. | MySQL은 MariaDB에 비해 속도가 느립니다. |
| 초판 | 2009년 | 1995년 |
| 서버 운영 체제 | FreeBSD리눅스macOS솔라리스윈도우 | FreeBSD리눅스OS X솔라리스윈도우 |
| 더 빠른 캐시/인덱스 | MariaDB의 메모리 스토리지 엔진을 사용하면 표준 MySQL보다 INSERT 문을 24% 더 빠르게 완료할 수 있습니다. | MySQL의 메모리 스토리지 엔진은 MariaDB에 비해 느립니다. |
| 더 크고 빠른 연결 풀 | MariaDB는 더 빠르게 실행되고 최대 200,000개 이상의 연결을 지원할 수 있는 고급 스레드 풀과 함께 제공됩니다. | MySQL에서 제공하는 스레드 풀은 시간당 최대 200,000개의 연결을 지원할 수 없습니다. |
| 향상된 복제 | MariaDB에서는 복제를 더 안전하고 빠르게 수행할 수 있습니다. 업데이트도 기존 MySQL에 비해 2배 더 빠르게 수행할 수 있습니다. | MySQL의 커뮤니티 에디션에서는 고정된 개수의 스레드를 연결할 수 있습니다. MySQL의 엔터프라이즈 요금제는 스레드 기능과 함께 제공됩니다. |
| 새로운 기능/확장 | MariaDB에는 JSON, WITH 및 KILL 문을 비롯한 새로운 기능과 확장 기능이 함께 제공됩니다. | 새로운 MariaDB 기능은 MySQL에서 제공되지 않습니다. |
| 누락된 기능 | MariaDB에는 MySQL 엔터프라이즈 에디션에서 제공하는 일부 기능이 없습니다. 이를 해결하기 위해 대체 오픈 소스 플러그인을 제공합니다. | MySQL Enterprise Edition은 독점 코드를 사용합니다. MySQL Enterprise Edition 사용자만 여기에 액세스할 수 있습니다. |
| 우선순위 코드 | MariaDB는 이 독점 콘텐츠에 대한 액세스를 허용하지 않으며 폐쇄된 소스입니다. | MySQL은 Enterprise Edition에서 일부 독점 코드를 사용합니다. |
| 데이터 마스킹 | 아니요 | 예 |
| 동적 열 | 아니요 | 예 |
| 모니터링 | SQLyog | MySQL 워크벤치 |
| 라우팅 | MariaDB 최대 규모 | MySQL 라우터 |
| 해석학 | MariaDB 컬럼스토어 | 아니요 |
| 보조 데이터베이스 모델 | 문서 저장소 및 그래프 DBMS | 문서 저장소 |
| 힘내 허브 별 | 2.8k | 4k |
| 포크 | 868 | 1.6K |
| 사용하는 유명 기업 | Nrise, Accenture, Docplanner, Grooveshark. | 에어비앤비, 우버 테크노글로이스, 넷플릭스, 드롭박스. |

### 결론

기업용 제품이 아닌 이상 어떤 DB를 쓰든 성능 차이를 느끼기 힘들기 때문에 아직 써보지 못한 MariaDB를 써보려 합니다.

</details>