package com.wasa.api.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.util.*;

public class Util {

    public static String removeSpecialChar(String st) {
        if (st != null) {
            st = st.replace('\'', '`');
        } else {
            st = "";
        }
        return st;
    }

    public static int numOfdays(Date d1, Date d2) {
        return (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int numOfWeeks(String month, String year) {
        Calendar weeks = new GregorianCalendar();
        weeks.set(Calendar.DAY_OF_MONTH, 1);
        weeks.set(Calendar.MONTH, Integer.parseInt(month) - 1); // month start from zero index ex: jan=0, feb=1....
        weeks.set(Calendar.YEAR, Integer.parseInt(year));
        return (int) (weeks.getActualMaximum(Calendar.WEEK_OF_MONTH)); // return total number of weeks
    }

    public static List<String> getAllDatesByMonth(String month, String datePattern) {
        List<String> objList = new ArrayList<String>();
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = dateFormat.parse("01-" + month.toUpperCase());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            dateFormat = new SimpleDateFormat(datePattern);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 0; i < daysInMonth; i++) {
                objList.add(dateFormat.format(cal.getTime()));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return objList;
    }

    // month: Feb-2012 => start date:01-02-2012 & end date:29-02-2012
    public static String[] getStartEndDateByGiventMonth(String month) {
        String[] dt = new String[2];
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = dateFormat.parse("01-" + month.toUpperCase());
            Calendar cal = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            cal.setTime(date);
            dt[0] = dateFormat.format((Date) cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            dt[1] = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dt;
    }

    // month: Feb-2012 => end date:29-02-2012
    public static String getEndDateByGivenMonth(String month) {
        String dt = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            Date date = dateFormat.parse("01-" + month.toUpperCase());
            Calendar cal = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            cal.setTime(date);
            cal.add(Calendar.MONTH, -1);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            dt = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dt;
    }

    // date: 01-Mar-2012 => end date:29-02-2012
    public static String getPreviousDateByGivenDate(String date, String datePattern) {
        String previousDate = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(datePattern);
            Date dt = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.DATE, -1);
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            previousDate = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return previousDate;
    }

    // given month: 11 => 10
    public static String getPreviousMonthByGivenMonth(String month, String datePattern) {
        String previousMonth = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(datePattern);
            Date dt = dateFormat.parse("01-" + month.toUpperCase());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.MONTH, -1);
            dateFormat = new SimpleDateFormat("MMM-yyyy");
            previousMonth = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return previousMonth;
    }

    // given month: 11 => 12
    public static String getNextMonthByGivenMonth(String month, String datePattern) {
        String previousMonth = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(datePattern);
            Date dt = dateFormat.parse("01-" + month.toUpperCase());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.MONTH, 1);
            dateFormat = new SimpleDateFormat("MMM-yyyy");
            previousMonth = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return previousMonth;
    }

    // get Due Date 
    public static String getDueDate(String date, String datePattern, String CrDays) {
        String previousDate = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(datePattern);
            Date dt = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.DATE, Integer.parseInt(CrDays));
            dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            previousDate = dateFormat.format((Date) cal.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return previousDate;
    }

    public static String assignCommaToString(String input) {
        String output = "";
        String[] strArray = input.split(",");
        for (int i = 0; i < strArray.length; i++) {
            if (i != strArray.length - 1) {
                output += "" + strArray[i].trim() + ", ";
            } else {
                output += "" + strArray[i].trim() + "";
            }
        }
        return output;
    }

    public static String assignCommaToString(String[] input) {
        String output = "";
        for (int i = 0; i < input.length; i++) {
            if (i != input.length - 1) {
                output += "" + input[i].trim() + ", ";
            } else {
                output += "" + input[i].trim() + "";
            }
        }
        return output;
    }

    public static String assignSingleQouteToString(String input) {
        String output = "";
        String[] strArray = input.split(",");
        for (int i = 0; i < strArray.length; i++) {
            if (i != strArray.length - 1) {
                output += "'" + strArray[i].trim() + "', ";
            } else {
                output += "'" + strArray[i].trim() + "'";
            }
        }
        return output;
    }

