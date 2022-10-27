package main;

import java.io.*;
import java.util.ArrayList;

import LexerPackage.*;

import static main.utils.*;

public class Compiler {
    private int syntax_pointer = 0;

    private static String input_program;

    private static TwoTuple word;

    private static Lexer lexer;

    private static MiddleCodeTable codeTable;

    private static TempVarTable tempVarTable;

    public static void main(String[] args) throws Exception {

        String input = readTxt("resource/in.txt");
        input_program = input + '#';
        File file = new File("resource/lexer_output.txt");
        if(file.exists()) file.delete();
        file = new File("resource/syntax_output.txt");
        if(file.exists()) file.delete();
        file = new File("resource/out.txt");
        if(file.exists()) file.delete();

        //Lexical Analysis
        lexer = new Lexer();
        word = lexer.getNextWord(input_program);
        writeTxt("resource/lexer_output.txt", word.toString());
        //Syntax Analysis
        Compiler compiler = new Compiler();
        codeTable = new MiddleCodeTable();
        tempVarTable = new TempVarTable();
        boolean res = compiler.parseProgram();
        if(res){
            System.out.println("程序编译结束");
        } else {
            System.out.println("程序编译失败");
        }
        codeTable.dump();
    }

    public boolean match(String type) throws Exception {
        if(!word.getType().equals(type)){
            System.out.println("Syntax Error at pointer " + syntax_pointer);
            writeTxt("resource/syntax_output.txt", "匹配" + type + "失败！");
            return false;
        }
        syntax_pointer = lexer.pointer;
        writeTxt("resource/syntax_output.txt", "匹配" + type + word.getValue() + "成功！");
        do{
            word = lexer.getNextWord(input_program);
            writeTxt("resource/lexer_output.txt", word.toString());
            if(word.getType().equals("标识符") && lexer.identifierTable.isExist(word.getValue()) == -1)
                throw new SemanticException("Identifier " + word.getValue() + " not declared error at pointer " + syntax_pointer);

        } while(word.getType().equals("unknownIdentifier"));

        writeTxt("resource/syntax_output.txt", "识别" + word.toString());
        return true;
    }

