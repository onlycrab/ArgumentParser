package com.github.onlycrab.argParser.demo;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentParser;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ConverterException;

/**
 * Example to demonstrate the use of the argument value converter.
 *
 * @author Roman Rynkovich
 */
class ConverterExample {
    private static final String TYPE_BOOLEAN = "bool";
    private static final String TYPE_INTEGER = "int";
    private static final String TYPE_ARRAY = "arr";

    public static String main(String[] args){
        //Create storage object
        ArgumentStorage storage = new ArgumentStorage();
        Argument argValue, argType;
        try {
            //Add arguments data
            argValue = new Argument("v", "value");
            argType = new Argument("t", "type");
            storage.add(argValue).add(argType);
        } catch (ArgumentException e) {
            return String.format(DemoJFrame.SORRY_TEXT, e.getMessage());
        }

        //Parse argument values
        ArgumentParser.parse(storage, args);

        //Main program
        StringBuilder sb = new StringBuilder();
        if (!argType.isFilled()){
            sb.append("Conversion type is not set\n");
            if (argValue.isFilled()){
                sb.append("Value : <").append(argValue.getValue()).append(">\n");
            } else {
                sb.append("Value is not set\n");
            }
        } else {
            if (!argValue.isFilled()){
                sb.append("Value is not set\n");
            } else {
                switch (argType.getValue()){
                    case TYPE_BOOLEAN:
                        try {
                            boolean boolVal = argValue.getValueConverter(
                                    new String[]{"true", "yes", "on", "active"},
                                    new String[]{"false", "no", "off", "inactive"}).toBoolean();
                            sb.append("Value <").append(argValue.getValue()).append("> converted to BOOLEAN as <")
                                    .append(boolVal).append(">");
                        } catch (ConverterException e) {
                            sb.append(e.getMessage());
                        }
                        break;
                    case TYPE_INTEGER:
                        try {
                            int intVal = argValue.getValueConverter().toInt() + 1;
                            sb.append("Value <").append(argValue.getValue())
                                    .append("> converted to INTEGER, <value> + 1 = <")
                                    .append(String.valueOf(intVal)).append(">");
                        } catch (ConverterException e) {
                            sb.append(e.getMessage());
                        }
                        break;
                    case TYPE_ARRAY:
                        try {
                            String[] arr = argValue.getValueConverter().toArray();
                            sb.append("Value <").append(argValue.getValue()).append("> converted to ARRAY : \n")
                                    .append(printArr(arr));
                        } catch (ConverterException e) {
                            sb.append(e.getMessage());
                        }
                        break;
                    default:
                        sb.append("Type <").append(argType.getValue()).append("> is unknown");
                }
            }
        }

        return sb.toString();
    }

    private static String printArr(String[] arr){
        if (arr == null) return "";
        if (arr.length == 0) return "{}";
        StringBuilder sb = new StringBuilder();
        sb.append("{[0]=<").append(arr[0]).append(">");
        for (int i = 1; i < arr.length; i++){
            sb.append("\n[").append(i).append("]=<").append(arr[0]).append(">");
        }
        return sb.append("}\n").toString();
    }
}
