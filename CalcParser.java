import java.io.InputStream;
import java.io.IOException;
/*
expr=term, bitwiseXOR;
bitwiseXOR="^",term,bitwiseXOR|;
term=factor,bitwiseAND;
bitwiseAND="&",factor,bitwiseAND|;
factor="(",expr,")"|digit;
digit = "1"|"2"|"3"|"4"|"5"|"6"|"7"|"8"|"9"|"0";
*/

class CalcParser {
    private final InputStream in;
    private int lookahead;
    private int parenthesis; // counter for open/close parentesis

    public CalcParser(InputStream in) throws IOException {
        this.in=in;
        lookahead = in.read();
        this.parenthesis=0;
    }

    public void consume(int symbol) throws IOException {
        if (lookahead==symbol)
            this.lookahead = in.read();
        else
            throw new IOException();
    }

    private boolean isDigit(int c) {return '0' <= c && c <= '9';}

    private int evalDigit(int c) {return c - '0';}

    public int Expr() throws IOException {
        int a,b=-1;
        a = Term(0);
        if(lookahead=='^')
            b = bitwiseXOR(0);
        if(this.lookahead==-1 || this.lookahead=='\n'){
            if(this.parenthesis!=0){
                System.out.println("Parse Error. Unbalanced parenthesis!");
                throw new IOException();
            }
        }
        if (b!=-1)
            return a^b;

        return a;
    }

    private int Term(int finalNum) throws IOException {
        int cond = Factor();
        int bitAND = bitwiseAND(finalNum);
        if (bitAND!=-1)
            return cond & bitAND;
        return cond;
    }
    // 6&5)
    private int bitwiseAND(int finalNum) throws IOException {
        if(lookahead!='&')
            return -1;
        consume('&');
        if(this.lookahead=='^' || this.lookahead==')'){
            System.out.println("Parse Error. Wrong symbol after '&'.");
            throw new IOException();
        }
        int cond = Factor();
        int newTest = bitwiseAND(finalNum);
        if(newTest==-1)
            return cond;
        return finalNum;
    }

    private int bitwiseXOR(int finalNum) throws IOException {
        if(lookahead!='^')
            return -1;
        consume('^');
        if(this.lookahead=='&' || this.lookahead==')'){
            System.out.println("Parse Error. Wrong symbol after '^'.");
            throw new IOException();
        }
        int term = Term(finalNum);
        int newTest = bitwiseXOR(finalNum);
        if(newTest==-1)
            return term;
        return term^newTest;
    }
    // 6&5(3^5)
    private int Factor() throws IOException {
        int cond;
        if(isDigit(this.lookahead)) {
            cond = evalDigit(this.lookahead);
            consume(this.lookahead);
            if(this.lookahead=='(' || isDigit(this.lookahead)) {
                System.out.println("Parse Error. No such symbol is allowed after digit.");
                throw new IOException();
            }
            if(this.lookahead==')') {
                if((--this.parenthesis) < 0) {
                    System.out.println("Parse Error. Unbalanced parenthesis!");
                    throw new IOException();
                }
            }
            return cond;
        }
        else if (this.lookahead=='('){
            this.parenthesis++;
            consume('(');
            if(this.lookahead==')' || this.lookahead=='&' || this.lookahead=='^'){
                System.out.println("Parse Error. Only digit or new parenthesis is allowed after '('.");
                throw new IOException();
            }
            cond = Expr();
            consume(')');
            if(isDigit(this.lookahead)) {
                System.out.println("Parse Error. Can't put number after ')'.");
                throw new IOException();
            }
            if (this.lookahead==-1 || this.lookahead=='\n') {
                if (this.parenthesis!=0) {
                    System.out.println("Parse Error. Unbalanced parenthesis!");
                    throw new IOException();
                }
            }
            return cond;
        }
        System.out.println("Parse Error.");
        throw new IOException();
    }

}

/*
( 6 & 5 ) $
*/