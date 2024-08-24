package com.chriscarini.jetbrains.gitpushreminder.messages;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public final class MyStrings
{
@NonNls private static final String STRINGS = "messages.strings";

@NonNls private static final ResourceBundle resources = ResourceBundle.getBundle(STRINGS);

public static String get(@PropertyKey(resourceBundle=STRINGS) String key, Object... params)
{
   String value = resources.getString(key);
   
   if(params.length > 0) return MessageFormat.format(value, params);
   else return value;
}
}
