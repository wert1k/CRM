package com.ewp.crm;

import com.ewp.crm.configs.initializer.DataInitializer;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.impl.SocialNetworkTypeServiceImpl;
import com.ewp.crm.utils.converters.IncomeStringToClient;
import org.codehaus.groovy.runtime.powerassert.SourceText;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

//@SpringBootApplication
public class CrmApplication {

	public static void main(String[] args) {
		IncomeStringToClient incomeStringToClient = new IncomeStringToClient();
		String parser = "<br>Страница: <b>http://www.java-mentor.com/index.html</b> <br />Форма: <b>Java Test</b> <br />1: <b>2</b> <br />2: <b>3</b> <br />3: <b>3</b> <br />4: <b>3</b> <br />5: <b>2</b> <br />6: <b>3</b> <br />Имя: <b>testew</b> <br />Social 2: <b>https://vk.com/andiovermide</b> <br />Phone 6: <b>89110812259</b> <br />City 6: <b>Россия</b> <br />Email 2: <b>mcdncrnc73@gmail.com</b> <br />";
		String parser2 = "Страница: http://www.java-mentor.com/index.html Форма: Java Test 1: 2 2: 1 3: 3 4: 2 5: 3 6: 4 Имя: testew Social 2: https://vk.com/andiovermide Phone 6: 89110812259 City 6: Россия Email 2: mcdncrnc73@gmail.com";
		Client client = incomeStringToClient.convert(parser);
		if (client != null) {
			//--------------------------
			if (parser2.contains("Java Test")) {
				boolean list = validatorTestResult(parser2);
				if (list) {
					System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "Вы прошли тест!\n\n Результаты пройденого теста: \n\n");
				} else {
					System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "Вы не прошли тест! Java программист это не про вас)) \n\n Результаты пройденого теста: \n\n");
				}
			}
			//--------------------------
		}
//		SpringApplication.run(CrmApplication.class, args);
	}

	private static boolean validatorTestResult(String parseContent) {
		String parseTest = parseContent;
		String list = parseTest.substring(parseTest.indexOf(" Java Test") + 1, parseTest.indexOf("6:") + 5).replace("Java Test", "")
				.replaceAll(" \\d+: ", "");
		Arrays.asList(list.split("\\s*\\s*"));
		System.out.println();
		return false;
	}

	@Bean(initMethod = "init")
	public DataInitializer initTestData() {
		return new DataInitializer();
	}
}