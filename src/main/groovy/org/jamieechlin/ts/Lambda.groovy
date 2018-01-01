package org.jamieechlin.ts

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
import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer

class RequestInput {
    String input
    String settings
}

class Lambda {
    static Settings settings() {
        final Settings settings = new Settings()
        settings.outputKind = TypeScriptOutputKind.global
        settings.jsonLibrary = JsonLibrary.jackson2
        settings.noFileComment = true
        settings.newline = "\n"
        settings.setExcludeFilter([GroovyObject.name], [])
        return settings
    }


    @SuppressWarnings(["GrMethodMayBeStatic", "GroovyUnusedDeclaration"])
    String myHandler(RequestInput params, Context context) {
        LambdaLogger logger = context.getLogger()

        def input = params.input
        logger.log("received : " + input)

        Settings conversionSettings
        try {
            conversionSettings = params.settings ?
                new ObjectMapper().readValue(params.settings, ConversionSettings).toSettings() :
                settings()
        } catch (any) {
            return JsonOutput.toJson([
                error: any.message
            ])
        }

        CompilerConfiguration config = new CompilerConfiguration()
        config.addCompilationCustomizers(new ASTTransformationCustomizer(CompileStatic))

        Class klass
        try {
            klass = new GroovyClassLoader(this.getClass().classLoader, config).parseClass(input)
        }
        catch (any) {
            return JsonOutput.toJson([
                error: any.message
            ])
        }

        final String output = new TypeScriptGenerator(conversionSettings).generateTypeScript(Input.from(klass))

        return JsonOutput.toJson([
            output: output
        ])
    }
}
