package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Example to demonstrate loading data from an external XML source.
 *
 * <p>Arguments, their parameters, description and dependencies may not be defined in the source code, but
 * loaded from an external XML resource: from an incoming stream or file. To do this, you need to use the
 * {@link ArgumentStorage#read(InputStream, String)} or {@link ArgumentStorage#read(File, String)} methods:
 * the read data will be added to the existing one.</p>
 *
 * @author Roman Rynkovich
 */
class XMLExample {
    @SuppressWarnings("IfCanBeSwitch")
    public static String main(String[] args){
        ArgumentStorage storage;
        try {
            //Load arguments data from external XML source
            storage = new ArgumentStorage(true);
            storage.read(storage.getClass().getClassLoader().getResourceAsStream("/XMLExample.xml"), null);
        } catch (Exception e) {
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }

        //Parse argument values
        ArgumentParser.parse(storage, args);
        if (args.length == 0){
            return storage.getHelp();
        }
        String help = storage.getSystemHelp(ArgumentStorage.HELP_SHORT_NAME);
        if (help != null){
            return help;
        }

        //Check arguments dependencies
        if (!storage.isRequireFilled() || storage.isConflict()){
            return "Wrong arguments data : " + storage.getMessage();
        }

        //Main program
        try {
            Locale locale = new Locale("en");
            String getValue = storage.getValue("g");
            String paramValue;
            SimpleDateFormat formatter;
            if (getValue.equals("date")){
                if (storage.isFilled("df")){
                    paramValue = storage.getValue("df");
                    if (paramValue.equals("long")){
                        formatter = new SimpleDateFormat("EEEE, d MMMM, yyyy", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else if (paramValue.equals("short")){
                        formatter = new SimpleDateFormat("yyyy.MM.dd", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else {
                        return "Error : date format <" + paramValue + "> is unknown";
                    }
                } else if (storage.isDeclared("tf")) {
                    return "Error : cant format date to time";
                } else {
                    formatter = new SimpleDateFormat("E, d MMM, yy", locale);
                    return formatter.format(new Date(System.currentTimeMillis()));
                }
            } else if (getValue.equals("time")){
                if (storage.isFilled("tf")){
                    paramValue = storage.getValue("tf");
                    if (paramValue.equals("24")){
                        formatter = new SimpleDateFormat("HH:mm", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else if (paramValue.equals("12")){
                        formatter = new SimpleDateFormat("h:m:s a", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else {
                        return "Error : time format <" + paramValue + "> is unknown";
                    }
                } else if (storage.isDeclared("df")){
                    return "Error : cant format time to date";
                } else {
                    formatter = new SimpleDateFormat("H:m:s.S z", locale);
                    return formatter.format(new Date(System.currentTimeMillis()));
                }
            } else {
                return "Error : get command <" + getValue + "> is unknown";
            }
        } catch (ArgumentNotFoundException e){
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }
    }
}
