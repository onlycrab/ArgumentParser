package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Example to demonstrate the use of the arguments dependencies.
 *
 * @author Roman Rynkovich
 */
class DependenciesExample {
    private static final String ARG_GET = "g";
    private static final String PARAM_GET_TIME = "time";
    private static final String PARAM_GET_DATE = "date";

    private static final String ARG_DATE_FORMAT = "df";
    private static final String PARAM_DF_SHORT = "short";
    private static final String PARAM_DF_LONG = "long";

    private static final String ARG_TIME_FORMAT = "tf";
    private static final String PARAM_TF_12 = "12";
    private static final String PARAM_TF_24 = "24";

    @SuppressWarnings("IfCanBeSwitch")
    public static String main(String[] args){
        ArgumentStorage storage;
        try {
            //Create storage object
            storage = initArgs();

            //Add dependencies
            storage.addDependence(ARG_DATE_FORMAT, ARG_GET);
            storage.addDependence(ARG_TIME_FORMAT, ARG_GET);
            storage.addConflict(ARG_DATE_FORMAT, ARG_TIME_FORMAT);
        } catch (ArgumentException e) {
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }

        //Parse argument values
        ArgumentParser.parse(storage, args);

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
            String getValue = storage.getValue(ARG_GET);
            String paramValue;
            SimpleDateFormat formatter;
            if (getValue.equals(PARAM_GET_DATE)){
                if (storage.isFilled(ARG_DATE_FORMAT)){
                    paramValue = storage.getValue(ARG_DATE_FORMAT);
                    if (paramValue.equals(PARAM_DF_LONG)){
                        formatter = new SimpleDateFormat("EEEE, d MMMM, yyyy", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else if (paramValue.equals(PARAM_DF_SHORT)){
                        formatter = new SimpleDateFormat("yyyy.MM.dd", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else {
                        return "Error : date format <" + paramValue + "> is unknown";
                    }
                } else if (storage.isDeclared(ARG_TIME_FORMAT)) {
                    return "Error : cant format date to time";
                } else {
                    formatter = new SimpleDateFormat("E, d MMM, yy", locale);
                    return formatter.format(new Date(System.currentTimeMillis()));
                }
            } else if (getValue.equals(PARAM_GET_TIME)){
                if (storage.isFilled(ARG_TIME_FORMAT)){
                    paramValue = storage.getValue(ARG_TIME_FORMAT);
                    if (paramValue.equals(PARAM_TF_24)){
                        formatter = new SimpleDateFormat("HH:mm", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else if (paramValue.equals(PARAM_TF_12)){
                        formatter = new SimpleDateFormat("h:m:s a", locale);
                        return formatter.format(new Date(System.currentTimeMillis()));
                    } else {
                        return "Error : time format <" + paramValue + "> is unknown";
                    }
                } else if (storage.isDeclared(ARG_DATE_FORMAT)){
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

    private static ArgumentStorage initArgs() throws ArgumentException{
        ArgumentStorage storage = new ArgumentStorage(true);
        //Add arguments data
        storage.setDescription("Awesome utility to print current daytime.");
        storage.setExample("To get current time in 24h format type\n" +
                String.format("  -%s %s -%s %s\n", ARG_GET, PARAM_GET_TIME, ARG_TIME_FORMAT, PARAM_TF_24)  +
                "To get current date type\n  -" + ARG_GET + " " + PARAM_GET_DATE);
        storage.add(new Argument(
                //short name
                ARG_GET,
                //long name
                "get",
                //valueDefault
                null,
                //isRequiredBeDeclared
                true,
                //isRequiredBeFilled
                true,
                //parameters
                null,
                //description
                "Command to get daytime data. Can be <" + PARAM_GET_TIME + "> or <" + PARAM_GET_DATE + ">.",
                //descriptionDetailed
                "Command to get daytime data. Type <" + PARAM_GET_TIME + "> to get current time, or <" +
                        PARAM_GET_DATE + "> to get current date."
        ));
        storage.add(new Argument(
                ARG_DATE_FORMAT,
                "dformat",
                null,
                false,
                true,
                "STRING",
                "Date format. Can be <" + PARAM_DF_LONG + "> or <" + PARAM_DF_SHORT + ">.",
                "Date format. Type <" + PARAM_DF_SHORT + "> for YYYY.MM.DD format, or <" +
                        PARAM_DF_LONG + "> for full format."
        ));
        storage.add(new Argument(
                ARG_TIME_FORMAT,
                "tformat",
                null,
                false,
                true,
                "STRING",
                "Time format. Can be <" + PARAM_TF_12 + "> or <" + PARAM_TF_24 + ">.",
                "Time format. Type <" + PARAM_TF_12 + "> to get current time in 12h format, or <" +
                        PARAM_TF_24 + "> - in 24h format."
        ));
        return storage;
    }
}
