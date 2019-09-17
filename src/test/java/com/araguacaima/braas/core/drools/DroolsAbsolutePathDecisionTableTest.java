package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import com.araguacaima.braas.core.drools.Model.Person;
import com.araguacaima.commons.utils.JsonUtils;
import io.codearte.jfairy.Fairy;
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
public class DroolsAbsolutePathDecisionTableTest {

    private static final String PERSON_FIRST_NAME_CANNOT_BE_NULL = "Person's first name cannot be null";
    private static final String PERSON_FIRST_NAME_CANNOT_BE_EMPTY = "Person's first name cannot be empty";
    private static final String PERSON_EMAIL_CANNOT_BE_NULL = "Person's email cannot be null";
    private static final String PERSON_EMAIL_CANNOT_BE_EMPTY = "Person's email cannot be empty";
    private static final String PERSON_EMAIL_IS_INVALID = "Person's email is not valid";
    private static Logger log = LoggerFactory.getLogger(DroolsAbsolutePathDecisionTableTest.class);
    private JsonUtils jsonUtils;
    private Locale locale = Locale.ENGLISH;
    private Predicate predicateMessage = object -> RuleMessage.class.isAssignableFrom(object.getClass());
    private Predicate transformerLocalizedComments = input -> {
        IMessage message = (IMessage) input;
        String language = message.getLanguage();
        String localeLanguage = locale.getLanguage();
        return localeLanguage.equals(language);
    };
    private DroolsUtils droolsUtils = null;
    private Fairy fairy;

    @SuppressWarnings("ConstantConditions")
    @Before
    public void init() throws FileNotFoundException, MalformedURLException, URISyntaxException {
        URL resource = DroolsAbsolutePathDecisionTableTest.class.getClassLoader().getResource("./drools-absolute-path-decision-table.properties");
        String fullconfigFile = resource.toURI().getPath();
        if (OSValidator.isWindows() && fullconfigFile.startsWith("/")) {
            fullconfigFile = fullconfigFile.substring(1);
        }
        DroolsConfig droolsConfig = new DroolsConfig(fullconfigFile);
        droolsUtils = new DroolsUtils(droolsConfig);
        jsonUtils = new JsonUtils();
        fairy = Fairy.create();
    }

    @Test
    public void testPersonFirstNameNullity() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        person.setFirstName(null);
        Collection comments = droolsUtils.executeRules(person);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due firstName is null, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due firstName is null");
        CollectionUtils.filter(comments, comment -> PERSON_FIRST_NAME_CANNOT_BE_NULL.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testPersonFirstNameEmpty() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        person.setFirstName("");
        Collection comments = droolsUtils.executeRules(person);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due firstName is empty, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due firstName is empty");
        CollectionUtils.filter(comments, comment -> PERSON_FIRST_NAME_CANNOT_BE_EMPTY.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testPersonEmailNullity() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        person.setEmail(null);
        Collection comments = droolsUtils.executeRules(person);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due email is null, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due email is null");
        CollectionUtils.filter(comments, comment -> PERSON_EMAIL_CANNOT_BE_NULL.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testPersonEmailEmpty() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        person.setEmail("");
        Collection comments = droolsUtils.executeRules(person);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due email is empty, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due email is empty");
        CollectionUtils.filter(comments, comment -> PERSON_EMAIL_CANNOT_BE_EMPTY.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testPersonEmailInvalid() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        person.setEmail("@.com");
        Collection comments = droolsUtils.executeRules(person);
        if (comments.size() == 0) {
            Assert.fail("It should be returned an error message due email is not valid, but no error was retrieved");
        }
        comments = getMessages(comments);
        log.debug("It should be returned an error message due email is not valid");
        CollectionUtils.filter(comments, comment -> PERSON_EMAIL_IS_INVALID.equals(((RuleMessage) comment).getComment()));
        Assert.assertEquals(1, comments.size());
        RuleMessage message = (RuleMessage) comments.iterator().next();
        log.info("Message returned: " + message.getComment());
        log.info(jsonUtils.toJSON(message));
    }

    @Test
    public void testNullPerson() throws Exception {
        Collection<Object> result = droolsUtils.executeRules(null);
        Collection comments = new ArrayList();
        if (result != null) {
            if (result.size() > 0) {
                Assert.fail("It should not be returned any error message due there is no facts provided, but some errors was retrieved");
                log.info(jsonUtils.toJSON(comments));
            }
        }
        log.debug("It should not be returned any error message due there is no facts provided");
        Assert.assertEquals(0, comments.size());
        log.info("Amount of message returned: " + comments.size());
    }

    @Test
    public void testValidatePerson()
            throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        Collection<Object> result = droolsUtils.executeRules(person);
        Collection comments = new ArrayList();
        if (result.size() == 1) {
            comments = getMessages(result);
        } else {
            Assert.fail("It should not be returned an error message due person provided is valid, but some error was retrieved");
            log.info(jsonUtils.toJSON(comments));
        }
        log.debug("It should not be returned an error message due person provided is valid");
        Assert.assertEquals(0, comments.size());
        log.info("Amount of message returned: " + comments.size());
    }

    private Collection getMessages(Collection result) {
        Collection collection = CollectionUtils.select(result, predicateMessage);
        CollectionUtils.filter(collection, transformerLocalizedComments);
        return collection;
    }
}
