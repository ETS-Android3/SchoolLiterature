package fitisov123.schoolliterature;

public class FieldChecker {

    public static String userDataWrongToastString = "Разрешены буквы русского и латинского алфавита, цифры и символы: '@', '-', '_', '.'";
    public static String noteTextWrongToastString = "Разрешены только буквы русского и латинского алфавита, цифры, пробелы и знаки препинания";

    private static boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'
                || c >= 'а' && c <= 'я' || c >= 'А' && c <= 'Я' || c == 'ё' || c == 'Ё');
    }

    private static boolean isDigit(char c){
        return (c >= '0' && c <= '9');
    }

    private static boolean isHelpSimbol(char c, boolean userData){
        if(userData)
            return (c == '_' || c == '-' || c == '.' || c == '@');
        else
            return (c == '_' || c == '-' || c == ' ' || c == ',' || c == '.' || c == ':' || c == ';' ||
                    c == '\''|| c == '\"' || c == '!' || c == '?' || c == '(' || c == ')' || c == '@');
    }

    public static boolean correctField(String input, boolean userData){
        for(int i = 0; i < input.length(); i++){
            char cur = input.charAt(i);
            if(!(isAlpha(cur) || isDigit(cur) || isHelpSimbol(cur, userData)))
                return false;
        }
        return true;
    }
}
