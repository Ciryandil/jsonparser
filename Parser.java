import java.util.*;

/*
Type.NUMBER == Number
Type.STRING == string
Type.BOOLEAN == boolean
Type.JSONOBJECT == JSONObject
Type.NULL == null
Type.JSONLIST == JSONList
*/

public class Parser
{
    public Parser()
    {

    }

    public static boolean matchBrackets(String data)
    {
        Stack<Character> brack = new Stack<>();
        int match = 0;
        for(int i = 0; i < data.length(); i++)
        {
            if(data.charAt(i) == '\"')
            {
                match = (match+1) % 2;
            }
            else if(match == 0)
            {
                if(data.charAt(i) == '{')
                    brack.push('{');
                else if(data.charAt(i) == '[')
                    brack.push('{');
                else if(data.charAt(i) == '}') {
                    if(brack.peek() == '{')
                        brack.pop();
                    else
                        return false;
                }
                else if(data.charAt(i) == ']') {
                    if(brack.peek() == '[')
                        brack.pop();
                    else
                        return false;
                }
            }

        }
        return brack.empty();
    }

    public static String removeSpaces(String data)
    {
        int match = 0;
        StringBuilder minData = new StringBuilder();
        for(int i = 0; i < data.length(); i++)
        {
            if(data.charAt(i) == '\"')
            {
                match = (match + 1) % 2;
                minData.append(data.charAt(i));
            }
            else if(match == 0 && data.charAt(i) != ' ')
                minData.append(data.charAt(i));
            else if(match == 1)
                minData.append(data.charAt(i));

        }

        return minData.toString();
    }

    public static Type getType(String value)
    {
        if(value.length() > 0 && value.charAt(0) == '{' && value.charAt(value.length()-1) == '}')
            return Type.JSONOBJECT;
        if(value.length() > 0 && value.charAt(0) == '[' && value.charAt(value.length()-1) == ']')
            return Type.JSONLIST;
        if(value.equals("true") || value.equals("false"))
            return Type.BOOLEAN;
        if(value.equals("null"))
            return Type.NULL;
        if(value.length() > 0 && value.matches("-?[0-9]*.?[0-9]+"))
            return Type.NUMBER;

        return Type.STRING;

    }

    public static Node getVal(Type t, String value)
    {

        if(t == Type.NUMBER)
        {
            if(value.indexOf('.') == -1)
                return new Node(t, Long.parseLong(value));
            else
                return new Node(t, Double.parseDouble(value));
        }
        if(t == Type.BOOLEAN)
        {
            if(value.equals("true"))
                return new Node(t, true);
            else
                return new Node(t, false);
        }
        if(t == Type.NULL)
            return new Node(t, null);
        if(t == Type.JSONLIST)
            return new Node(t, parseList(value));
        if(t == Type.JSONOBJECT)
            return new Node(t, parseObject(value));

        return new Node(t, value); // everything else is a String
    }

    public static JSONList parseList(String data)
    {
        data = data.trim();
        if(data.charAt(0) != '[' || data.charAt(data.length()-1) != ']')
            throw new IllegalArgumentException("Invalid format");

        String minData = removeSpaces(data);
        if(!matchBrackets(minData))
            throw new IllegalArgumentException("Invalid format");
        /*
         * STATES
         * 0 : INSIDE
         * 1 : VALUE
         * 2 : INSIDE JSONOBJECT OR JSONLIST
         */
        JSONList obj = new JSONList();
        Stack<Integer> inds = new Stack<>();
        int state = 0, match = 0;
        minData = minData.substring(0,minData.length()-1)+"," + minData.charAt(minData.length()-1);
        StringBuilder value = new StringBuilder();
        for(int i = 1; i < minData.length()-1; i++)
        {
            if(minData.charAt(i) == '\"' && state != 2)
            {
                match = (match+1) % 2;
                if(state == 0)
                    state = 1;
                else if(match == 0)
                    state = 0;


            }
            else if(minData.charAt(i) == ',' && state != 2)
            {

                String valstring = value.toString();

                value.setLength(0);
                if(minData.charAt(i-1) == '\"')
                {
                    obj.add(getVal(Type.STRING, valstring));
                }
                else {
                    Type type = getType(valstring);
                    obj.add(getVal(type, valstring));
                }
                state = 1;
            }

            else if(match == 0 && (minData.charAt(i) == '{' || minData.charAt(i) == '['))
            {
                state = 2;
                inds.push(i);
            }

            else if(state == 2 && (minData.charAt(i) == '}' || minData.charAt(i) == ']')) {
                int index = inds.pop();
                if (inds.empty()) {
                    String valstring = minData.substring(index, i + 1);
                    value.setLength(0);

                    //System.out.println("2: " + valstring);

                    Type type = getType(valstring);
                    obj.add(getVal(type, valstring));
                    if (minData.charAt(i + 1) != ',')
                        throw new IllegalArgumentException("Invalid format");

                    i++;
                    state = 0;
                }
            }


            else if(state == 1)
                value.append(minData.charAt(i));



        }



        return obj;
    }

