# TCRUD_Backend

  

## TCRUD project

기본적인 기능(__C__ reate/__R__ ead/__U__ pdate/__D__ elete)를 중심으로 개발되었으며, 안정적인 동작을 최우선 고려하였습니다.
추가적인 기능 개발과 확장을 고려하여 설계하였으며, 필요에 따라 새로운 기능을 추가/확장 할 계획입니다.
TCRUD는 **TOTAL CRUD**의 약자로 프로젝트명을 정했습니다.

- 배포주소 http://150.230.248.90:8000/

  


### TCRUD 구현기능 (v0.1)

 - 회원가입 / 로그인 
 - 게시글 작성 / 수정 / 삭제 / 검색 / 페이징
 - 댓글 작성 / 수정 / 삭제 / 페이징
 

### TCRUD 구현기능 (v0.2)

- 대댓글(NestedReply) 적용(v0.1의 페이징 제거)
- HTTP RESPONSE 일부 수정
<details>
<summary>수정사항</summary>
<div markdown="1">
         
<p></p>
   
1. 대댓글 구현
   

<img src='https://github.com/supermo0n/supermo0n.github.io/assets/132265663/30b16e5c-e906-4807-a5f4-3436b6bc711f'>
<p></p>
<img src='https://github.com/supermo0n/supermo0n.github.io/assets/132265663/110eef3d-35f6-4d3f-aa9f-fd1048823c01'>

단순 댓글 기능에서 계층형 댓글 구현.
Reply Entity 수정 및 Front(Vue.js)에서 댓글 노출을 위한 옵션(hidden) 추가.
만약, 자식 댓글이 있는 댓글을 삭제할 경우 => hidden = 'Y'
👉 자식 댓글이 없는 경우 => 기존의 삭제 로직 수행
 기존 페이징 방식(Pageable)으론 제대로 된 페이징 구현이 어려움.
<p></p>👉 v0.3에서 페이징 재적용 위한 시도.

<p></p>

2. HTTP Response 일부 수정
<p></p>

<img src='https://github.com/supermo0n/supermo0n.github.io/assets/132265663/529e4bfc-ca8b-4592-ab98-4e46093687f1'>


이미 서비스 하고있는 여러 사이트에서 잘못된 로그인 시도를 하며 콘솔을 확인한 결과
HTTP STATUS를 노출시키지 않음.<p></p>
👉 일부 API 응답에 새로운 messageResponse를 이용, 상태+메세지를 함께 전송하는 방식을 적용
👉 URI 및 메소드 노출로 인한 보안우려를 낮출 가능성


<p></p>

3. CKEditor 적용

<img src='https://github.com/supermo0n/supermo0n.github.io/assets/132265663/a55fa8cc-a2f5-4439-bb17-41cba81a1def'>

기존 사용중이던 textarea 입력 폼 대신 CKEDITOR 적용. 
👉 CSS 문제가 일부 있음. 차후 수정 또는 다른 리치 에디터 적용 검토.
   

</div>
</details>


___
### 사용 기술 및 도구
 - Backend
	 - JAVA 8 / Gradle
	 - SpringBoot 2.7.1
	 - JPA
	 - Oracle DB (Oracle Cloud)
	 - Spring Security + JWT

 - Frontend
	 - Vue.js 2.6.1
	 - Bootstrap 5.2.3
	 - HTML / CSS / JavaScript
- 배포
	- Oracle VM (ubuntu 20.04 / Tomcat 9)
	
- 개발도구
	- Intellij / VScode / WebStorm / DataGrip
 
___
### 구조


<details>
<summary> DB</summary>
<div markdown="1">

<img src='https://github.com/supermo0n/supermo0n.github.io/assets/132265663/1c0e46e8-d361-43f8-8861-bf51ffed7a5a'>

<p></p>

#### COMMON(공통요소)
| 컬럼명 | 데이터 타입 | 조건 | 설명 |
|---|:---:|:---:|---|
| DELETE_YN | `VARCHAR2` | - | SOFT DELETE 스위치 | 
| INSERT_TIME | `TIMESTAMP` | - | 생성시간 STAMP | 
| UPDATE_TIME | `TIMESTAMP` | - | 수정시간 STAMP | 
| DELETE_TIME | `TIMESTAMP` | - | 삭제시간 STAMP | 

<br>

