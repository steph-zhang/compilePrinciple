package LexerPackage;

import main.SemanticException;

public class Lexer {
    public int pointer = 0;
    public IdentifierTable identifierTable = new IdentifierTable();

    public TwoTuple getNextWord(String input_program) throws Exception {
        String temp = "";
        int state = 0;
        while(input_program.charAt(pointer) != '#'){
            char symbol = input_program.charAt(pointer);
            //忽略空格，回车，换行，Tab
            if(state == 0 && (symbol == ' ' || symbol == 10 || symbol == 13 || symbol == 9)){pointer ++;  continue;}
            // 标识符
            if(state == 0 && symbol == '$'){state = 1;  temp += symbol;  pointer ++;  continue;}
            if(state == 1 && (symbol >= 'a' && symbol <= 'z')){state = 2;  temp += symbol;  pointer ++;  continue;}
            if(state == 2) {
                if ((symbol >= 'a' && symbol <= 'z') || (symbol >= '0' && symbol <= '9')) {
                    temp += symbol;
                    pointer++;
                    continue;
                } else {
                    identifierTable.add(temp);
                    return new TwoTuple("标识符", temp);
                }
            }
            //if、int
            if(state == 0 && symbol == 'i'){state = 3;  temp += symbol;  pointer ++; continue;}
            if(state == 3 && symbol == 'f'){
                state = 0;//state = 4;
                temp += symbol;
                pointer ++;
                return new TwoTuple("if", temp);
            }
            if(state == 3 && symbol =='n'){state = 5;  temp += symbol;  pointer ++;  continue;}
            if(state == 5 && symbol == 't'){
                state = 0;//state = 6;
                temp += symbol;
                pointer ++;
                return new TwoTuple("变量说明", temp);
            }

            //end、else
            if(state == 0 && symbol == 'e'){state = 7;  temp += symbol;  pointer ++;  continue;}
            if(state == 7 && symbol == 'n'){state = 8;  temp += symbol;  pointer ++;  continue;}
            if(state == 8 && symbol == 'd'){
                state = 0;//state = 9;
                temp += symbol;
                pointer ++;
                return new TwoTuple("end", temp);
            }
            if(state == 7 && symbol == 'l'){state = 10;  temp += symbol;  pointer ++;  continue;}
            if(state == 10 && symbol == 's'){state = 11;  temp += symbol;  pointer ++;  continue;}
            if(state == 11 && symbol == 'e'){
                state = 0;//state = 12;
                temp += symbol;
                pointer ++;
                return new TwoTuple("else", temp);
            }

            //then
            if(state == 0 && symbol == 't'){state = 13;  temp += symbol;  pointer ++;  continue;}
            if(state == 13 && symbol == 'h'){state = 14;  temp += symbol;  pointer ++;  continue;}
            if(state == 14 && symbol == 'e'){state = 15;  temp += symbol;  pointer ++;  continue;}
            if(state == 15 && symbol == 'n'){
                state = 0;//state = 16;
                temp += symbol;
                pointer ++;
                return new TwoTuple("then", temp);
            }

            //while
            if(state == 0 && symbol == 'w'){state = 17;  temp += symbol;  pointer ++;  continue;}
            if(state == 17 && symbol == 'h'){state = 18;  temp += symbol;  pointer ++;  continue;}
            if(state == 18 && symbol == 'i'){state = 19;  temp += symbol;  pointer ++;  continue;}
            if(state == 19 && symbol == 'l'){state = 20;  temp += symbol;  pointer ++;  continue;}
            if(state == 20 && symbol == 'e'){
                state = 0;//state = 21;
                temp += symbol;
                pointer ++;
                return new TwoTuple("while", temp);
            }

            //do
            if(state == 0 && symbol == 'd'){state = 22;  temp += symbol;  pointer ++;  continue;}
            if(state == 22 && symbol == 'o'){
                state = 0;//state = 23;
                temp += symbol;
                pointer ++;
                return new TwoTuple("do", temp);
            }

            //begin
            if(state == 0 && symbol == 'b'){state = 24;  temp += symbol;  pointer ++;  continue;}
            if(state == 24 && symbol == 'e'){state = 25;  temp += symbol;  pointer ++;  continue;}
            if(state == 25 && symbol == 'g'){state = 26;  temp += symbol;  pointer ++;  continue;}
            if(state == 26 && symbol == 'i'){state = 27;  temp += symbol;  pointer ++;  continue;}
            if(state == 27 && symbol == 'n'){
                state = 0;//state = 28;
                temp += symbol;
                pointer ++;
                return new TwoTuple("begin", temp);
            }

            //=、==
            if(state == 0 && symbol == '='){state = 29;  temp += symbol;  pointer ++;  continue;}
            if(state == 29){
                if(symbol != '='){
                    state = 0;
                    return new TwoTuple("=", temp);
                }else {
                    state = 0;//state = 30
                    temp += symbol;
                    pointer++;
                }
                return new TwoTuple("关系运算符", temp);
            }

            // +
            if(state == 0 && symbol == '+'){
                //state = 31;
                temp += symbol;
                pointer ++;
                return new TwoTuple("加法运算符", temp);
            }

            //*
            if(state == 0 && symbol == '*'){
                //state = 32;
                temp += symbol;
                pointer ++;
                return new TwoTuple("乘法运算符", temp);
            }

            //<、<=
            if(state == 0 && symbol == '<'){state = 33;  temp += symbol;  pointer ++;  continue;}
            if(state == 33){
                if(symbol != '='){
                    state = 0;
                    return new TwoTuple("关系运算符", temp);
                }else {
                    state = 0;//state = 34
                    temp += symbol;
                    pointer++;
                    return new TwoTuple("关系运算符", temp);
                }
            }

            //>,>=
            if(state == 0 && symbol == '>'){state = 35;  temp += symbol;  pointer ++;  continue;}
            if(state == 35){
                if(symbol != '='){
                    state = 0;
                    return new TwoTuple("关系运算符", temp);
                }else {
                    state = 0;//state = 36
                    temp += symbol;
                    pointer++;
                    return new TwoTuple("关系运算符", temp);
                }
            }

            //!=
            if(state == 0 && symbol == '!'){state = 37;  temp += symbol;  pointer ++;  continue;}
            if(state == 37 && symbol == '='){
                state = 0;//state = 38
                temp += symbol;
                pointer++;
                return new TwoTuple("关系运算符", temp);
            }

            //;
            if(state == 0 && symbol == ';'){
                //state = 39;
                temp += symbol;
                pointer ++;
                return new TwoTuple(";", temp);
            }

            //，
            if(state == 0 && symbol == ','){
                //state = 40;
                temp += symbol;
                pointer ++;
                return new TwoTuple(",", temp);
            }

            //（
            if(state == 0 && symbol == '('){
                //state = 41;
                temp += symbol;
                pointer ++;
                return new TwoTuple("(", temp);
            }

            //）
            if(state == 0 && symbol == ')'){
                //state = 32;
                temp += symbol;
                pointer ++;
                return new TwoTuple(")", temp);
            }

            //常数
            if(state == 0 && (symbol >= '0' && symbol <='9')){state = 43;  temp += symbol;  pointer++;  continue;}
            if(state == 43){
                if(symbol >= '0' && symbol <='9'){
                    //state = 43
                    temp += symbol;
                    pointer ++;
                    continue;
                }else{
                    state = 0;
                    return new TwoTuple("常数", temp);
                }
            }
            System.out.println("Lexical Error at pointer " + pointer + ", and has been ignored.");
            pointer ++;
            return new TwoTuple("unknownIdentifier", symbol + "");
        }
        if(state != 0){
            System.out.println("illegal end.");
            if(state == 2)
                return new TwoTuple("标识符", temp);
            if(state == 43)
                return new TwoTuple("常数", temp);
        }
        return new TwoTuple("#", "#");
    }
}
