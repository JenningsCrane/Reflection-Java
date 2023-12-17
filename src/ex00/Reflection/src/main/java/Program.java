import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Program {
    public static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        try {
            Set<Class<?>> classes = getClasses();
            Class<?> clazz = enterClassName(classes);
            showClassFields(clazz);
            showClassMethods(clazz);
            Object obj = createObject(clazz);
            fieldChanging(obj);
            callMethod(obj);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static Set<Class<?>> getClasses() {
        System.out.println("Classes:  ");
        Reflections reflections = new Reflections("Classes", new SubTypesScanner(false));
        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);
        for(Class<?> clazz : classes) {
            System.out.println("    - " + clazz.getSimpleName());
        }
        System.out.println("---------------------");
        return classes;
    }

    private static Class<?> enterClassName(Set<Class<?>> classes) {
        System.out.println("Enter class name: ");
        System.out.print("-> ");
        String clazz = scanner.nextLine();
        for (Class<?> tmpClass : classes) {
            if (tmpClass.getSimpleName().equals(clazz)) {
                return tmpClass;
            }
        }
        return null;
    }

    private static void showClassFields(Class<?> clazz) {
        System.out.println("---------------------");
        Field[] fields = clazz.getDeclaredFields();
        System.out.println("fields: ");
        for (Field field : fields) {
            System.out.println("        " + field.getType().getSimpleName() + " " + field.getName());
        }
    }

    private static void showClassMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        System.out.println("methods: ");
        for (Method method : methods) {
            System.out.println("        " +
                    method.getReturnType().getSimpleName() + " " +
                    method.getName() + getParameters(method));
        }
        System.out.println("---------------------");
    }

    private static String getParameters(Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        Parameter[] parameters = method.getParameters();
        stringBuilder.append("(");
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i].getType().getSimpleName());
            if (i < parameters.length - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    private static Object createObject(Class<?> clazz) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        System.out.println("Let's create an object.");
        Object obj = Class.forName(clazz.getName()).newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field.getName() + ":");
            System.out.print("-> ");
            field.setAccessible(true);
            setValue(field, obj);
            field.setAccessible(false);
        }
        System.out.println("Object created: " + obj);
        System.out.println("---------------------");
        return obj;
    }

    private static void setValue(Field field, Object obj) throws IllegalAccessException {
        switch (field.getType().getSimpleName()) {
            case "Integer", "int" -> {
                Integer variable = scanner.nextInt();
                scanner.nextLine();
                field.set(obj, variable);
            }
            case "String" -> {
                String variable = scanner.nextLine();
                field.set(obj, variable);
            }
            case "Double", "double" -> {
                Double variable = scanner.nextDouble();
                scanner.nextLine();
                field.set(obj, variable);
            }
            case "Long", "long" -> {
                Long variable = scanner.nextLong();
                scanner.nextLine();
                field.set(obj, variable);
            }
            case "Float", "float" -> {
                Float variable = scanner.nextFloat();
                scanner.nextLine();
                field.set(obj, variable);
            }
            case "Boolean", "boolean" -> {
                Boolean variable = scanner.nextBoolean();
                scanner.nextLine();
                field.set(obj, variable);
            }
        }
    }

    private static void fieldChanging(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        System.out.println("Enter name of the field for changing:");
        System.out.print("-> ");
        String str = scanner.nextLine();
        for (Field field: fields) {
            if (field.getName().equals(str)) System.out.println("Enter " + field.getType().getSimpleName() + " value:");
            System.out.print("-> ");
            field.setAccessible(true);
            setValue(field, obj);
            field.setAccessible(false);
            break;
        }
        System.out.println("Object updated: " + obj);
        System.out.println("---------------------");
    }

    private static void callMethod(Object obj) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        System.out.println("Enter name of the method for call: ");
        System.out.print("-> ");
        String str = scanner.nextLine();
        for (Method method : methods) {
            if (str.equals(method.getName() + getParameters(method))) {
                invokeMethod(method, obj);
            }
        }
    }
    private static void invokeMethod(Method method, Object obj) throws InvocationTargetException, IllegalAccessException {
        ArrayList<Object> arguments = new ArrayList<>();
        for (Class<?> param : method.getParameterTypes()) {
            String value = param.getSimpleName();
            switch (value) {
                case "Integer", "int" -> {
                    printValue(value);
                    int variable = scanner.nextInt();
                    arguments.add(variable);
                }
                case "String" -> {
                    printValue(value);
                    String variable = scanner.nextLine();
                    arguments.add(variable);
                }
                case "Double", "double" -> {
                    printValue(value);
                    double variable = scanner.nextDouble();
                    arguments.add(variable);
                }
                case "Boolean", "boolean" -> {
                    printValue(value);
                    boolean variable = scanner.nextBoolean();
                    arguments.add(variable);
                }
                case "Float", "float" -> {
                    printValue(value);
                    float variable = scanner.nextFloat();
                    arguments.add(variable);
                }
                case "Long", "long" -> {
                    printValue(value);
                    long variable = scanner.nextLong();
                    arguments.add(variable);
                }
            }
        }

        if(method.getReturnType().getSimpleName().equals("void")) {
            method.invoke(obj, arguments.toArray());
        } else {
            System.out.println("Method returned:");
            System.out.println(method.invoke(obj, arguments.toArray()));
        }
    }

    private static void printValue(String value) {
        System.out.println("Enter " + value + " value:");
        System.out.print("-> ");
    }
}