#### USER
| 컬럼명 | 데이터 타입 | 조건 | 설명 |
|---|:---:|---|---|
| ID | `NUMBER` | `PK` | USER 고유 ID (시퀀스) | 
| USERNAME | `VARCHAR2` | `unique` `not null` | 로그인 ID | 
| NICKNAME | `VARCHAR2` | `unique` `not null` | 닉네임 | 
| EMAIL | `VARCHAR2` | `unique` `not null` | EMAIL | 
| PASSWORD | `VARCHAR2` | `not null` | 비밀번호 | 

<br>

#### ROLE
| 컬럼명 | 데이터 타입 | 조건 | 설명 |
|---|:---:|---|---|
| ID | `NUMBER` | `PK` | ROLE 고유 ID (시퀀스) | 
| NAME | `VARCHAR2` | `not null` | ROLE(역할) | 

<br>

#### USER_ROLE
| 컬럼명 | 데이터 타입 | 조건 | 설명 |
|---|:---:|---|---|
| ID | `NUMBER` | `PK` | USER_ROLE 고유 ID (시퀀스) | 
| USER_ID | `NUMBER` | `FK` | ROLE을 부여할 USER 고유ID | 
| ROLE_ID | `NUMBER` | `FK` | 부여할 ROLE 고유ID | 

<br>

#### BOARD
| 컬럼명 | 데이터 타입 | 조건 | 설명 |
|---|:---:|---|---|
| ID | `NUMBER` | `PK` | BOARD 고유 ID (시퀀스) | 
| TITLE | `VARCHAR2` | `not null` | 제목 | 
| CONTENT | `CLOB` | `not null` | 내용 | 
| VIEWCNT | `NUMBER` | `unique` `not null` | 조회수 | 
| USER_ID | `NUMBER` | `FK` `not null` | 작성자 고유ID | 

<br>

#### REPLY
| 컬럼명 | 데이터 타입 | 조건 | 설명 | 비고 |
|---|:---:|---|---|---|
| ID | `NUMBER` | `PK` | REPLY 고유 ID (시퀀스) | 
| CONTENT | `VARCHAR2` | `not null` | 내용 | 
| USER_ID | `NUMBER` | `FK` `not null` | 작성자 고유ID | 
| BOARD_ID | `NUMBER` | `FK` `not null` | 해당 댓글의 게시글 고유ID | 
| PARENT_ID | `NUMBER` | `FK` `not null` | 해당 댓글이 대댓글인 경우<br> 부모 댓글의 고유ID | 
| HIDDEN | `VARCHAR2` | - | 해당 댓글에 자식(children) 댓글이 있을 경우 <br> HIDDEN 으로 삭제 처리(비노출) |v0.2에서 추가|

</div>
</details>

</div>
</details>

<p></p>
<details>
<summary> API</summary>


#### BOARD

| 기능 | method | URL | RETURN |
|---|:---:|---|---|
| 게시판 전체 조회 | `GET` | /board | 게시글 리스트(페이징) 목록 |
| 게시글 조회 | `GET` | /board/{id} | 게시글{id} 상세보기 페이지 
| 게시글 등록 | `POST` | /board/add-board | 게시글 등록 페이지 
| 게시글 수정 | `PUT` | /board/{id} | 게시글 수정 페이지
| 게시글 삭제 | `DELETE` | /board/{id} | 게시글 목록

<br>

#### REPLY
| 기능 | method | URL | RETURN |
|---|:---:|---|---|
| 게시글 댓글 조회 | `GET` | /board/{boardId}/reply | {boardId} 게시글의 댓글 목록 | 
| 댓글 등록 | `POST` | /board/{boardId}/reply | {boardId} 게시글의 댓글 목록 |
| 댓글 수정 | `PUT` | /board/{boardId}/reply/{replyId} | {boardId} 게시글의 댓글 목록 |
| 댓글 삭제 | `DELETE` | /board/{boardId}/reply/{replyId}  | {boardId} 게시글의 댓글 목록 |

<br>

#### USER
| 기능 | method | URL | RETURN |
|---|:---:|---|---|
| 회원가입 | `POST` | /auth/signup | 회원가입 성공여부 | 
| 로그인 | `POST` | /auth/signin | 로그인 성공시 → myProfile <br> 로그인 실패시 → message | 
| 회원정보 수정 전<br> 비밀번호 확인  | `POST` | /auth/matchpwd | 비밀번호 확인 성공 → 회원정보 수정 <br> 로그인 비밀번호 확인 실패 → message |
| 회원정보 수정 | `POST` | /auth/user/update | myProfile 페이지 |
| 회원 탈퇴 | `DELETE` | /auth/user/{userId} | 로그인 페이지 |
| 로그아웃 | - | - | LocalStorage JWT 삭제<br> 로그인 페이지 이동 | 

</div>
</details>

