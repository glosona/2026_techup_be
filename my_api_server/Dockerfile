# ---- Stage 1: Build (빌드 전용 단계) ----
# JDK가 포함된 이미지를 사용하여 소스코드를 컴파일하고 JAR 파일을 만듭니다.
FROM amazoncorretto:25-al2023 AS builder

# 작업 디렉토리 설정
WORKDIR /app

RUN dnf install -y \
    findutils \
    tar \
    gzip \
    unzip

# Gradle 래퍼와 설정 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# [핵심] 의존성만 먼저 다운로드하여 캐싱함
# 소스코드가 바뀌어도 라이브러리가 그대로면 이 단계는 건너뛰어 빌드 시간이 단축됩니다.
RUN ./gradlew dependencies --no-daemon

# 실제 소스코드 복사 및 빌드 (테스트는 제외하여 속도 향상)
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# ---- Stage 2: Run (실행 전용 단계) ----
# 실행에는 JDK가 필요 없으므로 가벼운 'headless' JRE 버전을 사용합니다. (이미지 크기 감소)
FROM amazoncorretto:25-al2023-headless

WORKDIR /app

# [핵심] 1단계(builder)에서 생성된 결과물(jar)만 쏙 뽑아옵니다.
# 소스코드나 빌드 툴은 버리고 실행 파일만 남겨서 보안상 더 안전합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 컨테이너가 사용할 포트 명시
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
