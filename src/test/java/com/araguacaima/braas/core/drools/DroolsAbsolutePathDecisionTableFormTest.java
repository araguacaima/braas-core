package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import com.araguacaima.braas.core.drools.model.forms.Form;
import com.araguacaima.commons.utils.JsonUtils;
import com.araguacaima.commons.utils.OSValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class DroolsAbsolutePathDecisionTableFormTest {

    private static Logger log = LoggerFactory.getLogger(DroolsAbsolutePathDecisionTableFormTest.class);
    private JsonUtils jsonUtils = new JsonUtils();
    private Locale locale = Locale.ENGLISH;
    private Predicate predicateMessage = object -> RuleMessage.class.isAssignableFrom(object.getClass());
    private Predicate transformerLocalizedComments = input -> {
        IMessage message = (IMessage) input;
        String language = message.getLanguage();
        String localeLanguage = locale.getLanguage();
        return localeLanguage.equals(language);
    };
    private DroolsUtils droolsUtils = null;


    @SuppressWarnings("ConstantConditions")
    @Before
    public void init() throws FileNotFoundException, MalformedURLException, URISyntaxException {
        URL resource = DroolsAbsolutePathDecisionTableFormTest.class.getClassLoader().getResource("drools-absolute-path-decision-form-table.properties");
        String fullconfigFile = resource.toURI().getPath();
        if (OSValidator.isWindows() && fullconfigFile.startsWith("/")) {
            fullconfigFile = fullconfigFile.substring(1);
        }
        DroolsConfig droolsConfig = new DroolsConfig(fullconfigFile);
        ClassLoader classLoader = Form.class.getClassLoader();
        URL resource1 = classLoader.getResource("./" + Form.class.getPackage().getName().replaceAll("\\.", "/"));
        droolsConfig.setClassLoader(new DroolsURLClassLoader(resource1, classLoader));
        droolsUtils = new DroolsUtils(droolsConfig);
    }

    @Test
    public void testValidateForm()
            throws Exception {
        Form form = jsonUtils.fromJSON("{\"id\":\"e122af1f-1a78-4245-864e-42ee1afbd43b\",\"locale\":\"EN\",\"questions\":[{\"id\":\"4e5a67c4-b02e-45dd-be94-d675b622f22a\",\"options\":[{\"title\":\"No\"}]}]}", Form.class);
        Collection<Object> result = droolsUtils.executeRules(form);
        Collection comments = new ArrayList();
        if (result.size() == 1) {
            comments = getMessages(result);
        } else {
            Assert.fail("It should not be returned an error message due form provided is valid, but some error was retrieved");
            log.info(jsonUtils.toJSON(comments));
        }
        log.debug("It should not be returned an error message due form provided is valid");
        Assert.assertEquals(0, comments.size());
        log.info("Amount of message returned: " + comments.size());
    }

    private Collection getMessages(Collection result) {
        Collection collection = CollectionUtils.select(result, predicateMessage);
        CollectionUtils.filter(collection, transformerLocalizedComments);
        return collection;
    }
}
