pluginManagement {
	repositories {
		gradlePluginPortal()
		maven("https://repo.papermc.io/repository/maven-public/")
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "cheaf"

for (name in listOf("Cheaf-API", "Cheaf-Server", "paper-api-generator")) {
	val projectName = name.lowercase()
	include(projectName)
	findProject(":$projectName")!!.projectDir = file(name)
}
