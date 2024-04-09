/*
 *  Copyright (c) 2022 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

plugins {
    `java-library`
    id("application")
    alias(libs.plugins.shadow)
    `maven-publish`
}

dependencies {
    implementation(libs.edc.control.plane.api.client)
    implementation(libs.edc.control.plane.api)
    implementation(libs.edc.control.plane.core)
    implementation(libs.edc.dsp)
    implementation(libs.edc.configuration.filesystem)
    implementation(libs.edc.vault.filesystem)
    implementation(libs.edc.iam.mock)
    implementation(libs.edc.management.api)
    implementation(libs.edc.transfer.data.plane)
    implementation(libs.edc.transfer.pull.http.receiver)
    implementation(project(":extensions:common:sql:sql-core"))
    implementation(project(":extensions:common:sql:sql-lease"))
    implementation(project(":extensions:control-plane:store:sql:asset-index-sql"))
    implementation(project(":extensions:control-plane:store:sql:contract-definition-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:contract-negotiation-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:policy-definition-store-sql"))
    implementation(project(":extensions:control-plane:store:sql:transfer-process-store-sql"))

    testImplementation(project(":core:common:junit"))
    testImplementation(project(":core:control-plane:control-plane-catalog"))
    testImplementation(project(":core:control-plane:control-plane-contract"))
    testImplementation(project(":core:control-plane:control-plane-core"))
    testImplementation(testFixtures(project(":extensions:common:sql:sql-core")))

    implementation(libs.edc.data.plane.selector.api)
    implementation(libs.edc.data.plane.selector.core)
    implementation(libs.edc.data.plane.selector.client)

    implementation(libs.edc.data.plane.api)
    implementation(libs.edc.data.plane.core)
    implementation(libs.edc.data.plane.http)
}

application {
    mainClass.set("$group.boot.system.runtime.BaseRuntime")
}

var distTar = tasks.getByName("distTar")
var distZip = tasks.getByName("distZip")

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    mergeServiceFiles()
    archiveFileName.set("connector.jar")
    dependsOn(distTar, distZip)
}
