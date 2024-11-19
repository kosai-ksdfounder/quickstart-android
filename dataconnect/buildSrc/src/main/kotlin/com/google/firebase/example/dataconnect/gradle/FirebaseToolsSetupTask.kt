/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.example.dataconnect.gradle

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

abstract class FirebaseToolsSetupTask : DefaultTask() {

    @get:Input abstract val version: Property<String>

    @get:InputFile @get:Optional
    abstract val npmExecutable: Property<File>

    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @get:Internal
    val firebaseExecutable: Provider<RegularFile>
        get() = outputDirectory.map { it.file("node_modules/.bin/firebase") }

    @TaskAction
    fun run() {
        val version: String = version.get()
        val npmExecutable: File? = npmExecutable.orNull
        val outputDirectory: File = outputDirectory.get().asFile

        logger.info("version: {}", version)
        logger.info("npmExecutable: {}", npmExecutable?.absolutePath)
        logger.info("outputDirectory: {}", outputDirectory.absolutePath)

        project.delete(outputDirectory)
        project.mkdir(outputDirectory)

        val packageJsonFile = File(outputDirectory, "package.json")
        packageJsonFile.writeText("{}", Charsets.UTF_8)

        runCommand(File(outputDirectory, "install.log.txt")) {
            val arg0 = npmExecutable?.absolutePath ?: "npm"
            commandLine(arg0, "install", "firebase-tools@$version")
            workingDir(outputDirectory)
        }
    }

    internal fun configureFrom(providers: MyProjectProviders) {
        version.set(providers.firebaseToolsVersion)
        outputDirectory.set(providers.buildDirectory.map { it.dir("firebase-tools") })

        npmExecutable.set(
            providers.localConfigs.map(
                TransformerInterop { localConfigs ->
                    val result = localConfigs.filter {
                        it.npmExecutable !== null
                    }.map { Pair(it.srcFile, it.npmExecutable!!) }.firstOrNull()
                    result?.let { (configFile, npmExecutablePath) ->
                        File(npmExecutablePath).also {
                            if (!it.exists()) {
                                throw GradleException(
                                    "npmExecutable specified in ${configFile?.absolutePath} " +
                                        "does not exist: ${it.absolutePath} " +
                                        "(error code eaw5gppkep)"
                                )
                            }
                        }
                    }
                }
            )
        )
    }
}
