package com.araguacaima.braas.drools;

import com.araguacaima.braas.IMessage;
import com.araguacaima.braas.drools.Model.Person;
import com.araguacaima.braas.drools.factory.KieStatelessDrlSessionImpl;
import com.araguacaima.commons.utils.JsonUtils;
import io.codearte.jfairy.Fairy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.StatelessKieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class RulesTest {

    private static Logger log = LoggerFactory.getLogger(RulesTest.class);
    private static final boolean VERBOSE = false;
    private KieStatelessDrlSessionImpl ksession;

    private Collection<Object> facts = new ArrayList<>();
    private JsonUtils jsonUtils;
    private Locale locale = Locale.ENGLISH;
    private Predicate predicateNonPerson = object -> !Person.class.isAssignableFrom(object.getClass());
    private Predicate transformerComments = input -> {
        IMessage message = (IMessage) input;
        String language = message.getLanguage();
        String localeLanguage = locale.getLanguage();
        return localeLanguage.equals(language);
    };
    private static final String PERSON_FIRST_NAME_CANNOT_BE_NULL = "Person firstName cannot be null";
    private static final String PERSON_FIRST_NAME_CANNOT_BE_EMPTY = "Person firstName cannot be empty";
    private static final String PERSON_EMAIL_CANNOT_BE_NULL = "Person email cannot be null";
    private static final String PERSON_EMAIL_CANNOT_BE_NULL_OR_EMPTY = "Person email cannot be null or empty";
    private static final String PERSON_EMAIL_SHOULD_BE_VALID = "Person email should be valid";
    private static final String OK_PREFIX_BEFORE = "@|bold,green [OK]|@ Testing @|bold ";
    private static final String OK_PREFIX_AFTER = "|@: @|italic,underline ";
    private static final String OK_CHECK_PREFIX = "@|bold,green ";
    private static final String FAIL_PREFIX_BEFORE = "@|bold,red [KO]|@ Testing @|bold ";
    private static final String FAIL_PREFIX_AFTER = "|@: @|italic,underline ";
    private static final String FAIL_CHECK_PREFIX = "@|bold,red ";
    private static final String END = "|@";
    private static final String MIDDLE_SCORE = " - ";
    private static final String BLANK_SPACE = " ";
    private static final String PIPE = " | ";
    private Collection<String> ALL_NULL_COMMENTS = new ArrayList<String>() {{
        add(PERSON_FIRST_NAME_CANNOT_BE_NULL);
        add(PERSON_EMAIL_CANNOT_BE_NULL_OR_EMPTY);
    }};
    private DroolsConfig droolsConfig = null;
    private DroolsUtils droolsUtils = null;
    private Fairy fairy;

    @Before
    public void init()
            throws Exception {

        droolsConfig = new DroolsConfig();
        droolsUtils = new DroolsUtils(droolsConfig);

        jsonUtils = new JsonUtils();
        String path = "rules.xlsx";
        InternalKnowledgeBase knowledgeBase = KieSessionFactory.createKnowledgeBaseFromSpreadsheet(path);
        StatelessKieSession session = knowledgeBase.newStatelessKieSession();
        Map<String, Object> globals = new HashMap<>();
        globals.put("locale", locale);
        globals.put("logger", log);
        this.ksession = new KieStatelessDrlSessionImpl(session, VERBOSE, globals);

        fairy = Fairy.create();
    }

    @Test
    public void testPersonFirstNameNullity() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        facts.add(person);
        final String condition = "is null";
        final String attribute = "person.firstName";
        person.setFirstName(null);
        Collection<Object> result = ksession.execute(facts, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should fail due firstName is null",
                    comments.contains(PERSON_FIRST_NAME_CANNOT_BE_NULL));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }

    @Test
    public void testPersonFirstNameEmpty() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        facts.add(person);
        final String condition = "is empty";
        final String attribute = "person.firstName";
        person.setFirstName("");
        Collection<Object> result = ksession.execute(facts, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should fail due firstName is empty",
                    comments.contains(PERSON_FIRST_NAME_CANNOT_BE_EMPTY));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }

    @Test
    public void testPersonEmailNullity() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        facts.add(person);
        final String condition = "is null";
        final String attribute = "person.email";
        person.setEmail(null);
        Collection<Object> result = ksession.execute(facts, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should fail due email is null",
                    comments.contains(PERSON_FIRST_NAME_CANNOT_BE_NULL));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }

    @Test
    public void testPersonEmailEmpty() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        facts.add(person);
        final String condition = "is empty";
        final String attribute = "person.email";
        person.setEmail("");
        Collection<Object> result = ksession.execute(facts, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should fail due email is empty",
                    comments.contains(PERSON_FIRST_NAME_CANNOT_BE_EMPTY));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }

    @Test
    public void testPersonEmailValid() throws Exception {
        Person person = Person.PersonWrapper.fromParent(fairy.person());
        facts.add(person);
        final String condition = "is valid";
        final String attribute = "person.email";
        Collection<Object> result = ksession.execute(facts, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should fail due email is invalid",
                    comments.contains(PERSON_FIRST_NAME_CANNOT_BE_EMPTY));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }
    
    @Test
    public void testNullPerson() throws Exception {

        facts.clear();
        final String attribute = "person";
        final String condition = "is empty";
        Collection<Object> result = ksession.execute(null, true);
        Collection comments = new ArrayList();
        if (result.size() > 1) {
            comments = getMessages(result);
        }
        try {
            Assert.assertTrue("It should not fail due " + attribute + " " + condition,
                    CollectionUtils.isEqualCollection(comments, ALL_NULL_COMMENTS));
            log.info(getOK(attribute, condition, comments));
        } catch (AssertionError ae) {
            log.error(getFail(attribute, condition, comments));
            throw ae;
        }
    }

    @Test
    public void testValidateTestObject()
            throws Exception {
        Person person = (Person) fairy.person();
        final Collection<IMessage> inputCollection = droolsUtils.executeRules(true, person);
        Collection<IMessage> messages = CollectionUtils.select(inputCollection,
                object -> IMessage.class.isAssignableFrom(object.getClass()));
        int expectedObjectcount = 0;
        Assert.assertNotNull(messages);
        Assert.assertEquals(messages.size(), expectedObjectcount);
    }

    private String getFail(String attribute, String message, Collection comments)
            throws IOException {
        return FAIL_PREFIX_BEFORE + attribute + FAIL_PREFIX_AFTER + message + END + PIPE + FAIL_CHECK_PREFIX
                + "X" + END + (CollectionUtils.isNotEmpty(
                comments) ? (PIPE + "@|bold Messages:|@" + jsonUtils.toJSON(comments)) : StringUtils.EMPTY);
    }

    private String getOK(String attribute, String message, Collection comments)
            throws IOException {
        return OK_PREFIX_BEFORE + attribute + OK_PREFIX_AFTER + message + END + PIPE + OK_CHECK_PREFIX + "✓"
                + END + (CollectionUtils.isNotEmpty(
                comments) ? (PIPE + "@|bold Messages:|@" + jsonUtils.toJSON(comments)) : StringUtils.EMPTY);
    }

    private Collection getMessages(Collection result) {
        Collection collection = CollectionUtils.select(result, predicateNonPerson);
        CollectionUtils.filter(collection, transformerComments);
        return collection;
    }
}
