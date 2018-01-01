package org.jamieechlin.ts

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import cz.habarta.typescript.generator.*
import groovy.json.JsonOutput
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
            def objectMapper = new ObjectMapper()
            objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true)
            conversionSettings = params.settings ?
                objectMapper.readValue(params.settings, ConversionSettings).toSettings() :
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
