package com.araguacaima.braas.drools;

import com.araguacaima.braas.Constants;
import com.araguacaima.braas.drools.factory.*;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Alejandro on 01/12/2014.
 */
public class KieSessionFactory {
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
        if (Constants.RULES_SESSION_TYPE.STATEFUL.name().equalsIgnoreCase(kieSessionType)) {
            if (Constants.RULES_REPOSITORY_STRATEGIES.DRL.name().equalsIgnoreCase(rulesRepositoryStrategy)) {
                KieContainer kieContainer = droolsUtils.getKieContainer();
                session = kieContainer.newKieSession(kieSession);
                return new KieStatefulDrlSessionImpl(session, verbose, globals);
            } else if (Constants.RULES_REPOSITORY_STRATEGIES.DECISION_TABLE.name().equalsIgnoreCase(
                    rulesRepositoryStrategy)) {
                try {
                    InternalKnowledgeBase knowledgeBase = createKnowledgeBaseFromSpreadsheet(url);
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
                        knowledgeBase = createKnowledgeBaseFromSpreadsheet(url);
                    } else {
                        knowledgeBase = createKnowledgeBaseFromSpreadsheet(droolsConfig.getExcelStream());
                    }
                    statelessSession = knowledgeBase.newStatelessKieSession();
                    return new KieStatelessDecisionTableSessionImpl(statelessSession, verbose, globals);
                } catch (Exception e) {
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

    private static InternalKnowledgeBase getInternalKnowledgeBase(DecisionTableConfiguration dtconf, KnowledgeBuilder knowledgeBuilder, Resource resource) {
        knowledgeBuilder.add(resource, ResourceType.DTABLE, dtconf);
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException(knowledgeBuilder.getErrors().toString());
        }
        InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
        return knowledgeBase;
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(ByteArrayOutputStream excelStream) {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource = ResourceFactory.newByteArrayResource(excelStream.toByteArray());
        return getInternalKnowledgeBase(dtconf, knowledgeBuilder, resource);
    }

    public static InternalKnowledgeBase createKnowledgeBaseFromSpreadsheet(String path) {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Resource resource;
        File file = new File(path);
        if (file.exists()) {
            resource = ResourceFactory.newFileResource(file);
        } else {
            resource = ResourceFactory.newClassPathResource(path);
        }
        return getInternalKnowledgeBase(dtconf, knowledgeBuilder, resource);
    }
}
