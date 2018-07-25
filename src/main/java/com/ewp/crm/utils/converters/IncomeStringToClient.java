package com.ewp.crm.utils.converters;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialNetwork;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class IncomeStringToClient {

    private final SocialNetworkTypeService socialNetworkTypeService;

    private static final Logger logger = LoggerFactory.getLogger(IncomeStringToClient.class);

    private final String[] testAnswers = new String[]{"2", "1", "3", "2", "3", "4"};

    @Autowired
    public IncomeStringToClient(SocialNetworkTypeService socialNetworkTypeService) {
        this.socialNetworkTypeService = socialNetworkTypeService;
    }

    public Client convert(String income) {
        Client client = null;
//        income = "<br>Страница: <b>http://www.java-mentor.com/index.html</b> <br />Форма: <b>Java Test</b> <br />1: <b>2</b> <br />2: <b>1</b> <br />3: <b>4</b> <br />4: <b>4</b> <br />5: <b>4</b> <br />6: <b>4</b> <br />Имя: <b>testew343</b> <br />Phone 6: <b>8911545422259</b> <br />City 6: <b>Россия</b> <br />Email 2: <b>mcdn3434343@gmail.com</b> <br />";
        if (income != null && !income.isEmpty()) {
            String workString = prepareForm(income);
            if (income.contains("Начать обучение")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Месяц в подарок")) {
                client = parseClientFormOne(workString);
            } else if (income.contains("Остались вопросы")) {
                client = parseClientFormTwo(workString);
            } else if (income.contains("Java Test")) {
                client = parseClientFormFour(workString);
            } else {
                logger.error("The incoming email does not match any of the templates!!!");
            }
        }
        return client;
    }

    private static String prepareForm(String text) {
        return text.substring(text.indexOf("Форма:"), text.length())
                .replaceAll("<b>|</b>|(\\r\\n|\\n)", "");
    }

    private Client parseClientFormOne(String form) {
        Client client = new Client();
        String removeExtraCharacters = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email");
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Name"));
        client.setPhoneNumber(clientData.get("Телефон"));
        client.setCountry(clientData.get("Страна"));
        client.setCity(clientData.get("Город"));
        if (clientData.containsKey("Соцсеть")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(clientData.get("Соцсеть"))));
        }
        if (form.contains("Согласен")) {
            client.setEmail(clientData.get("Email"));
        } else {
            client.setEmail(clientData.get("Email"));
            client.setClientDescriptionComment("На пробные 3 дня");
        }
        return client;
    }

    private Client parseClientFormTwo(String form) {
        Client client = new Client();
        String removeExtraCharacters = form.substring(form.indexOf("Name"), form.length())
                .replaceAll(" ", "")
                .replaceAll("Name[0-9]", "Name")
                .replaceAll("Email[0-9]", "Email");
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Name"));
        client.setPhoneNumber(clientData.get("Phone"));
        client.setClientDescriptionComment(clientData.get("Vopros"));
        if (clientData.containsKey("Social")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(clientData.get("Social"))));
        }
        return client;
    }

    private Client parseClientFormFour(String form) {
        Client client = new Client();
        String replaceSpaceInString = form.replaceAll(" ", "");
        String parseStart = "JavaTest<br/>";
        String removeExtraCharacters = replaceSpaceInString.substring(replaceSpaceInString.indexOf(parseStart) + parseStart.length(), replaceSpaceInString.length());
        String[] createArrayFromString = removeExtraCharacters.split("<br/>");
        Map<String, String> clientData = createMapFromClientData(createArrayFromString);
        setClientName(client, clientData.get("Имя"));
        if (clientData.containsKey("Social2")) {
            client.setSocialNetworks(Collections.singletonList(getSocialNetwork(clientData.get("Social2"))));
        }
        client.setPhoneNumber(clientData.get("Phone6"));
        client.setCountry(clientData.get("City6"));
        client.setEmail(clientData.get("Email2"));
        client.setClientDescriptionComment("Проходил Тест");
        client.setTestResult(getTestResult(clientData));
        return client;
    }

    private SocialNetwork getSocialNetwork(String link) {
        SocialNetwork socialNetwork = new SocialNetwork();
        if (link.contains("vk.com") || link.contains("m.vk.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("vk"));
        } else if (link.startsWith("www.facebook.com") || link.startsWith("m.facebook.com")) {
            socialNetwork.setLink(link);
            socialNetwork.setSocialNetworkType(socialNetworkTypeService.getByTypeName("facebook"));
        } else {
            socialNetwork = null;
        }
        return socialNetwork;
    }

    private Map<String, String> createMapFromClientData(String[] res) {
        Map<String, String> clientData = new HashMap<>();
        for (int i = 0; i < res.length; i++) {
            String name = res[i].substring(0, res[i].indexOf(":"));
            String value = res[i].substring(res[i].indexOf(":") + 1, res[i].length());
            clientData.put(name, value);
        }
        return clientData;
    }

    private void setClientName(Client client, String fullName) {
        if (StringUtils.countOccurrencesOf(fullName, " ") == 1) {
            String[] full = fullName.split(" ");
            client.setName(full[0]);
            client.setLastName(full[1]);
        } else {
            client.setName(fullName);
        }
    }

    private int getTestResult(Map<String,String> clientData) {
        double rightAnswers = 0;

        Integer i = 1;
        for (String answer : testAnswers) {
            String clientAnswer = clientData.get(i.toString());
            if (clientAnswer != null && clientAnswer.equals(answer)){
                rightAnswers++;
            }
            i++;
        }

        return (int)Math.ceil(rightAnswers * 100 / testAnswers.length);
    }
}
