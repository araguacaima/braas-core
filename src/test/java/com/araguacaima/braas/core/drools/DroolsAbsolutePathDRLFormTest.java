package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import com.araguacaima.braas.core.drools.model.forms.Form;
import com.araguacaima.commons.utils.JsonUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class DroolsAbsolutePathDRLFormTest {

    private static Logger log = LoggerFactory.getLogger(DroolsAbsolutePathDRLFormTest.class);
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
        URL resource = DroolsAbsolutePathDRLFormTest.class.getClassLoader().getResource("drools-absolute-drl-file-or-directory.properties");
        String fullconfigFile = resource.toURI().getPath();
        if (fullconfigFile.startsWith(String.valueOf(File.separatorChar))) {
            fullconfigFile = fullconfigFile.substring(1);
        }
        DroolsConfig droolsConfig = new DroolsConfig(fullconfigFile);
        ClassLoader classLoader = Form.class.getClassLoader();
        URL rootPath = classLoader.getResource(".");
        File rootDirectory = new File(rootPath.getFile());
        droolsConfig.setRulesPath(rootDirectory.getAbsolutePath());
        File ruleFilePath = new File(rootDirectory, droolsConfig.getArtifactName());
        droolsConfig.setUrl(ruleFilePath.getAbsolutePath());
        URL resource1 = classLoader.getResource("./" + Form.class.getPackage().getName().replaceAll("\\.", "/"));
        droolsConfig.setClassLoader(new DroolsURLClassLoader(resource1, classLoader));
        droolsUtils = new DroolsUtils(droolsConfig);
    }

    @Test
    public void testSingleRule() throws Exception {
        String form_ = "{\"id\":\"7ff61900-cc7d-43f6-9eef-7b8d4eae3114\",\"locale\":\"ES\",\"questions\":[{\"id\":\"638a93e9-0d6d-4d80-8b13-25ad232206b0\",\"options\":[{\"id\":\"eba69599-e22f-46b6-b6bd-e162744f75c6\"},{\"id\":\"b5bfb950-bccb-4580-97ea-82ca34327460\"}]}]}";
        Form form = jsonUtils.fromJSON(form_, Form.class);
        Collection<Object> result = droolsUtils.executeRules(form);
        log.debug(jsonUtils.toJSON(result));
    }
}
