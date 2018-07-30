package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import com.ewp.crm.models.Client;
import com.ewp.crm.utils.converters.IncomeStringToClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}
}