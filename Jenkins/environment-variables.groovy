import hudson.EnvVars
import hudson.slaves.EnvironmentVariablesNodeProperty
import hudson.slaves.NodeProperty
import hudson.slaves.NodePropertyDescriptor
import hudson.util.DescribableList
import jenkins.model.Jenkins

public createGlobalEnvironmentVariables(String key, String value) {

    Jenkins jenkins = Jenkins.getInstance();

    DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties = jenkins.getGlobalNodeProperties();
    List<EnvironmentVariablesNodeProperty> envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);

    EnvironmentVariablesNodeProperty newEnvVarsNodeProperty = null;
    EnvVars envVars = null;

    if (envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0) {
        newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
        globalNodeProperties.add(newEnvVarsNodeProperty);
        envVars = newEnvVarsNodeProperty.getEnvVars();
    } else {
        envVars = envVarsNodePropertyList.get(0).getEnvVars();
    }
    envVars.put(key, value)
    jenkins.save()
}

def env = System.getenv()

createGlobalEnvironmentVariables('HEROKU_API_KEY', env.HEROKU_API_KEY)
createGlobalEnvironmentVariables('HEROKU_APP_NAME', env.HEROKU_APP_NAME)