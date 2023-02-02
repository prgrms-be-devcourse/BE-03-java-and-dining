# 🍣 Java-and-Dining

프로그래머스 데브코스 백엔드 3기 앨런팀의 캐치 테이블 클론코딩 프로젝트

![image](https://user-images.githubusercontent.com/65555299/216244230-c9ad2dc3-4a35-4774-a0ea-f13b6bad80b0.png)

## 📌 프로젝트 목표

- 협업 경험 ( 수정 예정)

## 🧑‍💻 팀원 소개

|            Product Owner            |             Scrum Master              |              Developer              |              Developer               |             Developer             |                Mentor                |              Sub Mentor               |
|:-----------------------------------:|:-------------------------------------:|:-----------------------------------:|:------------------------------------:|:---------------------------------:|:------------------------------------:|:-------------------------------------:|
| [김선호](https://github.com/preferKim) | [이택승](https://github.com/dlxortmd987) | [서예원](https://github.com/yewon9609) | [김 환](https://github.com/hwankim123) | [이수린](https://github.com/Tnfls99) | [앨런](https://github.com/hongbin-dev) | [함승훈](https://github.com/seung-hun-h) |

## 🛠️ 기술 스택

<!-- 요 링크에서 따오면 좋을 듯! https://github.com/Ileriayo/markdown-badges --> 

### Tech

<img src="https://img.shields.io/badge/Java-FC4C02?style=flat-square&logo=java&logoColor=white"/>
<img src="https://img.shields.io/badge/Spring boot-6DB33F?style=flat-square&logo=Spring boot&logoColor=white"/>
<img src="https://img.shields.io/badge/gradle-02303A?logo=gradle&logoWidth=25"/> 
<img src="https://img.shields.io/badge/Spring Data JPA-0078D4?style=flat-square&logo=Spring Data JPA&logoColor=white"/>
<img src="https://img.shields.io/badge/Query DSL-0078D4?style=flat-square&logo=Spring Data JPA&logoColor=white"/>

<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=spring-security&logoColor=white"/> 

<img src="https://img.shields.io/badge/MySQL-2AB1AC?style=flat-square&logo=MySQL&logoColor=white"/> 

<img src="https://img.shields.io/badge/H2 Database-2AB1AC?style=flat-square&logo=&logoColor=white"/> 


<img src="https://img.shields.io/badge/Junit-25A162?style=flat-square&logo=Junit5&logoColor=white"/> 
<img src="https://img.shields.io/badge/REST Docs-8CA1AF?style=flat-square&logo=Read the Docs&logoColor=white">

### Deploy

<img src="https://img.shields.io/badge/Github Actions-2AB1AC?style=flat-square&logo=github&logoColor=black"/> <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=flat-square&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/AWS-%23FF9900.svg?style=flat-square&logo=amazon-aws&logoColor=white"/> <img src="https://img.shields.io/badge/Cloud Watch-FF4F8B?style=flat-square&logo=amazon-cloudwatch&logoColor=white"/>

### Tool

<img src="https://img.shields.io/badge/IntelliJ IDEA-8A3391?style=flat-square&logo=IntelliJ IDEA&logoColor=black"/> <img src="https://img.shields.io/badge/Github-000000?style=flat-square&logo=Github&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-FFFFFF?style=flat-square&logo=Notion&logoColor=black"/> 
<img src="https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=Slack&logoColor=white"/>
<img src="https://img.shields.io/badge/Jira-0052CC?style=flat-square&logo=Jira&logoColor=white"/>

## 🍎 설계 및 문서

### 프로젝트 구조

(예정)

### ERD

(예정)

### API 명세서

- [REST Docs](http://3.37.85.54:8080/)
- [노션](https://backend-devcourse.notion.site/API-6671c63eabbe4d5cb20f41700a884bbd)

## 주요 기능

- 회원가입, 로그인
- 예약
- 예약 상태 변경

## 🚀 배포 주소

- [java-and-dining](http://3.37.85.54:8080/)

## 브랜치 전략 (추가 예정)

### [현재 접근 가능한 IP](43.200.173.123)

## 🍇 프로젝트 실행 방법

### 사전 준비

- 프로젝트 실행 전 슬랙 채널 연동을 위한 토큰을 발급받아야 하며, 슬랙 채널을 생성하고, `applicatoin.yml` 에
  입력해야한다. ([참고](https://backend-devcourse.notion.site/Slack-48efebe50bdd48d7a52539568b136a78))
- JWT 토큰을 발급 받기 위해 `jwt issure`, `jwt secret` 를 `applicatoin.yml` 에 입력해야한다.

### using Github Project

1. github에서 프로젝트를 다운받는다

   ```https://github.com/prgrms-be-devcourse/BE-03-java-and-dining.git```

2. 프로젝트 파일 경로 `src/main/resources` 에 `application.yml` 을 작성한다.

   ```yaml
   spring:
     config:
       activate:
         on-profile: local
   
   ---
   
   spring:
     profiles:
       active: local
     datasource:
       url: jdbc:h2:mem:test
       username: sa
       password:
       driver-class-name: org.h2.Driver
   jpa:
     hibernate:
       ddl-auto: create
     properties:
        hibernate:
          format_sql: true
   
   slack:
     token: "ENTER YOUR SLACK TOKEN"
   
   jwt:
     issuer: "ENTER YOUR JWT ISSUER"
     secret: "ENTER YOUR JWT SECRET"
     expiration_ms: 1800000  
   
   ```

3. build 후, jar 파일을 실행한다

    ```
    ./gradlew clean build
    java -jar build/libs/dining-1.0.0-SNAPSHOT.jar
    ```

## 🫐 프로젝트 페이지

### [프로젝트 홈](https://backend-devcourse.notion.site/7609a45d6dc648dba5df537456db75d3)

### [프로젝트 문서 (최종 프로젝트 문서 추가)]

### [회고](https://backend-devcourse.notion.site/ec39d675e1ee42929672d9d481eba364)
