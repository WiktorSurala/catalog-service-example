plugins {
	id("java")
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("de.surala.containertool.docker-plugin") version "1.0.0"
	id("io.freefair.lombok") version "8.11"
}

group = "de.surala.example.eco"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

tasks.withType<JavaCompile> {
	sourceCompatibility = JavaVersion.VERSION_21.toString()
	targetCompatibility = JavaVersion.VERSION_21.toString()
}

repositories {
	mavenCentral()
}

val jacksonVersion="2.18.0"
val springVersion="3.4.0"
val liquibaseVersion="4.29.2"
val testcontainerVersion="1.19.0"
val postgresVersion="42.7.4"
val junitVersion="5.9.2"
val junitPlatformLauncherVersion="1.11.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:$springVersion")
	implementation("org.springframework.boot:spring-boot-starter-web:$springVersion")
	implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
	implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
	implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
	implementation ("org.liquibase:liquibase-core:$liquibaseVersion")

	runtimeOnly("org.postgresql:postgresql:$postgresVersion")

	testImplementation("org.springframework.boot:spring-boot-starter-test:$springVersion")
	testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
	testImplementation("org.testcontainers:postgresql:$testcontainerVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformLauncherVersion")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

docker {
	mainGroupName = "Docker"

	container {
		group = "Database"
		name = "postgresDB"
		image = "postgres"
		tag = "latest"
		ports[5432] = 5432
		environments["POSTGRES_USER"] = "myuser"
		environments["POSTGRES_PASSWORD"] = "mypassword"
		volumes["pg-data"] = "/var/lib/postgresql/data"
	}
}
