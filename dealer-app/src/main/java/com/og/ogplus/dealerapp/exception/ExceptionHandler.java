package com.og.ogplus.dealerapp.exception;

import com.og.ogplus.dealerapp.config.AppConfig;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.springframework.kafka.KafkaException;

import java.util.Optional;

public class ExceptionHandler {

    public static String getReadableErrorMessage(Exception e) {
        return getThrowableList(e);
    }

    private static String getThrowableList(Exception e) {
        String s = e.getMessage();
        if (isContainsError(KafkaException.class, e)) {
            return "\t -------------------------------------------- Quick fix --------------------------------------------\n" +
                    "\t 1. telnet kafka server ,check the connection is successful. \n " +
                    "\t 2. confirm the kafka server is alive. ";
        } else if (ExceptionUtils.getRootCause(e).getMessage().contains(AppConfig.ERROR_INFO)) {
            return "\t -------------------------------------------- Quick fix --------------------------------------------\n" +
                    "\t 1. check that the entered table number is correct.";
        } else if (isContainsError(HibernateException.class, e)) {
            return "\t -------------------------------------------- Quick fix --------------------------------------------\n" +
                    "\t 1. telnet database server ,check the connection is successful. \n " +
                    "\t 2. check the database connection information is correct. \n" +
                    "\t 3. check ip in whitelist. \n " +
                    "\t 4. Ensure that the user has access to the database.";
        } else {
            return "";
        }

    }

    private static boolean isContainsError(Class clazz, Exception e) {
        Optional<Throwable> option = ExceptionUtils.getThrowableList(e)
                .stream()
                .filter(c -> c.getClass().equals(clazz))
                .findAny();
        return option.isPresent();
    }

}
