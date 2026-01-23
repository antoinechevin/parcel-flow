package com.parcelflow;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/DashboardList.feature")
@SelectClasspathResource("features/DashboardAggregation.feature")
@SelectClasspathResource("features/DashboardUrgency.feature")
@SelectClasspathResource("features/mail-adapter.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.parcelflow.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
public class CucumberTest {
}
