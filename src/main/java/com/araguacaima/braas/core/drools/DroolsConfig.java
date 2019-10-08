package com.araguacaima.braas.core.drools;

import com.araguacaima.commons.utils.FileUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.LocaleUtils;
import org.codehaus.plexus.util.StringUtils;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.util.Base64;
import java.util.Locale;
import java.util.Properties;

import static com.araguacaima.braas.core.Commons.propertiesHandlerUtils;

/**
 * Created by Alejandro on 04/12/2014.
 */

public class DroolsConfig {

    public static final String DEFAULT_RULESHEET_NAME = ExcelParser.DEFAULT_RULESHEET_NAME;
    private static final Logger log = LoggerFactory.getLogger(DroolsConfig.class);
    private String rulesPath;
    private String credentialsPath = System.getProperty("user.home") + File.separator + ".braas" + File.separator + "credentials.json";
    private String appName;
    private String artifactName;
    private String artifactId;
    private String decisionTablePath;
    private String groupId;
    private String kieSession;
    private String kieSessionType;
    private String mavenLocalRepositoryPath;
    private String port;
    private String protocol;
    private String rulesRepositoryStrategy;
    private String scannerPeriod;
    private String server;
    private String url;
    private String rulesTabName = DEFAULT_RULESHEET_NAME;
    @JsonIgnore
    private ByteArrayOutputStream spreadsheetStream;
    @JsonIgnore
    private InputStream credentialsStream;
    private String urlResourceStrategy;
    private boolean verbose;
    private String version;
    private String credentialStrategy;
    @JsonIgnore
    private URLClassLoader classLoader;
    private Locale locale;

