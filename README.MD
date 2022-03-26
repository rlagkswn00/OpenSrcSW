# 2022 오픈소스 SW 과제물 제출

컴퓨터공학부 201811246 김한주

## 파일 구조

```bash
├── README.md
├── jars
├── data
├── index.post
├── index.xml
├── collection.xml
└── src
     ├── indexer.java
     ├── kuir.java
     ├── makeCollection.java
     └── makeKeyword.java
```

## 인코딩

**Encoding : UTF-8**

## 디렉토리 설명

**src/scrips** : .java 소스 파일이 저장되는 디렉토리

**bin/** : 컴파일된 .class 바이너리 파일이 저장되는 디렉토리

**jars/** : 외부 jar 파일이 저장되는 디렉토리

## 컴파일 명령어

### MAC

`javac -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,, src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp jars/jsoup-1.13.1.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

### WINDOWS

`javac -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,, src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp jars/jsoup-1.13.1.jar:jars/kkma-2.1.jar src/scripts/*.java -d bin -encoding UTF8`

`javac -cp "(외부 jar 파일 이름 1);(외부 jar 파일 이름 2);,,,," src/scripts/*.java -d bin (-encoding UTF8)`

ex) `javac -cp "jars/jsoup-1.13.1.jar;jars/kkma-2.1.jar" src/scripts/*.java -d bin -encoding UTF8`

## 실행 명령어

### MAC

`java -cp (외부 jar 파일 이름 1):(외부 jar 파일 이름 2):,,,,:bin scripts.kuir (args 1) (args 2) ,,, (args n)`

ex) `java -cp ./jars/jsoup-1.13.1.jar:./jars/kkma-2.1.jar:bin scripts.kuir -c data`

### WINDOWS

`java -cp (외부 jar 파일 이름 1);(외부 jar 파일 이름 2);,,,,;bin scripts.kuir (args 1) (args 2) ,,, (args n)`

ex) `java -cp ./jars/jsoup-1.13.1.jar;./jars/kkma-2.1.jar;bin scripts.kuir -c data`