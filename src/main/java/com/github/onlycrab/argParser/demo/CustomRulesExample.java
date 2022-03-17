package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.dependencies.Rule;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;

import java.util.regex.Pattern;

/**
 * Example to demonstrate the use of a custom rules.
 *
 * @author Roman Rynkovich
 */
class CustomRulesExample {
    static String main(String[] args){
        //Create rule
        Rule ruleEmail = new EmailRule();
        //Create storage object
        ArgumentStorage storage = new ArgumentStorage(true);
        Argument argEmail;
        try {
            //Add arguments data
            argEmail = new Argument(
                    //short name
                    "e",
                    //long name
                    "email",
                    //valueDefault
                    null,
                    //isRequiredBeDeclared
                    true,
                    //isRequiredBeFilled
                    true,
                    //parameters
                    null,
                    //description
                    "Check if value is an email.",
                    //descriptionDetailed
                    null
            );

            argEmail.setRequiredBeDeclared(true);
            argEmail.setRequiredBeFilled(true);
            storage.add(argEmail);

            //Add argument to rule
            ruleEmail.addTarget(argEmail);

            //Add rule to storage object
            storage.addRule(ruleEmail);
        } catch (ArgumentException e) {
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }

        //Parse argument values
        ArgumentParser.parse(storage, args);

        String help = storage.getSystemHelp(ArgumentStorage.HELP_SHORT_NAME);
        if (help != null){
            return help;
        }

        //Main program
        if (!storage.isRequireFilled()){
            return "Wrong arguments data : " + storage.getMessage();
        }
        //Check rules
        if (storage.isRulesSatisfied()){
            return String.format("Value <%s> is a email.", argEmail.getValue());
        } else {
            return storage.getMessage();
        }
    }

    /**
     * Custom rule to check if argument value is email.
     */
    private static class EmailRule extends Rule{
        /**Email pattern */
        private final Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

        @Override
        public boolean isSatisfied() {
            clearMessages();
            boolean result = true;
            for (Argument target : targets){
                if (target == null){
                    continue;
                } else if (!target.isFilled()){
                    continue;
                }
                if (!pattern.matcher(target.getValue()).matches()){
                    result = false;
                    addMessage(String.format("Value <%s> for argument %s is not a email.",
                            target.getValue(), target.getName()));
                }
            }
            return result;
        }
    }
}
