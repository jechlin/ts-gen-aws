interface ConversionSettings {
    outputFile: string;
    outputFileType: TypeScriptFileType;
    outputKind: TypeScriptOutputKind;
    module: string;
    namespace: string;
    mapPackagesToNamespaces: boolean;
    umdNamespace: string;
    classes: string[];
    classPatterns: string[];
    classesFromJaxrsApplication: string;
    classesFromAutomaticJaxrsApplication: boolean;
    excludeClasses: string[];
    excludeClassPatterns: string[];
    includePropertyAnnotations: string[];
    jsonLibrary: JsonLibrary;
    declarePropertiesAsOptional: boolean;
    optionalProperties: OptionalProperties;
    declarePropertiesAsReadOnly: boolean;
    removeTypeNamePrefix: string;
    removeTypeNameSuffix: string;
    addTypeNamePrefix: string;
    addTypeNameSuffix: string;
    customTypeNaming: string[];
    customTypeNamingFunction: string;
    referencedFiles: string[];
    importDeclarations: string[];
    customTypeMappings: string[];
    mapDate: DateMapping;
    mapEnum: EnumMapping;
    nonConstEnums: boolean;
    mapClasses: ClassMapping;
    mapClassesAsClassesPatterns: string[];
    disableTaggedUnions: boolean;
    ignoreSwaggerAnnotations: boolean;
    generateJaxrsApplicationInterface: boolean;
    generateJaxrsApplicationClient: boolean;
    jaxrsNamespacing: JaxrsNamespacing;
    jaxrsNamespacingAnnotation: string;
    restResponseType: string;
    restOptionsType: string;
    customTypeProcessor: string;
    sortDeclarations: boolean;
    sortTypeDeclarations: boolean;
    noFileComment: boolean;
    javadocXmlFiles: any[];
    extensionClasses: string[];
    extensions: string[];
    optionalAnnotations: string[];
    generateNpmPackageJson: boolean;
    npmName: string;
    npmVersion: string;
    stringQuotes: StringQuotes;
    indentString: string;
    displaySerializerWarning: boolean;
    disableJackson2ModuleDiscovery: boolean;
    jackson2ModuleDiscovery: boolean;
    jackson2Modules: string[];
    debug: boolean;
}

type TypeScriptFileType = "declarationFile" | "implementationFile";

type TypeScriptOutputKind = "global" | "module" | "ambientModule";

type JsonLibrary = "jackson1" | "jackson2" | "jaxb";

type OptionalProperties = "useSpecifiedAnnotations" | "useLibraryDefinition" | "all";

type DateMapping = "asDate" | "asNumber" | "asString";

type EnumMapping = "asUnion" | "asInlineUnion" | "asEnum" | "asNumberBasedEnum";

type ClassMapping = "asInterfaces" | "asClasses";

type JaxrsNamespacing = "singleObject" | "perResource" | "byAnnotation";

type StringQuotes = "doubleQuotes" | "singleQuotes";
