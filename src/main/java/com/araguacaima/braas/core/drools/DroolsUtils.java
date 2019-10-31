package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.Constants;
import com.araguacaima.braas.core.drools.factory.KieSessionImpl;
import com.araguacaima.braas.core.drools.factory.ResourceStrategyFactory;
import com.araguacaima.braas.core.drools.strategy.ResourceStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.drools.compiler.compiler.DRLFactory;
import org.drools.compiler.lang.DRLParser;
import org.drools.compiler.lang.descr.GlobalDescr;
import org.drools.core.io.impl.UrlResource;
import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.decisiontable.parser.xls.PropertiesSheetListener;
import org.drools.template.model.Global;
import org.drools.template.parser.DataListener;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static com.araguacaima.braas.core.Commons.reflectionUtils;
import static com.araguacaima.braas.core.Constants.RULES_REPOSITORY_STRATEGIES.DECISION_TABLE;
import static com.araguacaima.braas.core.Constants.RULES_REPOSITORY_STRATEGIES.DRL;

/**
 * Clase utilitaria para manipular repositorio DRL de Jboss Drools <p>
 * Clase: DroolsUtils.java <br>
 *
 * @author Alejandro Manuel Méndez Araguacaima (AMMA)
 * Changes:<br>
 * <ul>
 * <li> 2014-11-26 (AMMA)  Creacion de la clase. </li>
 * </ul>
 */


public class DroolsUtils {

    private static final Logger log = LoggerFactory.getLogger(DroolsUtils.class);
    private DroolsConfig droolsConfig;
    private Map<String, Object> globals = new HashMap<>();
    private Collection<String> globalsFromRules = new ArrayList<>();

    public DroolsUtils(DroolsConfig droolsConfig) throws IllegalAccessException {
        this(droolsConfig, true);
    }

    public DroolsUtils(DroolsConfig droolsConfig, boolean initialize) throws IllegalAccessException {
        this.droolsConfig = droolsConfig;
        if (initialize) {
            init();
        } else {
            validate();
        }
    }

    public void addGlobal(String globalName, Object value) {
        this.globals.put(globalName, value);
    }

    public void addGlobals(Map<String, Object> globals) {
        this.globals.putAll(globals);
    }

    public void buildDroolsUtils(String key, String value)
            throws IOException, URISyntaxException {
        DroolsConfig droolsConfig = this.getDroolsConfig();
        droolsConfig.build(key, value);
        this.setDroolsConfig(droolsConfig);
        this.init();
    }

    public DroolsConfig getDroolsConfig() {
        return droolsConfig;
    }

    public void setDroolsConfig(DroolsConfig droolsConfig) {
        this.droolsConfig = droolsConfig;
    }

