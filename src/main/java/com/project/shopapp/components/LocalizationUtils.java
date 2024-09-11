package com.project.shopapp.components;

import com.project.shopapp.utils.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
@Component
public class LocalizationUtils {

    @Autowired
    MessageSource messageSource;
    @Autowired
    LocaleResolver localeResolver;

    public String getLocalizedMessage(String messageKey){

        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale =localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,null,locale);
    }



}
