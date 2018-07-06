package it.polimi.se2018.view.cli;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomPrintStream extends PrintStream {

    private boolean useUnicode = false;

    private static final Map<Character, Character> charMap;
    static {
        Map<Character, Character> tmp = new HashMap<>();
        tmp.put('═', '-');
        tmp.put('║', '|');
        tmp.put('╔', '+');
        tmp.put('╗', '+');
        tmp.put('╚', '+');
        tmp.put('╝', '+');
        tmp.put('╤', '+');
        tmp.put('╧', '+');
        tmp.put('─', '-');
        tmp.put('│', '|');
        tmp.put('╭', '+');
        tmp.put('╮', '+');
        tmp.put('╰', '+');
        tmp.put('╯', '+');
        tmp.put('┌', '+');
        tmp.put('─', '-');
        tmp.put('┬', '+');
        tmp.put('┐', '+');
        tmp.put('├', '+');
        tmp.put('┼', '+');
        tmp.put('┤', '+');
        tmp.put('└', '+');
        tmp.put('┴', '+');
        tmp.put('┘', '+');
        tmp.put('┏', '/');
        tmp.put('━', '-');
        tmp.put('┓', '\\');
        tmp.put('┃', '|');
        tmp.put('┠', '+');
        tmp.put('┗', '\\');
        tmp.put('┛', '/');
        tmp.put('━', '-');

        charMap = Collections.unmodifiableMap(tmp);
    }

    CustomPrintStream(OutputStream out, boolean useUnicode) {
        super(out);
        this.useUnicode = useUnicode;
    }

    @Override
    public void println(String x) {
        if(useUnicode){
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < x.length(); i++){
                if(charMap.containsKey(x.charAt(i))){
                    builder.append(charMap.get(x.charAt(i)));
                } else {
                    builder.append(x.charAt(i));
                }
            }
            super.println(builder.toString());
        } else {
            super.println(x);
        }
    }
}
