package com.formz.utils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.StringWriter;
import java.util.Map;

@Component
public class FormzUtils {
    @Autowired
    private VelocityEngine velocityEngine;

    public String getTemplatetoText(String templateName, Map<String, Object> props) {
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate(templateName, "UTF-8", new VelocityContext(props), stringWriter);
        return stringWriter.toString();
    }
}
