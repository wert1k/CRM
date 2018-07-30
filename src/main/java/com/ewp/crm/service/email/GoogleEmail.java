package com.ewp.crm.service.email;

import com.ewp.crm.models.User;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.configs.inteface.MailConfig;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.utils.converters.IncomeStringToClient;
import org.apache.commons.mail.util.MimeMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mail.ImapIdleChannelAdapter;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.security.core.context.SecurityContextHolder;


import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableIntegration
public class GoogleEmail {

    private String login;
    private String password;
    private String mailFrom;
    private String socketFactoryClass;
    private String socketFactoryFallback;
    private String protocol;
    private String debug;
    private String imapServer;

    private final BeanFactory beanFactory;
    private final ClientService clientService;
    private final StatusService statusService;
    private final IncomeStringToClient incomeStringToClient;
    private final ClientHistoryService clientHistoryService;
    private final MailSendService prepareAndSend;


    private static Logger logger = LoggerFactory.getLogger(GoogleEmail.class);

    @Autowired
    public GoogleEmail(MailSendService prepareAndSend, MailConfig mailConfig, BeanFactory beanFactory, ClientService clientService, StatusService statusService, IncomeStringToClient incomeStringToClient, ClientHistoryService clientHistoryService, VKService vkService) {
        this.beanFactory = beanFactory;
        this.clientService = clientService;
        this.statusService = statusService;
        this.incomeStringToClient = incomeStringToClient;

        login = mailConfig.getLogin();
        password = mailConfig.getPassword();
        mailFrom = mailConfig.getMailFrom();
        socketFactoryClass = mailConfig.getSocketFactoryClass();
        socketFactoryFallback = mailConfig.getSocketFactoryFallback();
        protocol = mailConfig.getProtocol();
        debug = mailConfig.getDebug();
        imapServer = mailConfig.getImapServer();
	    this.clientHistoryService = clientHistoryService;
        this.prepareAndSend = prepareAndSend;
    }

    private Properties javaMailProperties() {
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.imap.socketFactory.class", socketFactoryClass);
        javaMailProperties.setProperty("mail.imap.socketFactory.fallback", socketFactoryFallback);
        javaMailProperties.setProperty("mail.store.protocol", protocol);
        javaMailProperties.setProperty("mail.debug", debug);

        return javaMailProperties;
    }

    @Bean
    public ImapIdleChannelAdapter mailAdapter() {
        ImapMailReceiver mailReceiver = new ImapMailReceiver("imaps://" + login + ":" + password + "@" + imapServer);
        mailReceiver.setJavaMailProperties(javaMailProperties());
        mailReceiver.setShouldDeleteMessages(false);

        mailReceiver.setShouldMarkMessagesAsRead(true);
        mailReceiver.setCancelIdleInterval(1200);
        mailReceiver.setBeanFactory(beanFactory);
        mailReceiver.setSearchTermStrategy(this::fromAndNotSeenTerm);
        mailReceiver.afterPropertiesSet();

        ImapIdleChannelAdapter imapIdleChannelAdapter = new ImapIdleChannelAdapter(mailReceiver);
        imapIdleChannelAdapter.setAutoStartup(true);
        imapIdleChannelAdapter.setShouldReconnectAutomatically(true);
        imapIdleChannelAdapter.setOutputChannel(directChannel());
        imapIdleChannelAdapter.afterPropertiesSet();

        return imapIdleChannelAdapter;
    }

    @Bean
    public DirectChannel directChannel() {
        DirectChannel directChannel = new DirectChannel();
        directChannel.subscribe(message -> {
            MimeMessageParser parser = new MimeMessageParser((MimeMessage) message.getPayload());
            try {
                logger.info("start parsing income email", parser.getHtmlContent());
                parser.parse();

                Client client = incomeStringToClient.convert(parser.getHtmlContent());
                if (client != null) {
                    //--------------------------
                    if (parser.getHtmlContent().contains("Java Test")) {
                        Long list = validatorTestResult(parser.getPlainContent());
                        if (list > 0) {
                            System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "Поздравляем! Вы ответили правильно на: \n\n" + list + "% вопросов!");
                        } else {
                            System.out.println("Java-Mentor.ru" + client.getEmail() + "Test complete!" + "К сожалению вы не ответили ни на один вопрос верно...");
                        }
                    //--------------------------
                }
                    client.setStatus(statusService.get(1L));
                    client.addHistory(clientHistoryService.createHistory("GMail"));
                    clientService.addClient(client);
                }
            } catch (Exception e) {
                logger.error("MimeMessageParser can't parse income data ", e);
            }
        });
        return directChannel;
    }


    private static Long validatorTestResult(String parseContent) {
        String parseTest = parseContent;
        String indexQuery = parseTest.substring(parseTest.indexOf(" Java Test") + 1, parseTest.indexOf("6:") + 5)
                .replace("Java Test ", "")
                .replaceAll(": \\d+ ", "");

        String listQuery = parseTest.substring(parseTest.indexOf(" Java Test") + 1, parseTest.indexOf("6:") + 5)
                .replace("Java Test", "")
                .replaceAll(" \\d+: ", "");

        List<Integer> result = Arrays.asList(3, 3, 1, 2, 3, 4);
        List<Integer> resultList = Arrays.asList(indexQuery.split("\\s*\\s*")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        List<Integer> resultTest = Arrays.asList(listQuery.split("\\s*\\s*")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());

        System.out.println(resultList);
        System.out.println(resultTest);

        for (int q = 1, count = 0, i = 0; i < result.size(); i++) {
            if (result.size() > i & resultTest.size() > i) {
                if (resultList.get(i).equals(i + q)) {
                    if (result.get(i + q - 1).equals(resultTest.get(i))) {
                        count++;
                        System.out.println(count);
                        if ((resultTest.size()) - i == 1) {
                            return (long) ((count * 100) / resultTest.size());
                        }
                    }
                    if ((resultList.size()) - i == 1 & count == 0) {
                        return 0L;
                    }
                }
                if (!resultList.get(i + 1).equals(i + q + 1)) {
                    q++;
                }
            }
        }
        return 0L;
    }

    private SearchTerm fromAndNotSeenTerm(Flags supportedFlags, Folder folder) {
        Optional<InternetAddress> internetAddress = Optional.empty();
        try {
            internetAddress = Optional.of(new InternetAddress(mailFrom));
        } catch (AddressException e) {
            logger.error("Can't parse email address \"from\"", e);
        }
        FromTerm fromTerm = new FromTerm(internetAddress.orElse(new InternetAddress()));
        return new AndTerm(fromTerm, new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    }
}
