package com.novalang.runtime.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class JavaFunctionDescriptor extends JavaSymbolDescriptor {
    private final List<JavaParameterDescriptor> parameters;
    private final JavaTypeRef returnType;
    private final JavaFunctionInvoker invoker;

    public JavaFunctionDescriptor(String name,
                                  List<JavaParameterDescriptor> parameters,
                                  JavaTypeRef returnType,
                                  String documentation,
                                  String deprecatedMessage,
                                  List<String> examples,
                                  JavaFunctionInvoker invoker) {
        super(name, JavaSymbolKind.FUNCTION, documentation, deprecatedMessage, examples);
        this.parameters = Collections.unmodifiableList(new ArrayList<JavaParameterDescriptor>(parameters != null ? parameters : Collections.<JavaParameterDescriptor>emptyList()));
        this.returnType = returnType != null ? returnType : JavaTypeRefs.UNIT;
        this.invoker = invoker;
    }

    public List<JavaParameterDescriptor> getParameters() {
        return parameters;
    }

    public JavaTypeRef getReturnType() {
        return returnType;
    }

    public JavaFunctionInvoker getInvoker() {
        return invoker;
    }

    public boolean isVararg() {
        return !parameters.isEmpty() && parameters.get(parameters.size() - 1).isVararg();
    }
}
