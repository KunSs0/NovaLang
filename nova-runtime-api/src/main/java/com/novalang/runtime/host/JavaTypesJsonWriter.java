package com.novalang.runtime.host;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class JavaTypesJsonWriter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JavaTypesJsonWriter() {}

    public static void write(JavaTypes registry, Path path) throws IOException {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        if (path == null) {
            throw new IllegalArgumentException("path must not be null");
        }

        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(toJsonElement(registry), writer);
        }
    }

    public static String toJson(JavaTypes registry) {
        if (registry == null) {
            throw new IllegalArgumentException("registry must not be null");
        }
        return GSON.toJson(toJsonElement(registry));
    }

    private static JsonObject toJsonElement(JavaTypes registry) {
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.add("globals", toSymbolsArray(registry.globals()));
        root.add("extensions", toExtensionsArray(registry.extensions()));
        root.add("extensionProperties", toExtensionPropertiesArray(registry.extensionProperties()));

        JsonObject namespaces = new JsonObject();
        for (Map.Entry<String, JavaNamespaceDescriptor> entry : registry.namespaces().entrySet()) {
            JavaNamespaceDescriptor namespace = entry.getValue();
            JsonObject namespaceObj = new JsonObject();

            JsonArray extendsArray = new JsonArray();
            for (String parent : namespace.getExtendsNamespaces()) {
                extendsArray.add(parent);
            }
            if (extendsArray.size() > 0) {
                namespaceObj.add("extends", extendsArray);
            }
            namespaceObj.add("globals", toSymbolsArray(namespace.getGlobals()));
            namespaceObj.add("extensions", toExtensionsArray(namespace.getExtensions()));
            namespaceObj.add("extensionProperties",
                    toExtensionPropertiesArray(namespace.getExtensionProperties()));
            namespaces.add(entry.getKey(), namespaceObj);
        }
        root.add("namespaces", namespaces);
        return root;
    }

    private static JsonArray toExtensionsArray(Iterable<JavaExtensionDescriptor> extensions) {
        JsonArray array = new JsonArray();
        for (JavaExtensionDescriptor extension : extensions) {
            JsonObject object = new JsonObject();
            object.addProperty("targetType", extension.getTargetType().getName());
            object.add("function", toSymbolObject(extension.getFunction()));
            array.add(object);
        }
        return array;
    }

    private static JsonArray toExtensionPropertiesArray(
            Iterable<JavaExtensionPropertyDescriptor> properties) {
        JsonArray array = new JsonArray();
        for (JavaExtensionPropertyDescriptor extension : properties) {
            JsonObject object = new JsonObject();
            object.addProperty("targetType", extension.getTargetType().getName());
            object.add("property", toSymbolObject(extension.getProperty()));
            array.add(object);
        }
        return array;
    }

    private static JsonArray toSymbolsArray(Iterable<JavaSymbolDescriptor> symbols) {
        JsonArray array = new JsonArray();
        for (JavaSymbolDescriptor symbol : symbols) {
            array.add(toSymbolObject(symbol));
        }
        return array;
    }

    private static JsonObject toSymbolObject(JavaSymbolDescriptor symbol) {
        JsonObject object = new JsonObject();
        object.addProperty("name", symbol.getName());
        object.addProperty("kind", symbol.getKind().name().toLowerCase());

        if (symbol.getDocumentation() != null) {
            object.addProperty("documentation", symbol.getDocumentation());
        }
        if (symbol.getDeprecatedMessage() != null) {
            object.addProperty("deprecated", symbol.getDeprecatedMessage());
        }
        if (!symbol.getExamples().isEmpty()) {
            JsonArray examples = new JsonArray();
            for (String example : symbol.getExamples()) {
                examples.add(example);
            }
            object.add("examples", examples);
        }

        if (symbol instanceof JavaVariableDescriptor) {
            JavaVariableDescriptor variable = (JavaVariableDescriptor) symbol;
            object.addProperty("type", variable.getType().displayName());
            object.addProperty("mutable", variable.isMutable());
        } else if (symbol instanceof JavaPropertyDescriptor) {
            JavaPropertyDescriptor property = (JavaPropertyDescriptor) symbol;
            object.addProperty("type", property.getType().displayName());
            object.addProperty("mutable", property.isMutable());
        } else if (symbol instanceof JavaFunctionDescriptor) {
            JavaFunctionDescriptor function = (JavaFunctionDescriptor) symbol;
            JsonArray params = new JsonArray();
            for (JavaParameterDescriptor parameter : function.getParameters()) {
                JsonObject param = new JsonObject();
                param.addProperty("name", parameter.getName());
                if (parameter.getType() != null) {
                    param.addProperty("type", parameter.getType().displayName());
                }
                if (parameter.isVararg()) {
                    param.addProperty("vararg", true);
                }
                params.add(param);
            }
            object.add("parameters", params);
            object.addProperty("returnType", function.getReturnType().displayName());
        } else if (symbol instanceof JavaObjectDescriptor) {
            JavaObjectDescriptor hostObject = (JavaObjectDescriptor) symbol;
            object.addProperty("type", hostObject.getType().displayName());
            object.add("members", toSymbolsArray(hostObject.getMembers()));
        }

        return object;
    }
}
