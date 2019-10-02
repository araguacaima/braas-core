package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.Constants;
import com.araguacaima.braas.core.drools.factory.*;
import org.apache.commons.lang.StringUtils;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.*;
import org.kie.internal.builder.conf.DefaultPackageNameOption;
import org.kie.internal.builder.conf.TrimCellsInDTableOption;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Map;

/**
 * Created by Alejandro on 01/12/2014.
 */
public class KieSessionFactory {

    private static final Logger log = LoggerFactory.getLogger(KieSessionFactory.class);

    public static KieSessionImpl getSession(DroolsUtils droolsUtils)
            throws IOException {
        Map<String, Object> globals = droolsUtils.getGlobals();
        DroolsConfig droolsConfig = droolsUtils.getDroolsConfig();
        final String kieSessionType = droolsConfig.getKieSessionType();
        final String kieSession = droolsConfig.getKieSession();
        boolean verbose = droolsConfig.isVerbose();
        final String rulesRepositoryStrategy = droolsConfig.getRulesRepositoryStrategy();
        final KieSession session;
        final StatelessKieSession statelessSession;
        String url = droolsConfig.getUrl();
        String rulesTabName = droolsConfig.getRulesTabName();
        URLClassLoader classLoader = droolsConfig.getClassLoader();
        if (Constants.RULES_SESSION_TYPE.STATEFUL.name().equalsIgnoreCase(kieSessionType)) {
            if (Constants.RULES_REPOSITORY_STRATEGIES.DRL.name().equalsIgnoreCase(rulesRepositoryStrategy)) {
                KieContainer kieContainer = droolsUtils.getKieContainer();
                session = kieContainer.newKieSession(kieSession);
                return new KieStatefulDrlSessionImpl(session, verbose, globals);
            } else if (Constants.RULES_REPOSITORY_STRATEGIES.DECISION_TABLE.name().equalsIgnoreCase(
                    rulesRepositoryStrategy)) {
                try {
                    InternalKnowledgeBase knowledgeBase = createKnowledgeBaseFromSpreadsheet(url, classLoader, rulesTabName);
                    session = knowledgeBase.newKieSession();
                    return new KieStatefulDecisionTableSessionImpl(session, verbose, globals);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(
                        "Rules Repository Strategy can be only of types DRL and DECISION_TABLE");
            }
        } else if (Constants.RULES_SESSION_TYPE.STATELESS.name().equalsIgnoreCase(kieSessionType)) {
            if (Constants.RULES_REPOSITORY_STRATEGIES.DRL.name().equalsIgnoreCase(rulesRepositoryStrategy)) {
                KieContainer kieContainer = droolsUtils.getKieContainer();
                return new KieStatelessDrlSessionImpl(kieContainer.newStatelessKieSession(kieSession),
                        verbose,
                        globals);
            } else if (Constants.RULES_REPOSITORY_STRATEGIES.DECISION_TABLE.name().equalsIgnoreCase(
                    rulesRepositoryStrategy)) {
                try {
                    InternalKnowledgeBase knowledgeBase;
                    if (url != null) {
                        log.info("Using url: " + url);
                        knowledgeBase = createKnowledgeBaseFromSpreadsheet(url, classLoader, rulesTabName);
                    } else {
                        ByteArrayOutputStream spreadsheetStream = droolsConfig.getSpreadsheetStream();
                        knowledgeBase = createKnowledgeBaseFromSpreadsheet(spreadsheetStream, classLoader, rulesTabName);
                    }
                    log.info("knowledge base created!");
                    statelessSession = knowledgeBase.newStatelessKieSession();
                    return new KieStatelessDecisionTableSessionImpl(statelessSession, verbose, globals);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(
                        "Rules Repository Strategy can be only of types DRL and DECISION_TABLE");
            }
        } else {
            throw new IllegalArgumentException("Kie Session's can be only of types STATEFUL and STATELESS");
        }
    }

    private static InternalKnowledgeBase getInternalKnowledgeBase(DecisionTableConfiguration dtconf, KnowledgeBuilder knowledgeBuilder, Resource resource, URLClassLoader classLoader) throws IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, classLoader);
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(conf);
        knowledgeBuilder.add(resource, ResourceType.DTABLE, dtconf);
        log.info("Resource added to knowledge builder");
        if (knowledgeBuilder.hasErrors()) {
            log.info("Knowledge builder detected some error in rules definition");
            throw new RuntimeException(knowledgeBuilder.getErrors().toString());
        }
        log.info("Knowledge builder does not detect any error in rules definition");
        knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
        return knowledgeBase;
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(ByteArrayOutputStream spreadsheetStream, String rulesTabName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return createKnowledgeBaseFromSpreadsheet(spreadsheetStream, null, rulesTabName);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(ByteArrayOutputStream spreadsheetStream) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return createKnowledgeBaseFromSpreadsheet(spreadsheetStream, null, null);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(ByteArrayOutputStream spreadsheetStream, URLClassLoader classLoader, String rulesTabName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        dtconf.setWorksheetName(StringUtils.isNotBlank(rulesTabName) ? rulesTabName : DroolsUtils.RULES_TABLES_DEFAULT_NAME);
        dtconf.setTrimCell(false);
        KnowledgeBuilderConfiguration configuration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
        configuration.setOption(TrimCellsInDTableOption.DISABLED);
        configuration.setProperty(DefaultPackageNameOption.PROPERTY_NAME, "com.araguacaima.braas");
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(configuration);
        log.info("Retrieving resource...");
        Resource resource = ResourceFactory.newByteArrayResource(spreadsheetStream.toByteArray());
        log.info("Resource retrieved");
        return getInternalKnowledgeBase(dtconf, knowledgeBuilder, resource, classLoader);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(String path, String rulesTabName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return createKnowledgeBaseFromSpreadsheet(path, null, rulesTabName);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(String path) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        return createKnowledgeBaseFromSpreadsheet(path, null, null);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(String path, URLClassLoader classLoader, String rulesTabName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        dtconf.setTrimCell(false);
        dtconf.setWorksheetName(StringUtils.isNotBlank(rulesTabName) ? rulesTabName : DroolsUtils.RULES_TABLES_DEFAULT_NAME);
        KnowledgeBuilderConfiguration configuration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoader);
        configuration.setOption(TrimCellsInDTableOption.DISABLED);
        configuration.setProperty(DefaultPackageNameOption.PROPERTY_NAME, "com.araguacaima.braas");
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(configuration);
        Resource resource;
        File file = new File(path);
        if (file.exists()) {
            resource = ResourceFactory.newFileResource(file);
        } else {
            resource = ResourceFactory.newClassPathResource(path);
        }
        return getInternalKnowledgeBase(dtconf, knowledgeBuilder, resource, classLoader);
    }
}