    public static List<String> convertArrayToList(String[] input) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < input.length; i++) {
            list.add(input[i]);
        }
        return list;
    }

    public static String convertArrayToString(String[] input) {
        String output = "";
        if (input != null) {
            for (int i = 0; i < input.length; i++) {
                if (i != input.length - 1) {
                    output += "" + input[i].trim() + ", ";
                } else {
                    output += "" + input[i].trim() + "";
                }
            }
        }
        return output;
    }

    public static String[] convertNumStringToArray(String input, String delimiter) {
        String delimt = ",";
        if(!delimiter.isEmpty()){
            delimt = delimiter;
        }
        StringTokenizer tokenizer = new StringTokenizer(input, delimt);
        int tokenCount = tokenizer.countTokens();
        String[] stringArray = new String[tokenCount];                
        // Converting each token to array elements
        for (int i = 0; i < tokenCount; i++) {
            stringArray[i] = tokenizer.nextToken();
        }        
        return stringArray;
    }

    //Fin Year End Date
    public static String getFinancialYearName(String date, String datePattern) {
        String yearName = "";
        SimpleDateFormat dateFormat = null;
        try {
            dateFormat = new SimpleDateFormat(datePattern);
            Date dt = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            cal.add(Calendar.MONTH, +12);
            cal.add(Calendar.DATE, -1);
            Calendar cal2 = Calendar.getInstance();
            Date dt2 = dateFormat.parse(date);
            cal2.setTime(dt2);
            if (cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
                dateFormat = new SimpleDateFormat("yyyy");
                yearName = dateFormat.format((Date) cal.getTime());
            } else {
                dateFormat = new SimpleDateFormat("yyyy");
                yearName = dateFormat.format((Date) cal2.getTime()) + "-" + dateFormat.format((Date) cal.getTime());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return yearName;
    }

    //From Date & To Date
    public static List<Map> getAllMonthsBetweenTwoDates(String fromDate, String toDate, String datePattern, String monthPattern) {
        List monthList = new ArrayList();
        Map<String, String> mapMonth = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            SimpleDateFormat monthFormat = new SimpleDateFormat(monthPattern);

            Calendar beginCalendar = Calendar.getInstance();
            Calendar finishCalendar = Calendar.getInstance();

            beginCalendar.setTime(dateFormat.parse(fromDate));
            finishCalendar.setTime(dateFormat.parse(toDate));

            while (beginCalendar.before(finishCalendar)) {
                // add one month to date per loop
                String monthName = monthFormat.format(beginCalendar.getTime());
                mapMonth = new HashMap<>();
                mapMonth.put("monthName", monthName);
                mapMonth.put("monthNbr", String.valueOf(beginCalendar.get(Calendar.MONTH) + 1));
                monthList.add(mapMonth);
                beginCalendar.add(Calendar.MONTH, 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return monthList;
    }

    public static String generateVerificationCode() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString().toUpperCase();
        return saltStr;
    }

    public static String convertToInitCap(String str) {
        // Create a char array of given String 
        char ch[] = str.toCharArray();
        for (int i = 0; i < str.length(); i++) {

            // If first character of a word is found 
            if (i == 0 && ch[i] != ' '
                    || ch[i] != ' ' && ch[i - 1] == ' ') {

                // If it is in lower-case 
                if (ch[i] >= 'a' && ch[i] <= 'z') {

                    // Convert into Upper-case 
                    ch[i] = (char) (ch[i] - 'a' + 'A');
                }
            } // If apart from first character 
            // Any one is in Upper-case 
            else if (ch[i] >= 'A' && ch[i] <= 'Z') // Convert into Lower-Case 
            {
                ch[i] = (char) (ch[i] + 'a' - 'A');
            }
        }

        // Convert the char array to equivalent String 
        String st = new String(ch);
        return st;
    }

    public static String renameFileName(String input) {
        String output = input != null ? input.trim().replace(" ", "_") : "";
        if (output.contains("%")) {
            output = output.replace("%", "_");
        }
        if (output.contains("?")) {
            output = output.replace("?", "_");
        }
        if (output.contains("#")) {
            output = output.replace("#", "_");
        }
        if (output.contains("&")) {
            output = output.replace("&", "_");
        }
        return output;
    }

    public static String generateJsonList(List<java.util.Map> list) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        ArrayNode array = mapper.createArrayNode();
        ObjectNode obj = null;
        for (java.util.Map map : list) {
            obj = mapper.createObjectNode();
            Iterator<java.util.Map.Entry<String, Object>> itr = map.entrySet().iterator();
            while (itr.hasNext()) {
                String key = itr.next().getKey();
                if (map.get(key) != null) {
                    if (map.get(key) instanceof Integer) {
                        obj.put(key, (Integer) map.get(key));
                    } else if (map.get(key) instanceof BigDecimal) {
                        obj.put(key, (BigDecimal) map.get(key));
                    } else {
                        obj.put(key, map.get(key).toString());
                    }
                } else {
                    String nullVal = null;
                    obj.put(key, nullVal);
                }
            }
            array.add(obj);
        }
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(array);
    }
}

