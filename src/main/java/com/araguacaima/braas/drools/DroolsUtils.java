package com.araguacaima.braas.drools;

import com.araguacaima.braas.drools.factory.KieSessionImpl;
import com.araguacaima.braas.drools.factory.UrlResourceStrategyFactory;
import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScanner;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Object> globals = new HashMap<String, Object>();
    private String url;


    public DroolsUtils(DroolsConfig droolsConfig) {
        this.droolsConfig = droolsConfig;
    }

    public void addGlobal(String globalName, Object value) {
        this.globals.put(globalName, value);
    }

    public void addGlobals(Map<String, Object> globals) {
        this.globals.putAll(globals);
    }

    public void buildDroolsUtils(String key, String value)
            throws IOException {
        DroolsConfig droolsConfig = this.getDroolsConfig();
        droolsConfig.buildDroolsConfig(key, value);
        this.setDroolsConfig(droolsConfig);
        this.init();
    }

    public DroolsConfig getDroolsConfig() {
        return droolsConfig;
    }

    public void setDroolsConfig(DroolsConfig droolsConfig) {
        this.droolsConfig = droolsConfig;
    }

    @PostConstruct
    public void init()
            throws IOException {
        this.url = UrlResourceStrategyFactory.getUrlResourceStrategy(droolsConfig).buildUrl();
        droolsConfig.setUrl(this.url);
    }

    public <T> Collection<T> executeRules(boolean expandLists, Object... assets)
            throws Exception {
        return executeRules(assets, globals, expandLists);
    }

    public <T> Collection<T> executeRules(Object asset, Map<String, Object> globals, boolean expandLists)
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
        UrlResource urlResource = (UrlResource) resources.newUrlResource(url);

        InputStream is = urlResource.getInputStream();
        final Resource resource = resources.newInputStreamResource(is);
        KieModule kModule = kr.addKieModule(resource);

        KieContainer kContainer = ks.newKieContainer(kModule.getReleaseId());
        try {
            Long scannerPeriod = Long.valueOf(this.droolsConfig.getScannerPeriod());

            if (scannerPeriod != null) {
                KieScanner kieScanner = ks.newKieScanner(kContainer);
                kieScanner.start(50000L);
            }
        } catch (NumberFormatException ignored) {
        }
        return kContainer;
    }
}
