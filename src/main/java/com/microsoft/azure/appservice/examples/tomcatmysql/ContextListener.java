package com.microsoft.azure.appservice.examples.tomcatmysql;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

    private static Logger logger = LogManager.getLogger(ContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.info("Initializing WebListener.");

        Map<String, String> props = new HashMap<String, String>();

        // If a connection string exists as an app setting in App Service, then use the
        // autogenerated JNDI data source for the persistence unit.
        String azureDbUrl= System.getenv("AZURE_MYSQL_CONNECTIONSTRING");
        if (azureDbUrl!=null) {
            logger.info("Detected Azure MySQL connection string. Adding Tomcat data source...");
            props.put("jakarta.persistence.nonJtaDataSource", "java:comp/env/jdbc/AZURE_MYSQL_CONNECTIONSTRING_DS");
        }

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("defaultpu", props);
        sce.getServletContext().setAttribute("EMFactory", emf);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        logger.info("Destroying WebListener.");

        EntityManagerFactory emf = (EntityManagerFactory) sce.getServletContext().getAttribute("EMFactory");
        if(emf.isOpen()) {
            emf.close();
        }
    }
}
