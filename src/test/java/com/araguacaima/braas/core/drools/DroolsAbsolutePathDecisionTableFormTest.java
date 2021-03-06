package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import com.araguacaima.braas.core.drools.model.forms.Form;
import com.araguacaima.commons.utils.JsonUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public void init() throws FileNotFoundException, MalformedURLException, URISyntaxException, IllegalAccessException {
        URL resource = DroolsAbsolutePathDecisionTableFormTest.class.getClassLoader().getResource("drools-absolute-path-decision-form-table.properties");
        String fullconfigFile = resource.toURI().getPath();
        if (fullconfigFile.startsWith(String.valueOf(File.separatorChar))) {
            fullconfigFile = fullconfigFile.substring(1);
        }
        DroolsConfig droolsConfig = new DroolsConfig(fullconfigFile);
        ClassLoader classLoader = Form.class.getClassLoader();
        URL resource1 = classLoader.getResource("./" + Form.class.getPackage().getName().replaceAll("\\.", "/"));
        droolsConfig.setClassLoader(new DroolsURLClassLoader(resource1, classLoader));
        droolsUtils = new DroolsUtils(droolsConfig);
    }

    @Test
    public void testValidateForm() throws Exception {
        Form form = jsonUtils.fromJSON("{\"id\":\"7ff61900-cc7d-43f6-9eef-7b8d4eae3114\",\"locale\":\"ES\",\"questions\":[{\"id\":\"638a93e9-0d6d-4d80-8b13-25ad232206b0\",\"options\":[{\"id\":\"eba69599-e22f-46b6-b6bd-e162744f75c6\"},{\"id\":\"b5bfb950-bccb-4580-97ea-82ca34327460\"}]}]}", Form.class);
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