    public boolean parseProgram() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<程序> --> <变量说明部分>;<语句部分>");
        parseVariableDeclarationPart();
        lexer.identifierTable.flag = false;
        if (!match(";")) {throw new SyntaxException("Excepted ';' at pointer " + syntax_pointer);}
        parseStatementPart();
        if(word.getType().equals("#")){
            writeTxt("resource/syntax_output.txt", "语法分析结束");
            return true;
        }
        throw new SyntaxException("Compilation exited incorrectly at pointer " + syntax_pointer);
    }

    private boolean parseVariableDeclarationPart() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<变量说明部分> --> <变量说明><标识符列表>");
        String type = word.getValue();
        if(!match("变量说明")) throw new SyntaxException("Excepted 'int' at pointer " + syntax_pointer);
        parseIdList(type);
        return true;
    }

    private boolean parseIdList(String type) throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<标识符列表> --> <标识符><标识符列表Prime>");
        String idName = word.getValue();
        if (!match("标识符")) throw new SyntaxException("Excepted identifier at pointer " + syntax_pointer);
        else {
            lexer.identifierTable.updateTypeByName(idName, type);
        }
        ArrayList<String> varibleList = new ArrayList<String>();
        varibleList.add(idName);
        parseIdListPrime(type, varibleList);
        return true;
    }

    private boolean parseIdListPrime(String type, ArrayList<String> variableList) throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<标识符列表Prime> --> ,<标识符><标识符列表Prime>|ε");
        if(word.getType().equals(",")){
            match(",");
            String idName = word.getValue();
            if (!match("标识符")) throw new SyntaxException("Excepted identifier at pointer " + syntax_pointer);
            else if(variableList.contains(idName)){ //重复定义错误
                throw new SemanticException("Identifier " + idName + "  declared repeatedly error at pointer " + syntax_pointer);
            } else {
                lexer.identifierTable.updateTypeByName(idName, type);
                variableList.add(idName);
                parseIdListPrime(type, variableList);
            }
        } else {}
        return true;
    }

    private boolean parseStatementPart() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<语句部分> --> <语句>;<语句部分Prime>");
        parseStatement();
        if (!match(";")) throw new SyntaxException("Excepted ';' at pointer " + syntax_pointer);
        parseStatementPartPrime();
        return true;
    }

    private boolean parseStatementPartPrime() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<语句部分Prime> --> <语句>;<语句部分Prime>|ε");
        if(word.getType().equals("标识符") || word.getType().equals("if") || word.getType().equals("while")){
            parseStatement();
            if (!match(";")) throw new SyntaxException("Excepted ';' at pointer " + syntax_pointer);
            parseStatementPartPrime();
        } else {}
        return true;
    }

    private boolean parseStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<语句> --> <赋值语句>|<条件语句>|<循环语句>");
        if(word.getType().equals("标识符")){
            parseAssignStatement();
        } else if(word.getType().equals("if")){
            parseConditionalStatement();
        } else if(word.getType().equals("while")){
            parseLoopStatement();
        } else throw new SyntaxException("Excepted a statement at pointer " + syntax_pointer);;
        return true;
    }

    private boolean parseAssignStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<赋值语句> --> <标识符>=<表达式>");
        if(word.getType().equals("标识符")) {
            String idName = word.getValue();
            match("标识符");
            if (!match("=")) throw new SyntaxException("Excepted '=' at pointer " + syntax_pointer);
            Identifier E = parseExpression();
            if (E == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
            // 类型不匹配
//            System.out.println(lexer.identifierTable.getTypeByName(idName));
//            System.out.println(E.getType());
            if (!lexer.identifierTable.getTypeByName(idName).equals(E.getType()))
                throw new SemanticException("Type mismatch error at pointer " + syntax_pointer);
            codeTable.add("=", E.getName(), null, idName);
            lexer.identifierTable.updateValueByName(idName, E.getValue());
            writeTxt("resource/syntax_output.txt", "产生赋值四元式");
        }
        return true;
    }

    public boolean parseConditionalStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<条件语句> --> if (<条件>) then <嵌套语句> ;else <嵌套语句>");
        match("if");
        if (!match("(")) throw new SyntaxException("Excepted '(' at pointer " + syntax_pointer);
        Identifier E = parseCondition();
        if(E == null) throw new SyntaxException("Condition is null at pointer " + syntax_pointer);
        if (!match(")")) throw new SyntaxException("Excepted ')' at pointer " + syntax_pointer);
        if (!match("then")) throw new SyntaxException("Excepted 'then' at pointer " + syntax_pointer);

        codeTable.add("jnz", E.getName(), null, codeTable.NXQ() + 2 + "");
        int falseExitIndex = codeTable.NXQ();
        codeTable.add("jmp", null, null, "0");

        parseNestedStatement();

        int exitIndex = codeTable.NXQ();
        codeTable.add("jmp", null, null, "0");
        codeTable.backPatch(falseExitIndex, exitIndex + 1 + "");
        if (!match(";")) throw new SyntaxException("Excepted ';' at pointer " + syntax_pointer);
        if (!match("else")) throw new SyntaxException("Excepted 'else' at pointer " + syntax_pointer);

        parseNestedStatement();
        codeTable.backPatch(exitIndex, codeTable.NXQ() + "");

        return true;
    }

    public boolean parseLoopStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<循环语句> --> while (<条件>) do <嵌套语句>");
        int loopIndex = codeTable.NXQ();

        match("while");
        if (!match("(")) throw new SyntaxException("Excepted '(' at pointer " + syntax_pointer);
        Identifier E = parseCondition();
        if(E == null) throw new SyntaxException("Condition is null at pointer " + syntax_pointer);

        if (!match(")")) throw new SyntaxException("Excepted ')' at pointer " + syntax_pointer);
        codeTable.add("jnz", E.getName(), null, codeTable.NXQ() + 2 + "");
        int falseExitIndex = codeTable.NXQ();
        codeTable.add("jmp", null, null, "0");

        if (!match("do")) throw new SyntaxException("Excepted 'do' at pointer " + syntax_pointer);
        parseNestedStatement();
        codeTable.backPatch(falseExitIndex, codeTable.NXQ() + "");
        codeTable.add("jmp", null, null, loopIndex + "");
        return true;
    }

    public Identifier parseExpression() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<表达式> --> <项><表达式Prime>");
        if(word.getType().equals("标识符") || word.getType().equals("常数") || word.getType().equals("(")) {
            Identifier E1 = parseItem();
            if(E1 == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
            Identifier E2 = parseExpressionPrime(E1);
            return E2;
        }else {
            return null;
        }
    }

    public Identifier parseExpressionPrime(Identifier E1) throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<表达式Prime> --> <加法运算符><项><表达式Prime>|ε");
        if(word.getType().equals("加法运算符")){
            match("加法运算符");
            Identifier E2 = parseItem();
            if(E2 == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
            //类型不匹配
            if (!E1.getType().equals(E2.getType()))
                throw new SemanticException("Type mismatch error at pointer " + syntax_pointer);
            Identifier T = tempVarTable.getNewTempVar();
            codeTable.add("+", E1.getName(), E2.getName(), T.getName());
            writeTxt("resource/syntax_output.txt", "产生加法四元式");
            int value = Integer.parseInt(E1.getValue()) + Integer.parseInt(E2.getValue());
            T.setValue(value + "");
            T.setType(E1.getType());
            Identifier E = parseExpressionPrime(T);
            return E;
        } else {return E1;}
    }

    public Identifier parseItem() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<项> --> <因子><项Prime>");
        if(word.getType().equals("标识符") || word.getType().equals("常数") || word.getType().equals("(")) {
            Identifier E1 = parseFactor();
            if(E1 == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
            Identifier E2 = parseItemPrime(E1);
            return E2;
        }
        return null;
    }

    public Identifier parseItemPrime(Identifier E1) throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<项Prime> --> <乘法运算符><因子><项Prime>|ε");
        if(word.getType().equals("乘法运算符")) {
            match("乘法运算符");
            Identifier E2 = parseFactor();
            if(E2 == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
            // 类型不匹配
            if (!E1.getType().equals(E2.getType()))
                throw new SemanticException("Type mismatch error at pointer " + syntax_pointer);
            Identifier T = tempVarTable.getNewTempVar();
            codeTable.add("*", E1.getName(), E2.getName(), T.getName());
            writeTxt("resource/syntax_output.txt", "产生乘法四元式");
            int value = Integer.parseInt(E1.getValue()) * Integer.parseInt(E2.getValue());
            T.setValue(value + "");
            T.setType(E1.getType());
            Identifier E = parseItemPrime(T);
            return E;
        } else {return E1;}
    }

    public Identifier parseFactor() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<因子> --> <标识符>|<常数>|(<表达式>)");
        Identifier E;
        if(word.getType().equals("标识符")){
            E = new Identifier(word.getValue(), lexer.identifierTable.getTypeByName(word.getValue()), lexer.identifierTable.getValueByName(word.getValue()));
            match("标识符");
            return E;
        } else if(word.getType().equals("常数")) {
            E = new Identifier(word.getValue(), "int", word.getValue());
            match("常数");
            return E;
        } else if(word.getType().equals("(")){
            match("(");
            E = parseExpression();
            if (!match(")")) {throw new SyntaxException("Excepted ')' at pointer " + syntax_pointer);}
            return E;
        };
        return null;
    }

    public Identifier parseCondition() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<条件> --> <表达式><关系运算符><表达式>");
        Identifier E1 = parseExpression();
        String op = word.getValue();
        if (!match("关系运算符")) {throw new SyntaxException("Excepted relational operator at pointer " + syntax_pointer);}
        Identifier E2 = parseExpression();
        if (E2 == null || E1 == null) throw new SyntaxException("Expression is null at pointer " + syntax_pointer);
        // 类型不匹配
        if (!E1.getType().equals(E2.getType()))
            throw new SemanticException("Type mismatch error at pointer " + syntax_pointer);
        Identifier T = tempVarTable.getNewTempVar();
        codeTable.add(op, E1.getName(), E2.getName(), T.getName());

        T.setType("bool");
        boolean value;
        switch (op){
            case "!=": value = Integer.parseInt(E1.getValue()) != Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
            case "==": value = Integer.parseInt(E1.getValue()) == Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
            case ">": value = Integer.parseInt(E1.getValue()) > Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
            case "<": value = Integer.parseInt(E1.getValue()) < Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
            case ">=": value = Integer.parseInt(E1.getValue()) >= Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
            case "<=": value = Integer.parseInt(E1.getValue()) <= Integer.parseInt(E2.getValue());
                T.setValue(value + "");
                break;
        }
        return T;
    }

    public boolean parseCompoundStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<复合语句> --> begin <语句部分> end");
        match("begin");
        parseStatementPart();
        if (!match("end")) throw new SyntaxException("Excepted 'end' at pointer " + syntax_pointer);
        return true;
    }

    public boolean parseNestedStatement() throws Exception {
        writeTxt("resource/syntax_output.txt", "推导:<嵌套语句> --> <语句>|<复合语句>");
        if(word.getType().equals("标识符") || word.getType().equals("if") || word.getType().equals("while")) {
            parseStatement();
        } else if(word.getType().equals("begin")) {
            parseCompoundStatement();
        } else throw new SyntaxException("Excepted a statement or a compound statement at pointer " + syntax_pointer);
        return true;
    }
}