    public DroolsConfig(Properties bundle) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        this.build("rulesPath", bundle.getProperty("rulesPath"))
                .build("credentialsPath", bundle.getProperty("credentialsPath"))
                .build("drools.workbench.app.name", bundle.getProperty("drools.workbench.app.name"))
                .build("artifactName", bundle.getProperty("artifactName"))
                .build("drools.maven.artifactid", bundle.getProperty("drools.maven.artifactid"))
                .build("decision.table.path", bundle.getProperty("decision.table.path"))
                .build("drools.maven.groupid", bundle.getProperty("drools.maven.groupid"))
                .build("drools.kie.session", bundle.getProperty("drools.kie.session"))
                .build("drools.kie.session.type", bundle.getProperty("drools.kie.session.type"))
                .build("mavenLocalRepositoryPath", bundle.getProperty("mavenLocalRepositoryPath"))
                .build("drools.workbench.server.port", bundle.getProperty("drools.workbench.server.port"))
                .build("drools.workbench.server.protocol", bundle.getProperty("drools.workbench.server.protocol"))
                .build("rules.repository.strategy", bundle.getProperty("rules.repository.strategy"))
                .build("scannerPeriod", bundle.getProperty("scannerPeriod"))
                .build("drools.workbench.server.name", bundle.getProperty("drools.workbench.server.name"))
                .build("urlResourceStrategy", bundle.getProperty("urlResourceStrategy"))
                .build("drools.engine.verbose", bundle.getProperty("drools.engine.verbose"))
                .build("drools.maven.version", bundle.getProperty("drools.maven.version"))
                .build("credentialStrategy", bundle.getProperty("credentialStrategy"))
                .build("locale", bundle.getProperty("locale"))
                .build("rulesTabName", bundle.getProperty("rulesTabName"));
    }

    public DroolsConfig(String configFile) throws FileNotFoundException, URISyntaxException, MalformedURLException {
        this(propertiesHandlerUtils.getHandler(configFile).getProperties());
    }

    public DroolsConfig build(String key, String value) throws FileNotFoundException, MalformedURLException, URISyntaxException {

        if ("urlResourceStrategy".equals(key)) {
            this.setUrlResourceStrategy(value);
        } else if ("mavenLocalRepositoryPath".equals(key)) {
            this.setMavenLocalRepositoryPath(value);
        } else if ("drools.workbench.server.protocol".equals(key)) {
            this.setProtocol(value);
        } else if ("drools.workbench.server.name".equals(key)) {
            this.setServer(value);
        } else if ("drools.workbench.server.port".equals(key)) {
            this.setPort(value);
        } else if ("drools.workbench.app.name".equals(key)) {
            this.setAppName(value);
        } else if ("drools.kie.session".equals(key)) {
            this.setKieSession(value);
        } else if ("drools.kie.session.type".equals(key)) {
            this.setKieSessionType(value);
        } else if ("drools.maven.groupid".equals(key)) {
            this.setGroupId(value);
        } else if ("drools.maven.artifactid".equals(key)) {
            this.setArtifactId(value);
        } else if ("drools.maven.version".equals(key)) {
            this.setVersion(value);
        } else if ("drools.engine.verbose".equals(key)) {
            this.setVerbose(Boolean.valueOf(value));
        } else if ("rulesPath".equals(key)) {
            this.setRulesPath(value);
        } else if ("credentialsPath".equals(key)) {
            if (StringUtils.isNotBlank(value)) {
                this.setCredentialsPath(value);
            }
        } else if ("artifactName".equals(key)) {
            this.setArtifactName(value);
        } else if ("scannerPeriod".equals(key)) {
            this.setScannerPeriod(value);
        } else if ("rules.repository.strategy".equals(key)) {
            this.setRulesRepositoryStrategy(value);
        } else if ("decision.table.path".equals(key)) {
            this.setDecisionTablePath(value);
        } else if ("credentialStrategy".equals(key)) {
            this.setCredentialStrategy(value);
        } else if ("locale".equals(key)) {
            this.setLocale(value);
        } else if ("rulesTabName".equals(key)) {
            this.setRulesTabName(value);
        }
        return this;
    }

    public String getRulesTabName() {
        return rulesTabName;
    }

    public void setRulesTabName(String rulesTabName) {
        this.rulesTabName = rulesTabName;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(String localeStr) {
        if (StringUtils.isNotBlank(localeStr)) {
            this.locale = LocaleUtils.toLocale(localeStr);
        }
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getRulesPath() {
        return rulesPath;
    }

    public void setRulesPath(String rulesPath) {
        this.rulesPath = rulesPath;
    }

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) throws FileNotFoundException {
        this.credentialsPath = credentialsPath;
        if (this.credentialsPath != null) {
            try {
                File file = FileUtils.getFile("./" + this.credentialsPath);
                log.info("credentials found in '" + file.getCanonicalPath() + "'!");
                credentialsStream = new FileInputStream(file);
            } catch (Throwable t1) {
                log.info("credentialsPath of '" + this.credentialsPath + "' not found!");
                throw new FileNotFoundException("credentialsPath of '" + this.credentialsPath + "' not found!");
            }
        }
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getDecisionTablePath() {
        return decisionTablePath;
    }

    public void setDecisionTablePath(String decisionTablePath) {
        this.decisionTablePath = decisionTablePath;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getKieSession() {
        return kieSession;
    }

    public void setKieSession(String kieSession) {
        this.kieSession = kieSession;
    }

    public String getKieSessionType() {
        return kieSessionType;
    }

    public void setKieSessionType(String kieSessionType) {
        this.kieSessionType = kieSessionType;
    }

    public String getMavenLocalRepositoryPath() {
        return mavenLocalRepositoryPath;
    }

    public void setMavenLocalRepositoryPath(String mavenLocalRepositoryPath) {
        this.mavenLocalRepositoryPath = mavenLocalRepositoryPath;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRulesRepositoryStrategy() {
        return rulesRepositoryStrategy;
    }

    public void setRulesRepositoryStrategy(String rulesRepositoryStrategy) {
        this.rulesRepositoryStrategy = rulesRepositoryStrategy;
    }

    public String getScannerPeriod() {
        return scannerPeriod;
    }

    public void setScannerPeriod(String scannerPeriod) {
        this.scannerPeriod = scannerPeriod;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ByteArrayOutputStream getSpreadsheetStream() {
        return spreadsheetStream;
    }

    public void setSpreadsheetStream(ByteArrayOutputStream spreadsheetStream) {
        this.spreadsheetStream = spreadsheetStream;
    }

    public void setSpreadsheetStreamFromString(String spreadsheetAsBase64String) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
        byte[] spreadsheetBytes = Base64.getDecoder().decode(spreadsheetAsBase64String);
        out.write(spreadsheetBytes);
        byteArrayOutputStream.flush();
        byteArrayOutputStream.close();
        this.spreadsheetStream = byteArrayOutputStream;
    }

    public InputStream getCredentialsStream() {
        return credentialsStream;
    }

    public void setCredentialsStream(InputStream credentialsStream) {
        this.credentialsStream = credentialsStream;
    }

    public String getUrlResourceStrategy() {
        return urlResourceStrategy;
    }

    public void setUrlResourceStrategy(String urlResourceStrategy) {
        this.urlResourceStrategy = urlResourceStrategy;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getCredentialStrategy() {
        return credentialStrategy;
    }

    public void setCredentialStrategy(String credentialStrategy) {
        this.credentialStrategy = credentialStrategy;
    }

    public URLClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
