enum Type
{
    NUMBER, STRING, BOOLEAN, NULL, JSONOBJECT, JSONLIST
}

class Node
{
    Type type;
    Object val;

    Node(Type t, Object obj)
    {
        this.type = t;
        this.val = obj;
    }

    Object get()
    {
        return val;
    }

    void set(Object x)
    {
        val = x; // figure out how to clone
    }

    public String toString()
    {
        StringBuilder str = new StringBuilder();
        if(type == Type.STRING)
            str.append("\"");
        str.append(val);
        if(type == Type.STRING)
            str.append("\"");

        return str.toString();
    }
}