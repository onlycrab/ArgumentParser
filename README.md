# About this project
ArgParser is one more java command-line argument parser library. There are many libraries that perform basically the same functions. For my small java projects, I wanted to write my own simple parser. Over time, this project has acquired some additional functionality. So I decided to add a demo and publish it. I will be glad if it is useful to someone!  
# Demo
The project has a demo for GUI machines. You can run the compiled library, there are several working (elementary) examples inside: look at the source code of the example, immediately enter commands into the console and get the result!
# Table of contents 
- [How to use it](#howToUseIt)
- [Simple example](#simpleExample)
- [Creating Argument Objects](#creatingArgumentObjects)
    - [Creation by hand](#creationByHand)
    - [Load from external source](#downloadFromExternalSource)
- [Using standard help](#usingStandardHelp)
- [Argument definition check](#argumentDefinitionCheck)
- [Argument declaration check](#argumentDeclarationCheck)
- [Argument fill check](#argumentFillCheck)
- [Default value](#defaultValue)
- [Value converter](#valueConverter)
- [Additional features](#additionalFeatures)
    - [Requirement to be declared](#requirementToBeDeclared)
    - [Requirement to be filled](#requirementToBeFilled)
    - [Dependence of one argument on another](#dependenceOfOneArgumentOnAnother)
    - [Conflict between arguments](#conflictBetweenArguments)
    - [Custom rules](#customRules)
- [In conclusion](#inConclusion)
<a name="howToUseIt"></a>
# How to use it
1.Create an ArgumentStorage object and fill it with data about possible arguments  
2.Set (if necessary) additional conditions on the arguments  
3.Parse command line arguments by ArgumentParser  
4.Get argument values ​​for further use 
<a name="simpleExample"></a>
# Simple example
```
public static void main(String[] args){
    ...
    ArgumentStorage storage = new ArgumentStorage();    //create the storage
    storage.add(new Argument("sa", "someArgument"));    //create argument and add it to storage
    ArgumentParser.parse(storage, args);                //parse command line arguments
    String myValue = storage.getValue("sa");            //get argument value
    ...
}
```
<a name="creatingArgumentObjects"></a>
# Creating Argument Objects
You can either create each argument manually, or load the arguments from an external source (in XML format).  
<a name="creationByHand"></a>
## 1. Creation by hand
Create an empty argument store object `ArgumentStorage storage = new ArgumentStorage()`. When creating an argument: you can only specify its names `argument arg = new Argument("a1", "argument1")` or also specify additional parameters `argument arg = new Argument("a1", "argument1", "defaultValue", isRequireBeDeclared, isRequiredBeFilled, "PARAMS", "DESCRIPTION", "DESCRIPTION_DETAILED")`  
where  
`defaultValue` - you can assign this value to the argument after parsing if it is not passed on the command line  
`isRequireBeDeclared` - a condition meaning that the argument must be declared  
`isRequiredBeFilled` - a condition meaning that along with the argument its value must be specified  
`"PARAMS", "DESCRIPTION", "DESCRIPTION_DETAILED"` - information fields for displaying help.  
Now add the generated arguments to the store `storage.add(arg1).add(arg2)...;`.
<a name="downloadFromExternalSource"></a>
## 2. Load from external source
After creating the store, you can load arguments from a file or stream  
```
...
ArgumentStorage storage = new ArgumentStorage();
storage.read("/path/to/file", "encoding");
...
```
You can see the full example of using [XMLExample.java](https://github.com/onlycrab/ArgumentParser/blob/master/src/main/java/com/github/onlycrab/argParser/demo/XMLExample.java).  
<a name="usingStandardHelp"></a>
# Using standard help
When creating the repository, use the second constructor to use the `h(help)` built-in argument  
```
...
ArgumentStorage storage = new ArgumentStorage(true);
ArgumentParser.parse(storage, args);
String helpValue = storage.getArgument("help").getValue();
...
```
If you don't want the standard `h(help)` argument in the store, you can still get standard help by calling the methods `arguments.getHelp()` or `arguments.getHelp("myArgName")`.
<a name="argumentDefinitionCheck"></a>
# Argument definition check
You can check if the argument is defined in the store: use `storage.isDefined("myArgName")` or `storage.getArgument("myArgName") != null`.
<a name="argumentDeclarationCheck"></a>
# Argument declaration check
An argument is considered declared if the parser finds it in the passed array of arguments. To check, use `storage.isDefined("myArgName")` method after parsing.
<a name="argumentFillCheck"></a>
# Argument fill check
An argument is considered filled if the parse finds its value in the passed array of arguments. To check, use `storage.isFilled("myArgName")` method after parsing.  
For example: in the case of `"-arg1 -arg2"` both arguments are declared but not filled; in case of `"-arg1 -val1 -arg2"` the first argument is declared and filled, the second is declared and not filled.
<a name="defaultValue"></a>
# Default value
It is possible to set an argument its default value when it is created, or use the `argument.setValueDefault()`. If the value of such arguments is not passed to the parser, then you can assign default values ​​by calling `storage.setArgumentValuesToDefaultIfEmpty()`. The default value will only be assigned to those arguments that have been declared.
<a name="valueConverter"></a>
# Value converter
The value of any argument is some string. You can convert it yourself in the way you need, or use the built-in converter.  
Example  
```
...
int val = storage.getValueConverter("arg1").toInt();
long[] arr = storage.getValueConverter("arg2").toArrayLong();
...
```
The converter has a special relationship with `boolean` - it can convert `strings` to `boolean` according to custom rules. To do this, call it like this  
`boolean val = storage.getValueConverter("arg1", new String[]{"true", "1", "yes"}, new String[]{"false", "0", "no"})`  
If the argument is `"true"`, `"1"` or `"yes"` val will be equal to `true`, and, accordingly, for `"false"`, `"0"` or `"no"` val will be equal to `false`.  
You can also see the example [ConverterExample.java](https://github.com/onlycrab/ArgumentParser/blob/master/src/main/java/com/github/onlycrab/argParser/demo/ConverterExample.java).  
<a name="additionalFeatures"></a>
# Additional features
<a name="requirementToBeDeclared"></a>
## 1.Requirement to be declared
You can ask the need argument to be declared: specify the `isRequireBeDeclared=true` parameter when creating it, or use the `setRequiredBeDeclared(true)` argument method. If such an argument turns out to be undeclared after parsing, then `storage.isRequireFilled()` will return false.    
<a name="requirementToBeFilled"></a>
## 2.Requirement to be filled
You can set whether the argument is required to be filled: specify the `isRequiredBeFilled=true` parameter when creating it, or use the `setRequiredBeFilled(true)` argument method. If such an argument turns out to be empty after parsing, then `storage.isRequireFilled()` will return false.  
<a name="dependenceOfOneArgumentOnAnother"></a>
## 3.Dependence of one argument on another
To set a dependency between arguments, use `storage.addDependence(nameDependent, nameOn)`. If `argument1` depends on `argument2`, then checking whether `argument1` is filled or declared will trigger a similar check on `argument2`.  
Example:  
Let `args[]` be an array read from the console command `"-arg1 val1"`. Then if you make `arg1` dependent on `arg2`, then
```
...
storage.addDependence("arg1", "arg2");
ArgumentParser.parse(storage, args);
boolean res = storage.isRequireFilled();
...
```
`res` will be `false`.  
If no additional conditions are imposed on `arg2`, then for the string `"-arg1 val1 -arg2"` the same code will return `res = true`.  
If `arg2.isRequiredBeFilled=true`, then the string `"-arg1 val1 -arg2"` will already have `res = false`; and for `"-arg1 val1 -arg2 val2"` will be `res = true`.  
If you have declared a cyclic dependency, then the `storage.isRequireFilled()` method will always return false.  
For example, for
```
...
storage.addDependence("arg1", "arg2");
storage.addDependence("arg2", "arg3");
storage.addDependence("arg3", "arg1");
boolean res = storage.isRequireFilled();
...
```
`res` will be `false` no matter which of these arguments are declared or filled.  
Usage example in code [DependenciesExample.java](https://github.com/onlycrab/ArgumentParser/blob/master/src/main/java/com/github/onlycrab/argParser/demo/DependenciesExample.java).
<a name="conflictBetweenArguments"></a>
## 4.Conflict between arguments
You can make arguments conflict with the `storage.addConflict("arg1", "arg2")` method. In this case, if these arguments are declared at the same time, the `storage.isConflict()` method will return `true`.  
Usage example in code [DependenciesExample.java](https://github.com/onlycrab/ArgumentParser/blob/master/src/main/java/com/github/onlycrab/argParser/demo/DependenciesExample.java).
<a name="customRules"></a>
## 5.Custom rules
You can create your own rules for arguments and check if the argument values ​​match them after parsing. To do this, extends your rule from the `Rule` class, bind arguments to it, and add the rules to the store.  
```
...
ArgumentStorage storage = new ArgumentStorage();
Argument arg;
...
Rule myRule = new Rule() {
    @Override
    public boolean isSatisfied() {
        ...
        //your awesome code
        ...
    }
}
myRule.addTarget(arg); //bind argument to rule
storage.addRule(myRyle); //add rule to storage
ArgumentParser.parse(storage, args);

boolean res = storage.isRulesSatisfied(); //check if rules are passed 
...
```
You can also see the finished example [CustomRulesExample.java](https://github.com/onlycrab/ArgumentParser/blob/master/src/main/java/com/github/onlycrab/argParser/demo/CustomRulesExample.java).
<a name="inConclusion"></a>
# In conclusion
If you have read down to this point, then the project has aroused some interest in you. Thank you for taking time to view it, and I hope that the project will be useful! 
