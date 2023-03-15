# DIVIDEND(주식 배당금) API


## 프로젝트 개요


미국 기업의 주식 배당금 정보를 제공하는 REST API입니다.

주요 기능은 하기와 같습니다.

1. 스크래핑 기법을 활용하여 해당 회사의 정보와 미국 주식 배당금 정보를 가져옵니다.

2. 해당 회사의 정보와 배당금 정보를 저장하고 관리합니다.

3. 캐시 서버를 이용해 기존에 조회했던 회사의 정보를 신속하게 조회합니다.

## API명세

#### 1. GET - finance/dividend/{companyName}

- 회사 이름을 인풋으로 받아서 해당 회사의 메타 정보와 배당금 정보를 반환
- 잘못된 회사명이 입력으로 들어온 경우 400 status 코드와 에러메시지 반환

#### 2. GET - company/autocomplete

- 자동완성 기능을 위한 API
- 검색하고자 하는 prefix 를 입력으로 받고, 해당 prefix 로 검색되는 회사명 리스트 중 10개 반환

#### 3. GET - company

- 서비스에서 관리하고 있는 모든 회사 목록을 반환
- 반환 결과는 Page 인터페이스 형태

#### 4. POST - company

- 새로운 회사 정보 추가
- 추가하고자 하는 회사의 ticker 를 입력으로 받아 해당 회사의 정보를 스크래핑하고 저장
- 이미 보유하고 있는 회사의 정보일 경우 400 status 코드와 적절한 에러 메시지 반환
- 존재하지 않는 회사 ticker 일 경우 400 status 코드와 적절한 에러 메시지 반환

#### 5. DELETE - company/{ticker}

- ticker 에 해당하는 회사 정보 삭제
- 삭제시 회사의 배당금 정보와 캐시도 모두 삭제되어야 함

#### 6. POST - auth/signup

- 회원가입 API
- 중복 ID 는 허용하지 않음
- 패스워드는 암호화된 형태로 저장되어야함

#### 7. POST - auth/signin

- 로그인 API
- 회원가입이 되어있고, 아이디/패스워드 정보가 옳은 경우 JWT 발급

## Version

- Java 11
- SpringBoot 2.5.6

## Dependencies

- Spring Security
- JWT
- H2
- JPA
- Spring Web
- Spring MVC
- Jsoup
- Redis
- Lombok
