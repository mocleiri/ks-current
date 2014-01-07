package com.sigmasys.kuali.ksa.config;

import com.sigmasys.kuali.ksa.exception.ConfigurationException;
import com.sigmasys.kuali.ksa.model.ConfigParameter;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.util.CommonUtils;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * InitialParameterConfigurer.
 * It is used in Spring context to set parameter values in ${...} variables and
 * also by ConfigService to populate reference data.
 * <p/>
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.SUPPORTS)
public class InitialParameterConfigurer extends PropertyPlaceholderConfigurer {

    private static final Log logger = LogFactory.getLog(InitialParameterConfigurer.class);

    private final List<ConfigParameter> databaseParameters = new LinkedList<ConfigParameter>();
    private final Properties readOnlyProperties = new Properties();

    private String configTableName;
    private NamedParameterJdbcTemplate jdbcTemplate;

    private Config riceConfig;


    public InitialParameterConfigurer(String configTableName) {
        this(configTableName, Ordered.LOWEST_PRECEDENCE);
    }

    public InitialParameterConfigurer(String configTableName, int order) {
        if (StringUtils.isBlank(configTableName)) {
            String errMsg = "Configuration table name is required";
            logger.error(errMsg);
            throw new ConfigurationException(errMsg);
        }
        setConfigTableName(configTableName);
        setOrder(order);
    }

    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        loadDatabaseParameters();
    }

    public void setRiceConfig(Config riceConfig) {
        this.riceConfig = riceConfig;
    }

    private void validateJdbcTemplate() {
        if (jdbcTemplate == null) {
            logger.error("jdbcTemplate is null");
            throw new ConfigurationException("jdbcTemplate is null");
        }
    }

    public void loadDatabaseParameters() {

        validateJdbcTemplate();

        databaseParameters.clear();

        StringBuilder queryBuilder = new StringBuilder("select NAME as name, VALUE as value, LOCKED as locked from ");
        queryBuilder.append(getConfigTableName());

        jdbcTemplate.query(queryBuilder.toString(), new HashMap<String, Object>(), new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                String name = rs.getString("name");
                String value = rs.getString("value");
                String locked = rs.getString("locked");
                boolean isLocked = (locked != null) && locked.equalsIgnoreCase("Y");
                databaseParameters.add(new ConfigParameter(name, value, false, isLocked));
            }
        });
    }

    @Override
    protected void loadProperties(Properties props) throws IOException {

        // Loading local properties first and saving them as "read-only"
        super.loadProperties(props);

        readOnlyProperties.putAll(props);

        // Local properties should have the higher priority than DB parameters so we have to add them
        // to the final properties at the very end.
        for (ConfigParameter databaseParameter : databaseParameters) {
            String name = databaseParameter.getName();
            String value = CommonUtils.nvl(databaseParameter.getValue());
            props.setProperty(name, value);
        }

        props.putAll(readOnlyProperties);

        logger.debug("Initial parameters = " + props);

        if (riceConfig != null) {

            logger.debug("Initializing rice configuration...");

            // Additional properties set up
            String hostName = props.getProperty(Constants.RICE_APPLICATION_HOST);
            if (hostName == null) {
                hostName = RequestUtils.getIPAddress();
            }

            logger.debug("Setting the Rice application host name to " + hostName);

            riceConfig.putProperty(Constants.RICE_APPLICATION_HOST, hostName);

            String messageEnable = props.getProperty(Constants.RICE_MESSAGING_ENABLED);
            if (messageEnable != null) {
                riceConfig.putProperty(Constants.RICE_MESSAGING_ENABLED, messageEnable);
            }

            riceConfig.parseConfig();
            ConfigContext.init(riceConfig);

            logger.debug("Rice configuration has been initialized");

        }
    }

    private Map<String, String> getParameters(Properties props) {
        Map<String, String> initialParamMap = new HashMap<String, String>(props.size());
        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            initialParamMap.put(name, value);
        }
        return initialParamMap;
    }

    private Map<String, String> getParameters(List<ConfigParameter> parameters) {
        Map<String, String> initialParamMap = new HashMap<String, String>(parameters.size());
        for (ConfigParameter parameter : parameters) {
            initialParamMap.put(parameter.getName(), parameter.getValue());
        }
        return initialParamMap;
    }

    public Map<String, String> getParameterMap() {
        // Local properties should have the higher priority than DB parameters
        Map<String, String> initialParams = new HashMap<String, String>(getDatabaseParameters());
        initialParams.putAll(getReadOnlyParameters());
        return initialParams;
    }

    public Map<String, String> getDatabaseParameters() {
        return getParameters(databaseParameters);
    }

    public Map<String, String> getReadOnlyParameters() {
        return getParameters(readOnlyProperties);
    }

    public List<ConfigParameter> getParameters() {

        Map<String, String> readOnlyParams = getReadOnlyParameters();

        Set<String> readOnlyParamNames = readOnlyParams.keySet();

        List<ConfigParameter> params = new ArrayList<ConfigParameter>(databaseParameters.size() + readOnlyParams.size());

        for (ConfigParameter databaseParameter : databaseParameters) {
            if (!readOnlyParamNames.contains(databaseParameter.getName())) {
                params.add(databaseParameter);
            }
        }

        for (Map.Entry<String, String> entry : readOnlyParams.entrySet()) {
            params.add(new ConfigParameter(entry.getKey(), entry.getValue(), true));
        }

        Collections.sort(params);

        return params;
    }

    public int deleteInitialParameters(Set<String> paramNames) {
        int deleted = deleteDatabaseParameters(paramNames);
        loadDatabaseParameters();
        return deleted;
    }

    private int deleteDatabaseParameters(Set<String> paramNames) {

        validateJdbcTemplate();

        List<String> paramNameList = new ArrayList<String>(paramNames);

        logger.debug("Deleting the following properties from " + getConfigTableName() + " : " + paramNameList);

        if (!paramNameList.isEmpty()) {

            StringBuilder queryBuilder = new StringBuilder("delete from ");
            queryBuilder.append(getConfigTableName());
            queryBuilder.append(" where NAME in (");

            Map<String, String> parameterMap = new HashMap<String, String>(paramNameList.size());

            for (int i = 0; i < paramNameList.size(); i++) {
                if (i > 0) {
                    queryBuilder.append(",");
                }
                String paramName = "p" + i;
                queryBuilder.append(":").append(paramName);
                parameterMap.put(paramName, paramNameList.get(i));
            }

            queryBuilder.append(")");

            return jdbcTemplate.update(queryBuilder.toString(), parameterMap);
        }

        return 0;
    }

    public int updateInitialParameters(List<ConfigParameter> params) {
        int updated = updateDatabaseParameters(params);
        loadDatabaseParameters();
        return updated;
    }

    private int updateDatabaseParameters(List<ConfigParameter> params) {

        validateJdbcTemplate();

        logger.debug("Deleting the following parameters from " + getConfigTableName() + " : " + params);

        // Deleting the old parameters within the same transaction
        Set<String> paramNames = new HashSet<String>(params.size());
        for (ConfigParameter param : params) {
            paramNames.add(param.getName());
        }

        deleteInitialParameters(paramNames);

        // Assembling the batch parameters for insert statements
        List<Map<String, String>> batchParams = new LinkedList<Map<String, String>>();
        for (ConfigParameter param : params) {
            if (!param.isReadOnly()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", param.getName());
                map.put("value", param.getValue());
                map.put("locked", param.isLocked() ? "Y" : "N");
                batchParams.add(map);
            }
        }

        if (!batchParams.isEmpty()) {

            logger.debug("Inserting the following properties into " + getConfigTableName() + " : " + batchParams);

            StringBuilder queryBuilder = new StringBuilder("insert into ");
            queryBuilder.append(getConfigTableName());
            queryBuilder.append(" (NAME, VALUE, LOCKED) values (:name, :value, :locked)");

            Map<String, ?>[] batchParamArray = new Map[batchParams.size()];

            int[] values = jdbcTemplate.batchUpdate(queryBuilder.toString(), batchParams.toArray(batchParamArray));

            int insertedRows = 0;
            for (int value : values) {
                insertedRows += value;
            }

            return insertedRows;
        }

        return 0;
    }

    public void setConfigTableName(String configTableName) {
        this.configTableName = configTableName;
    }

    public String getConfigTableName() {
        return configTableName;
    }
}
