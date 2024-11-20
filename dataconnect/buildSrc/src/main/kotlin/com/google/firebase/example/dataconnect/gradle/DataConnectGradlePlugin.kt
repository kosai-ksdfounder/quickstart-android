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

import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.google.firebase.example.dataconnect.gradle.providers.MyProjectProviders
import com.google.firebase.example.dataconnect.gradle.providers.MyVariantProviders
import com.google.firebase.example.dataconnect.gradle.tasks.GenerateDataConnectSourcesTask
import com.google.firebase.example.dataconnect.gradle.tasks.SetupFirebaseToolsTask
import com.google.firebase.example.dataconnect.gradle.tasks.configureFrom
import java.util.Locale
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.register
import oshi.SystemInfo

@Suppress("unused")
abstract class DataConnectGradlePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val si = SystemInfo()
        si.operatingSystem.let {
            println("family: ${it.family}")
            println("bitness: ${it.bitness}")
            println("manufacturer: ${it.manufacturer}")
            println("versionInfo.version: ${it.versionInfo.version}")
            println("versionInfo.codeName: ${it.versionInfo.codeName}")
            println("versionInfo.buildNumber: ${it.versionInfo.buildNumber}")
        }
        si.hardware.let {
            println("computerSystem.model: ${it.computerSystem.model}")
            println("computerSystem.manufacturer: ${it.computerSystem.manufacturer}")
            println("computerSystem.firmware: ${it.computerSystem.firmware}")
            println("computerSystem.baseboard: ${it.computerSystem.baseboard}")
            println("computerSystem.hardwareUUID: ${it.computerSystem.hardwareUUID}")
            println("computerSystem.serialNumber: ${it.computerSystem.serialNumber}")
            println("processorIdentifier.family: ${it.processor.processorIdentifier.family}")
            println("processorIdentifier.name: ${it.processor.processorIdentifier.name}")
            println("processorIdentifier.model: ${it.processor.processorIdentifier.model}")
            println("processorIdentifier.identifier: ${it.processor.processorIdentifier.identifier}")
            println("processorIdentifier.processorID: ${it.processor.processorIdentifier.processorID}")
            println("processorIdentifier.isCpu64bit: ${it.processor.processorIdentifier.isCpu64bit}")
            println("processorIdentifier.microarchitecture: ${it.processor.processorIdentifier.microarchitecture}")
            println("processorIdentifier.stepping: ${it.processor.processorIdentifier.stepping}")
            println("processorIdentifier.vendor: ${it.processor.processorIdentifier.vendor}")
            println("processorIdentifier.vendorFreq: ${it.processor.processorIdentifier.vendorFreq}")
        }

        project.extensions.create("dataconnect", DataConnectExtension::class.java)
        val providers = project.objects.newInstance<MyProjectProviders>()

        project.tasks.register<SetupFirebaseToolsTask>("setupFirebaseToolsForDataConnect") {
            configureFrom(providers)
        }

        val androidComponents = project.extensions.getByType<ApplicationAndroidComponentsExtension>()
        androidComponents.onVariants { variant ->
            val variantProviders = project.objects.newInstance<MyVariantProviders>(variant, providers)
            registerVariantTasks(project, variant, variantProviders)
        }
    }

    private fun registerVariantTasks(
        project: Project,
        variant: ApplicationVariant,
        providers: MyVariantProviders
    ) {
        val variantNameTitleCase = variant.name.replaceFirstChar { it.titlecase(Locale.US) }

        val generateCodeTaskName = "generate${variantNameTitleCase}DataConnectSources"
        val generateCodeTask = project.tasks.register<GenerateDataConnectSourcesTask>(generateCodeTaskName) {
            configureFrom(providers)
        }

        variant.sources.java!!.addGeneratedSourceDirectory(
            generateCodeTask,
            GenerateDataConnectSourcesTask::outputDirectory
        )
    }
}
