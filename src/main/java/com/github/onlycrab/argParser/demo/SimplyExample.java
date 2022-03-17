package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;

/**
 * Simple example to demonstrate the use of arguments.
 *
 * @author Roman Rynkovich
 */
class SimplyExample {
    public static String main(String[] args){
        //Create storage object
        ArgumentStorage storage = new ArgumentStorage();
        try {
            //Add arguments data to storage
            storage.add(new Argument("a", "alpha"));
            storage.add(new Argument("b", "beta"));

            //Parse argument values into created storage with arguments data
            ArgumentParser.parse(storage, args);

            return getResult(storage, "a") + getResult(storage, "b");
        } catch (ArgumentException | ArgumentNotFoundException e) {
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }
    }

    private static String getResult(ArgumentStorage args, String shortName) throws ArgumentNotFoundException {
        StringBuilder sb = new StringBuilder();
        if (args.isDeclared(shortName)) {
            if (args.isFilled(shortName)) {
                sb.append(String.format("Argument <%s(%5s)> : declared, filled, value = <%s>\n",
                        shortName, args.getArgument(shortName).getLongName(), args.getValue(shortName)));
            } else {
                sb.append(String.format("Argument <%s(%5s)> : declared, but not filled\n",
                        shortName, args.getArgument(shortName).getLongName()));
            }
        } else {
            sb.append(String.format("Argument <%s(%5s)> : not declared, not filled\n",
                    shortName, args.getArgument(shortName).getLongName()));
        }
        return sb.toString();
    }
}
