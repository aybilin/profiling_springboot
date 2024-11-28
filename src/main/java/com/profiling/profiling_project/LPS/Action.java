package com.profiling.profiling_project.LPS;

import java.util.Map;

public class Action {
    private String method;
    private Map<String, Object> params;

    // Constructeur, getters et setters
    public Action(String method, Map<String, Object> params) {
        this.method = method;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
