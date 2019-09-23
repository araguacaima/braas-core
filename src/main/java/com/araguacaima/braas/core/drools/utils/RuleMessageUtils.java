package com.araguacaima.braas.core.drools.utils;

import com.araguacaima.braas.core.IMessage;
import com.araguacaima.braas.core.RuleMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.Collection;
import java.util.Locale;

public class RuleMessageUtils {

    private static Predicate predicateMessage = object -> RuleMessage.class.isAssignableFrom(object.getClass());

    public static Collection<?> getMessages(Collection result) {
        return getMessages(result, Locale.ENGLISH);
    }

    public static Collection<?> getMessages(Collection result, Locale locale) {
        Collection collection = CollectionUtils.select(result, predicateMessage);
        CollectionUtils.filter(collection, input -> {
            IMessage message = (IMessage) input;
            String language = message.getLanguage();
            String localeLanguage = locale.getLanguage();
            return localeLanguage.equals(language);
        });
        return collection;
    }
}
