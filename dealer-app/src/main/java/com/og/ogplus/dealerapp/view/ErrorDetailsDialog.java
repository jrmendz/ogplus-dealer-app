package com.og.ogplus.dealerapp.view;

import javax.swing.*;
import java.awt.*;

public class ErrorDetailsDialog extends JDialog {
    private final static int WIDTH = 600;
    private final static int HEIGHT = 400;

    private JLabel content;
    private JTextArea textArea;

    public ErrorDetailsDialog() {
        super((JFrame) null, "Error", true);
        setAlwaysOnTop(true);
        setIconImage(GameImages.LOGO);
        setLayout(new GridBagLayout());
        setResizable(false);
        addComponents();
        pack();
        setLocation((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2, (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2);
    }

    private void addComponents() {
        content = new JLabel("test");
        content.setFont(new Font("", Font.PLAIN, 36));
        content.setPreferredSize(new Dimension(WIDTH, 100));
        add(content,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 20, 0, 20), 0, 0));

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(WIDTH, HEIGHT - 100));
        add(scrollPane,
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    }

    public void show(String contentMessage, String errorMessage) {
        content.setText(contentMessage);
        textArea.setText(errorMessage);
        setVisible(true);
    }

    public static void main(String[] args) {
        new ErrorDetailsDialog().show("Error", "org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: Invocation of init method failed; nested exception is org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]\n" +
                "\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1778)\n" +
                "\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:593)\n" +
                "\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:515)\n" +
                "\tat org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:320)\n" +
                "\tat org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:222)\n" +
                "\tat org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:318)\n" +
                "\tat org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)\n" +
                "\tat org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1105)\n" +
                "\tat org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:867)\n" +
                "\tat org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:549)\n" +
                "\tat org.springframework.boot.SpringApplication.refresh(SpringApplication.java:775)\n" +
                "\tat org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)\n" +
                "\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:316)\n" +
                "\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1260)\n" +
                "\tat org.springframework.boot.SpringApplication.run(SpringApplication.java:1248)\n" +
                "\tat com.og.panda.pitboss.PitBossApplication.main(PitBossApplication.java:27)\n" +
                "Caused by: org.hibernate.service.spi.ServiceException: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment]\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.createService(AbstractServiceRegistryImpl.java:275)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.initializeService(AbstractServiceRegistryImpl.java:237)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.getService(AbstractServiceRegistryImpl.java:214)\n" +
                "\tat org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory.injectServices(DefaultIdentifierGeneratorFactory.java:152)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.injectDependencies(AbstractServiceRegistryImpl.java:286)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.initializeService(AbstractServiceRegistryImpl.java:243)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.getService(AbstractServiceRegistryImpl.java:214)\n" +
                "\tat org.hibernate.boot.internal.InFlightMetadataCollectorImpl.<init>(InFlightMetadataCollectorImpl.java:179)\n" +
                "\tat org.hibernate.boot.model.process.spi.MetadataBuildingProcess.complete(MetadataBuildingProcess.java:119)\n" +
                "\tat org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.metadata(EntityManagerFactoryBuilderImpl.java:904)\n" +
                "\tat org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:935)\n" +
                "\tat org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:57)\n" +
                "\tat org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:365)\n" +
                "\tat org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:390)\n" +
                "\tat org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:377)\n" +
                "\tat org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:341)\n" +
                "\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1837)\n" +
                "\tat org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1774)\n" +
                "\t... 15 more\n" +
                "Caused by: org.hibernate.HibernateException: Access to DialectResolutionInfo cannot be null when 'hibernate.dialect' not set\n" +
                "\tat org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl.determineDialect(DialectFactoryImpl.java:100)\n" +
                "\tat org.hibernate.engine.jdbc.dialect.internal.DialectFactoryImpl.buildDialect(DialectFactoryImpl.java:54)\n" +
                "\tat org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:137)\n" +
                "\tat org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.initiateService(JdbcEnvironmentInitiator.java:35)\n" +
                "\tat org.hibernate.boot.registry.internal.StandardServiceRegistryImpl.initiateService(StandardServiceRegistryImpl.java:94)\n" +
                "\tat org.hibernate.service.internal.AbstractServiceRegistryImpl.createService(AbstractServiceRegistryImpl.java:263)\n" +
                "\t... 32 more\n");
    }
}
