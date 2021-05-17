package com.feri.alessandro.attsw.project.e2e;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/e2e/resources/registration_and_login.feature", monochrome = true)
public class RegistrationAndLoginE2E {

	@BeforeClass
	public static void setUpDriver() {
		WebDriverManager.firefoxdriver().setup();
	}
}
