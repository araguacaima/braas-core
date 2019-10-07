package com.araguacaima.braas.core.drools;

import com.araguacaima.braas.core.drools.factory.KieSessionImpl;
import com.araguacaima.braas.core.drools.factory.ResourceStrategyFactory;
import com.araguacaima.braas.core.drools.strategy.ResourceStrategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;

import static com.araguacaima.braas.core.Commons.reflectionUtils;

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

    private DroolsConfig droolsConfig;
    private Map<String, Object> globals = new HashMap<>();

    public DroolsUtils(DroolsConfig droolsConfig) {
        this.droolsConfig = droolsConfig;
        init();
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
        final List<DataListener> listeners = new ArrayList<>();
        final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
        String rulesTabName = droolsConfig.getRulesTabName();
        if (StringUtils.isBlank(rulesTabName)) {
            droolsConfig.setRulesTabName(DroolsConfig.DEFAULT_RULESHEET_NAME);
        }
        listener.setWorksheetName(droolsConfig.getRulesTabName());
        listeners.add(listener);
        final ExcelParser parser = new ExcelParser(listeners);

        try {
            Field _useFirstSheet = reflectionUtils.getField(ExcelParser.class, "_useFirstSheet");
            _useFirstSheet.setAccessible(true);
            _useFirstSheet.set(parser, false);
            try {
                ResourceStrategy urlResourceStrategy = ResourceStrategyFactory.getUrlResourceStrategy(droolsConfig);
                String url = urlResourceStrategy.buildUrl();
                if (url != null) {
                    droolsConfig.setUrl(url);
                    parser.parseFile(new File(droolsConfig.getUrl()));
                } else {
                    ByteArrayOutputStream stream = urlResourceStrategy.getStream();
                    droolsConfig.setSpreadsheetStream(stream);
                    parser.parseFile(new ByteArrayInputStream(stream.toByteArray()));
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            PropertiesSheetListener.CaseInsensitiveMap properties = listener.getProperties();
            final List<Global> variableList = RuleSheetParserUtil.getVariableList(properties.getProperty(DefaultRuleSheetListener.VARIABLES_TAG));
            if (CollectionUtils.isNotEmpty(variableList)) {
                variableList.forEach(variable -> {
                    String identifier = variable.getIdentifier();
                    String className = variable.getClassName();
                    try {
                        Object instance = reflectionUtils.deepInitialization(Class.forName(className));
                        addGlobal(identifier, instance);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
        return kieSessionImpl.execute(asset, expandLists);
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals) {
        this.globals = globals;
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
        KieModule kModule = kr.addKieModule(resource);

        KieContainer kContainer;
        if (droolsConfig.getClassLoader() == null) {
            kContainer = ks.newKieContainer(kModule.getReleaseId());
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