    @SuppressWarnings("ConstantConditions")
    private void init() {
        try {
            validate();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void validate() throws IllegalAccessException {
        String rulesRepositoryStrategy = droolsConfig.getRulesRepositoryStrategy();
        if (DECISION_TABLE.name().equals(rulesRepositoryStrategy)) {
            List<DataListener> listeners = new ArrayList<>();
            final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
            listeners.add(listener);
            PropertiesSheetListener.CaseInsensitiveMap properties = listener.getProperties();

            String rulesTabName = droolsConfig.getRulesTabName();
            if (StringUtils.isBlank(rulesTabName)) {
                droolsConfig.setRulesTabName(DroolsConfig.DEFAULT_RULESHEET_NAME);
            }
            final ExcelParser parser = new ExcelParser(listeners);

            Field _useFirstSheet = reflectionUtils.getField(ExcelParser.class, "_useFirstSheet");
            _useFirstSheet.setAccessible(true);
            _useFirstSheet.set(parser, false);
            try {
                ResourceStrategy urlResourceStrategy = ResourceStrategyFactory.getUrlResourceStrategy(droolsConfig);
                String url = urlResourceStrategy.buildUrl();
                if (url != null) {
                    ByteArrayOutputStream o = new ByteArrayOutputStream();
                    droolsConfig.setUrl(url);
                    IOUtils.copy(FileUtils.openInputStream(new File(droolsConfig.getUrl())), o);
                    droolsConfig.setSpreadsheetStream(o);
                } else {
                    ByteArrayOutputStream stream = urlResourceStrategy.getStream();
                    droolsConfig.setSpreadsheetStream(stream);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            parser.parseFile(new ByteArrayInputStream(droolsConfig.getSpreadsheetStream().toByteArray()));

            final List<Global> globalVariablesList = RuleSheetParserUtil.getVariableList(properties.getProperty(DefaultRuleSheetListener.VARIABLES_TAG));
            if (CollectionUtils.isNotEmpty(globalVariablesList)) {
                globalVariablesList.forEach(variable -> {
                    String identifier = variable.getIdentifier();
                    String className = variable.getClassName();
                    fixGlobals(identifier, className);
                });
            }
        } else if (DRL.name().equals(rulesRepositoryStrategy)) {
            ResourceStrategy urlResourceStrategy = ResourceStrategyFactory.getUrlResourceStrategy(droolsConfig);
            String url = urlResourceStrategy.buildUrl();
            if (url != null) {
                droolsConfig.setUrl(url);
            }
            try {
                URL url1 = new URL(droolsConfig.getUrl());
                String file = url1.getFile();
                InputStream is = FileUtils.openInputStream(new File(file));
                String encoding = "UTF-8";
                LanguageLevelOption languageLevel = LanguageLevelOption.DRL6;
                DRLParser parser = DRLFactory.buildParser(is, encoding, languageLevel);
                List<GlobalDescr> variableList = parser.compilationUnit().getGlobals();
                if (CollectionUtils.isNotEmpty(variableList)) {
                    variableList.forEach(variable -> {
                        String identifier = variable.getIdentifier();
                        String className = variable.getType();
                        fixGlobals(identifier, className);
                    });
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void fixGlobals(String identifier, String className) {
        try {
            Class<?> clazz = droolsConfig.getClassLoader().loadClass(className);
            if (reflectionUtils.isCollectionImplementation(clazz) || reflectionUtils.isMapImplementation(clazz)) {
                Object instance = reflectionUtils.deepInitialization(clazz);
                addGlobal(identifier, instance);
            } else {
                if (reflectionUtils.getFullyQualifiedJavaTypeOrNull(clazz) != null) {
                    addGlobal(identifier, clazz.newInstance());
                }
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            String message = "It's not possible to initialize global variable '" + identifier + "' due there is no Class named '" + className + "' or it has no an accesible constructor able to create a new object";
            log.error(message);
            throw new RuntimeException(message);
        }
        this.globalsFromRules.add(identifier);
    }

    public Collection<Object> executeRules(Object assets)
            throws Exception {
        return executeRules(assets, globals, true);
    }

    public Collection<Object> executeRules(boolean expandLists, Object assets)
            throws Exception {
        return executeRules(assets, globals, expandLists);
    }

    public Collection<Object> executeRules(Object asset, Map<String, Object> globals, boolean expandLists)
            throws Exception {
        KieSessionImpl kieSessionImpl = KieSessionFactory.getSession(this);
        if (globals != null && !globals.isEmpty()) {
            for (Map.Entry<String, Object> entry : globals.entrySet()) {
                kieSessionImpl.setGlobal(entry.getKey(), entry.getValue());
            }
        }
        Collection<Object> result = kieSessionImpl.execute(asset, expandLists);
        return result;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals) {
        this.globals = globals;
    }

    public Collection<String> getGlobalsFromRules() {
        return globalsFromRules;
    }

    KieContainer getKieContainer()
            throws IOException {

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieResources resources = ks.getResources();
        InputStream is;
        String url = droolsConfig.getUrl();
        if (url != null) {
            UrlResource urlResource = (UrlResource) resources.newUrlResource(url);
            is = urlResource.getInputStream();
        } else {
            is = new ByteArrayInputStream(droolsConfig.getSpreadsheetStream().toByteArray());
        }
        final Resource resource = resources.newInputStreamResource(is);
        KieContainer kContainer;
        if (droolsConfig.getClassLoader() == null) {
            if (Constants.URL_RESOURCE_STRATEGIES.ABSOLUTE_DRL_FILE_OR_DIRECTORY.name().equals(droolsConfig.getRulesRepositoryStrategy())) {
                Class<? extends DroolsUtils> aClass = this.getClass();
                ReleaseId releaseId = ks.newReleaseId(aClass.getPackage().getName(), droolsConfig.getArtifactName().replaceAll("\\.", "-"), UUID.randomUUID().toString());
                kContainer = ks.newKieContainer(releaseId);
            } else {
                KieModule kModule = kr.addKieModule(resource);
                kContainer = ks.newKieContainer(kModule.getReleaseId());
            }
        } else {
            kContainer = ks.getKieClasspathContainer(droolsConfig.getClassLoader());
        }
        try {
            Long scannerPeriod = Long.valueOf(droolsConfig.getScannerPeriod());
            KieScanner kieScanner = ks.newKieScanner(kContainer);
            if (scannerPeriod != null) {
                kieScanner.start(50000L);
            }
        } catch (NumberFormatException ignored) {
        }
        return kContainer;
    }
}
