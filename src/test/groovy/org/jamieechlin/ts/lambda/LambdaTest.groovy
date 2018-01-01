package org.jamieechlin.ts.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.fasterxml.jackson.databind.ObjectMapper
import cz.habarta.typescript.generator.ClassMapping
import cz.habarta.typescript.generator.Input
import cz.habarta.typescript.generator.JsonLibrary
import cz.habarta.typescript.generator.Settings
import cz.habarta.typescript.generator.TypeScriptFileType
import cz.habarta.typescript.generator.TypeScriptGenerator
import cz.habarta.typescript.generator.TypeScriptOutputKind
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.jamieechlin.ts.ConversionSettings
import org.jamieechlin.ts.Lambda
import org.jamieechlin.ts.RequestInput
import spock.lang.Specification

class LambdaTest extends Specification {

    def context = [
        getLogger: { ->
            return [
                log: { s -> println(s) }
            ] as LambdaLogger
        }
    ] as Context

    def "test success"() {
        expect:
        runLambda("class X{}").output == "\ninterface X {\n}\n"
    }

    def "test failure"() {
        setup:
        def output = runLambda("clas X{}")

        expect:
        output.containsKey("error")
        output.error.startsWith("startup failed")
    }

    def "test success with options"() {
        expect:

        def settingsJson = JsonOutput.toJson([
            outputFileType: TypeScriptFileType.implementationFile.name(),
            outputKind    : TypeScriptOutputKind.global.name(),
            jsonLibrary   : JsonLibrary.jackson2.name(),
            mapClasses    : ClassMapping.asClasses.name(),
            noFileComment : true,
            excludeClasses: [GroovyObject.name],
        ])


        runLambda("class X{ }", settingsJson).output == "\nclass X {\n}\n"
    }

    def "test settings with bad options"() {
        setup:

        def settingsJson = JsonOutput.toJson([
            xoutputFileType: TypeScriptFileType.implementationFile.name(),
        ])


        def output = runLambda("class X{ }", settingsJson)

        expect:
        output.containsKey("error")
    }

    def "basic settings"() {
        setup:

        def settingsJson = [
            outputFileType: TypeScriptFileType.declarationFile.name(),
            outputKind    : TypeScriptOutputKind.global.name(),
            jsonLibrary   : JsonLibrary.jackson2.name(),
            noFileComment : true,
            excludeClasses: [GroovyObject.name],
        ]

        println JsonOutput.prettyPrint(JsonOutput.toJson(settingsJson))
    }

    private Map runLambda(String input, String settings = null) {

        new JsonSlurper().parseText(new Lambda().myHandler(new RequestInput(
            input: input,
            settings: settings
        ), context)) as Map
    }

    static Settings settings() {
        final Settings settings = new Settings()
        settings.outputKind = TypeScriptOutputKind.global
        settings.jsonLibrary = JsonLibrary.jackson2
        settings.noFileComment = true
        settings.newline = "\n"
        settings.setExcludeFilter([GroovyObject.name], [])
        return settings
    }

    def "gen settings ts"() {
        setup:
        final String output = new TypeScriptGenerator(settings()).generateTypeScript(Input.from(ConversionSettings))

        // gen schema using:
        // npx typescript-json-schema types.ts ConversionSettings

        println output


        println JsonOutput.toJson(new ConversionSettings())

        def conversionSettings = new ObjectMapper().readValue("{}", ConversionSettings)
        println conversionSettings.toSettings()

    }

    def "gen settings json"() {
        setup:
        def settings = new ConversionSettings()
        settings.outputKind = TypeScriptOutputKind.ambientModule

        def json = new ObjectMapper().writeValueAsString(settings)
        println json

        def conversionSettings = new ObjectMapper().readValue(json, ConversionSettings)
        println conversionSettings.toSettings()

        expect:
        conversionSettings.toSettings().outputKind == TypeScriptOutputKind.ambientModule


    }

}