    public static JSONObject parseObject(String data)
    {
        data = data.trim();
        if(data.charAt(0) != '{' || data.charAt(data.length()-1) != '}')
            throw new IllegalArgumentException("Invalid format");
        
        String minData = removeSpaces(data);
        /*
        * STATES
        * 0 : PUNCTUATION
        * 1 : KEY
        * 2 : VALUE
        * 3 : INSIDE ANOTHER JSON
        */
        JSONObject obj = new JSONObject();
        Stack<Integer> inds = new Stack<>();
        minData = minData.substring(0,minData.length()-1) + "," + minData.charAt(minData.length()-1);
       // System.out.println(minData);
        int state = 0, match = 0;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        for(int i = 1; i < minData.length()-1; i++)
        {

            if(minData.charAt(i) == '\"' && state != 3)
            {
               match = (match+1) % 2;
                if(state == 0)
                    state = 1;
                else if(match == 0)
                    state = 0;

            }

            else if(match == 0 && (minData.charAt(i) == '{' || minData.charAt(i) == '['))
            {
                state = 3;
                inds.push(i);
            }

            else if(state == 3 && (minData.charAt(i) == '}' || minData.charAt(i) == ']'))
            {
                int index = inds.pop();
                if(inds.empty())
                {
                    String valstring = minData.substring(index,i+1);
                    String keystring = key.toString();
                    key.setLength(0);
                    value.setLength(0);

                    System.out.println("3: " + keystring + " : " + valstring);

                    Type type = getType(valstring);
                    obj.insert(keystring, getVal(type, valstring));
                    if(minData.charAt(i+1) != ',')
                        throw new IllegalArgumentException("Invalid format");

                    i++;
                    state = 0;
                }
            }

            else if(minData.charAt(i) == ',' && state != 3)
            {
                String keystring = key.toString();
                String valstring = value.toString();
                key.setLength(0);
                value.setLength(0);

               System.out.println(keystring + " : " + valstring);

               if(minData.charAt(i-1) == '\"')
               {
                   obj.insert(keystring, valstring);
               }
               else {
                   Type type = getType(valstring);
                   obj.insert(keystring, getVal(type, valstring));
               }
                state = 0;
            }
            else if(minData.charAt(i) == ':' && state != 3)
            {
                state  = 2;
            }


            else if(state == 1)
                key.append(minData.charAt(i));
            else if(state == 2)
                value.append(minData.charAt(i));



//            System.out.println("Index: " + i + " State: " + state);
            
        }

        return obj;
    }

    public static void main(String[] args)
    {
        // JSON taken from cryptonator.com/api
        String jsonstring = "{\"ticker\":{\"base\":\"BTC\",\"target\":\"USD\",\"price\":\"57463.41360553\",\"volume\":\"53728.82532639\",\"change\":\"379.68558263\"},\"timestamp\":1634226242,\"success\":true,\"error\":\"\"}";

        JSONObject obj = Parser.parseObject(jsonstring);

        System.out.println(obj.toString());
        System.out.println(jsonstring);
        System.out.println(obj.toString().equals(jsonstring));
    }
}

