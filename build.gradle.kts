import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.30"

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")

    // mongodb
    annotation("org.springframework.data.mongodb.core.mapping.Document")
}

repositories {
    mavenCentral()
}

// MongoDB QueryDSL 사용을 위한 설정
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

extra["springCloudVersion"] = "2020.0.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kapt {
    annotationProcessor("com.querydsl.apt.jpa.JPAAnnotationProcessor")
    annotationProcessor("org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor")
}

dependencies {
    val springCloudStarterAwsVersion = "2.2.6.RELEASE"
    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    // FeignClient에서 HttpMethod PATCH를 지원하지 않아 추가 설정
    implementation("io.github.openfeign:feign-okhttp")
    implementation("io.github.openfeign:feign-jackson")
    implementation("org.springframework.cloud:spring-cloud-starter-aws:${springCloudStarterAwsVersion}")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // implementation("org.springframework.boot:spring-boot-starter-mustache")
    // implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // AOP
    //    implementation("org.springframework.boot:spring-boot-starter-aop")

    val springdocVersion = "1.5.10"
    //Swagger OpenAPI 3.0
    // GitHub: https://github.com/springdoc/springdoc-openapi
    // DEMO: https://github.com/springdoc/springdoc-openapi-demos
    // Getting Started: https://springdoc.org
    implementation("org.springdoc:springdoc-openapi-ui:${springdocVersion}")
    // implementation("org.springdoc:springdoc-openapi-data-rest:${springdocVersion}")
    // implementation("org.springdoc:springdoc-openapi-kotlin:${springdocVersion}")

    // RestTemplate에서 PATCH를 지원하기 위해 HttpClient를 이용
    implementation("org.apache.httpcomponents:httpclient")

    val slackWebhookVersion = "1.4.0"
    val userAgentUtilsVersion = "1.21"
    val gsonVersion = "2.8.8"
    // Slack Messanger: https://github.com/gpedro/slack-webhook
    implementation("net.gpedro.integrations.slack:slack-webhook:${slackWebhookVersion}")
    implementation("eu.bitwalker:UserAgentUtils:${userAgentUtilsVersion}")
    implementation("com.google.code.gson:gson:${gsonVersion}")

    val poiVersion = "5.0.0"
    val typeParserVersion = "0.7.0"
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("com.github.drapostolos:type-parser:${typeParserVersion}")

    val commonsPool2Version = "2.10.0"
    val commonsLang3Version = "3.12.0"
    val commonsTextVersion = "1.9"
    val commonsFileuploadVersion = "1.4"
    val commonsIoVersion = "2.8.0"
    val commonsCodecVersion = "1.15"

    // Apache Commons
    implementation("org.apache.commons:commons-pool2:$commonsPool2Version")
    implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    implementation("org.apache.commons:commons-text:$commonsTextVersion")
    implementation("commons-fileupload:commons-fileupload:$commonsFileuploadVersion")
    implementation("commons-io:commons-io:$commonsIoVersion")
    // implementation("commons-codec:commons-codec:$commonsCodecVersion")

    // REDIS
    // implementation("org.springframework.boot:spring-boot-starter-data-redis")

    val hibernateTypes52Version = "2.12.1"
    //DB
    implementation("mysql:mysql-connector-java")
    implementation("com.querydsl:querydsl-jpa") // querydsl
    implementation("com.vladmihalcea:hibernate-types-52:${hibernateTypes52Version}")
    kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa") // querydsl
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
    runtimeOnly("com.h2database:h2")
    // Querydsl 경로 설정
    sourceSets.main {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("$buildDir/generated/source/kapt/main")
        }
    }
    val p6spyVersion = "1.6.3"
    // JPA 로그 출력
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:${p6spyVersion}")

    // Others
    // val modelmapperVersion = "2.4.1"
    // implementation("org.modelmapper:modelmapper:$modelmapperVersion")

    val kotlinxSerializationJsonVersion = "1.2.2"
    val kotlinxCoroutinesCoreVersion = "1.5.1"
    // 코틀린
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    val jacksonVersion = "2.12.4"
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // JWT
    implementation( "io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly( "io.jsonwebtoken:jjwt-jackson:0.11.2")

    // Monitoring
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    val javaUuidGeneratorVersion = "4.0.1"
    // UUID
    implementation("com.fasterxml.uuid:java-uuid-generator:$javaUuidGeneratorVersion")

    val springmockkVersion = "3.0.1"
    val s3mockVersion = "0.2.6"
    // val spekVersion = "1.1.5"
    // val junitPlatformVersion = "1.1.0"
    // val kotlinVersion = "1.5.0"

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.ninja-squad:springmockk:${springmockkVersion}")
    testImplementation("io.findify:s3mock_2.13:${s3mockVersion}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

// 배포시 PlainJAR 파일이 생성되어 문제가되어 설정
tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
    // exclude("**/*")
}
