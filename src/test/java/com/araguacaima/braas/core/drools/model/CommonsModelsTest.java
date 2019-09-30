package com.araguacaima.braas.core.drools.model;

import com.araguacaima.braas.core.drools.model.forms.Form;
import com.araguacaima.commons.utils.JsonUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.LinkedHashSet;

public class CommonsModelsTest {

    private static final JsonUtils jsonUtils = new JsonUtils();
    private Class clazz = Form.class;

    @Test
    public void testBuildJsonPath() {
        LinkedHashSet<String> result = jsonUtils.buildJsonPath(clazz);
        System.out.println(StringUtils.join(result, "\n"));
    }
}
