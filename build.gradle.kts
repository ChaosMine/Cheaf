import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
	java
	`maven-publish`
	id("io.papermc.paperweight.patcher") version "1.5.11"
	id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

val paperMavenRepoUrl = "https://repo.papermc.io/repository/maven-public/"

repositories {
	mavenCentral()
	maven(paperMavenRepoUrl) {
		content { onlyForConfigurations(configurations.paperclip.name) }
	}
}

dependencies {
	remapper("net.fabricmc:tiny-remapper:0.10.1:fat")
	decompiler("org.vineflower:vineflower:1.9.3")
	paperclip("io.papermc:paperclip:3.0.3")
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "maven-publish")

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}

	tasks.withType<JavaCompile>().configureEach {
		options.encoding = Charsets.UTF_8.name()
		options.release = 17
	}
	tasks.withType<Javadoc>().configureEach {
		options.encoding = Charsets.UTF_8.name()
	}
	tasks.withType<ProcessResources>().configureEach {
		filteringCharset = Charsets.UTF_8.name()
	}
	tasks.withType<Test> {
		testLogging {
			showStackTraces = true
			exceptionFormat = TestExceptionFormat.FULL
			events(TestLogEvent.STANDARD_OUT)
		}
	}

	repositories {
		mavenCentral()
		maven(paperMavenRepoUrl)
		maven("https://oss.sonatype.org/content/groups/public/")
		maven("https://ci.emc.gs/nexus/content/groups/aikar/")
		maven("https://repo.aikar.co/content/groups/aikar")
		maven("https://repo.md-5.net/content/repositories/releases/")
		maven("https://hub.spigotmc.org/nexus/content/groups/public/")
		maven("https://jitpack.io")
		maven("https://repo.codemc.io/repository/maven-public/")
		maven("https://maven.elytrium.net/repo/")
	}
}

paperweight {
	serverProject = project(":cheaf-server")

	remapRepo = "https://maven.fabricmc.net/"
	decompileRepo = "https://maven.quiltmc.org/"

	useStandardUpstream("Leaf") {
		url = github("Winds-Studio", "Leaf")
		ref = providers.gradleProperty("leafRef")

		withStandardPatcher {
			baseName("Leaf")

			apiPatchDir = layout.projectDirectory.dir("patches/api")
			apiOutputDir = layout.projectDirectory.dir("Cheaf-API")

			serverPatchDir = layout.projectDirectory.dir("patches/server")
			serverOutputDir = layout.projectDirectory.dir("Cheaf-Server")
		}

		patchTasks.register("generatedApi") {
			isBareDirectory = true
			upstreamDirPath = "paper-api-generator/generated"
			patchDir = layout.projectDirectory.dir("patches/generated-api")
			outputDir = layout.projectDirectory.dir("paper-api-generator/generated")
		}
	}
}
