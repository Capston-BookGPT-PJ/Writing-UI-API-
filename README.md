# 책 GPT팀 독서 SNS 및 습관 관리 앱 개발 프로젝트

## GitHub에 코드 올리는 방법

<details><summary>자세히</summary>

### 0. 초기 환경 설정

1. #### 맨 처음, 이 repository를 `fork` 한다.

  ![image](https://github.com/user-attachments/assets/cfd70d2e-9381-44c3-8921-0e2f8a1e2904)



2. #### `fork` 받은 저장소를 자신의 컴퓨터에 clone 받은 뒤 폴더를 생성한다.

   앞으로 올리는 모든 파일들은 이 폴더 안에 올려야 한다

   ![image](https://github.com/user-attachments/assets/d62a12f5-c573-40d6-82a1-7b531a1419e8)


   ```bash
   $ git clone [fork한 자신의 repository 주소] // 자신의 repository에서 확인
   $ cd capstone-project

   $ mkdir -p [폴더이름]
   ```



3. #### upstream 주소 추가
  
   ﻿- master 브랜치 : 최종 프로덕트용 브랜치(완벽한 코드만 올라감)
   - develop 브랜치 : master 복사본(실수방지용/자유롭게 사용)
   - 기타 브랜치: 개인 연습장으로 활용

   ```bash
   $ git remote add upstream https://github.com/eomyejin20/capstone-project.git
   $ git remote -v // 저장소 확인
   ```
<br>

### 1. 저장소 최신으로 업데이트

커밋 내역을 깔끔하게 하기 위해서 `-r` 옵션 사용

```bash
$ git pull -r upstream master
```

<br>

### 2. 이슈(기능)별로 커밋 생성

* projects 에서 개발해야하는 이슈,기능들 확인
  ![image](https://github.com/user-attachments/assets/7a742922-e3de-4d26-a1a8-8428358c7253)

![image](https://github.com/user-attachments/assets/038615f5-e184-4377-811e-f6822bf7f052)


* master 

* 커밋하는 방법

  ```bash
  $ git add .
  $ git status
  $ git commit -m "[git 세팅하기] : 엄예진 - git 세팅완료"
  $ git push origin master
  ```




### 4. 코드리뷰 후 merge

* 코드 리뷰 내용은 자유롭게 작성하기
 

</details><br>
