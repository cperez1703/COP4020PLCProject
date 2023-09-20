/*Copyright 2023 by Beverly A Sanders
 *
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the
 * University of Florida during the fall semester 2023 as part of the course project.
 *
 * No other use is authorized.
 *
 * This code may not be posted on a public web site either during or after the course.
 */
package edu.ufl.cise.cop4020fa23;

import static edu.ufl.cise.cop4020fa23.Kind.EOF;

import edu.ufl.cise.cop4020fa23.exceptions.LexicalException;

import java.util.HashMap;
import java.util.Map;


public class Lexer implements ILexer {

	String input;
	int pos = 0;
	int col = 1;
	int line = 1;
	char[] arr;
	int startPos;
	int startCol;
	int length;
	private enum State{START,IN_IDENT,HAVE_ZERO,HAVE_DOT,IN_FLOAT,IN_NUM,HAVE_EQ,HAVE_MINUS,HAVE_LSQUARE,HAVE_HASH,HAVE_AND,HAVE_LT,HAVE_GT,HAVE_QUOTE,HAVE_OR,HAVE_STAR,HAVE_COLON}
	Map<Character,Kind> m = new HashMap<>();
	Map<Character,State> s = new HashMap<>();
	Map<String, String> constants = new HashMap<>();
	Map<String, String> reserved = new HashMap<>();
	Map<String,Kind> reservedKind = new HashMap<>();



	public Lexer(String input) {
		this.input = input;
		arr = input.toCharArray();
		m.put('+',Kind.PLUS);
		m.put(',',Kind.COMMA);
		m.put(';',Kind.SEMI);
		m.put(']',Kind.RSQUARE);
		m.put('%',Kind.MOD);
		m.put('/',Kind.DIV);
		m.put('?',Kind.QUESTION);
		m.put('!',Kind.BANG);
		m.put('0',Kind.NUM_LIT);
		m.put('(',Kind.LPAREN);
		m.put(')',Kind.RPAREN);
		m.put('^',Kind.RETURN);
		s.put('[',State.HAVE_LSQUARE);
		s.put('#',State.HAVE_HASH);
		s.put('&',State.HAVE_AND);
		s.put('<',State.HAVE_LT);
		s.put('=',State.HAVE_EQ);
		s.put('-',State.HAVE_MINUS);
		s.put('>',State.HAVE_GT);
		s.put('"',State.HAVE_QUOTE);
		s.put('|',State.HAVE_OR);
		s.put('*',State.HAVE_STAR);
		s.put(':',State.HAVE_COLON);
		constants.put("Z","");
		constants.put("BLACK","");
		constants.put("BLUE","");
		constants.put("CYAN","");
		constants.put("DARK_GRAY","");
		constants.put("GRAY","");
		constants.put("GREEN","");
		constants.put("LIGHT_GRAY","");
		constants.put("MAGENTA","");
		constants.put("ORANGE","");
		constants.put("PINK","");
		constants.put("RED","");
		constants.put("WHITE","");
		constants.put("YELLOW","");
		reserved.put("image","");
		reserved.put("pixel","");
		reserved.put("int","");
		reserved.put("string","");
		reserved.put("void","");
		reserved.put("boolean","");
		reserved.put("write","");
		reserved.put("height","");
		reserved.put("width","");
		reserved.put("if","");
		reserved.put("fi","");
		reserved.put("do","");
		reserved.put("od","");
		reserved.put("red","");
		reserved.put("green","");
		reserved.put("blue","");
		reservedKind.put("image",Kind.RES_image);
		reservedKind.put("pixel",Kind.RES_pixel);
		reservedKind.put("int",Kind.RES_int);
		reservedKind.put("string",Kind.RES_string);
		reservedKind.put("void",Kind.RES_void);
		reservedKind.put("boolean",Kind.RES_boolean);
		reservedKind.put("write",Kind.RES_write);
		reservedKind.put("height",Kind.RES_height);
		reservedKind.put("width",Kind.RES_width);
		reservedKind.put("if",Kind.RES_if);
		reservedKind.put("fi",Kind.RES_fi);
		reservedKind.put("do",Kind.RES_do);
		reservedKind.put("od",Kind.RES_od);
		reservedKind.put("red",Kind.RES_red);
		reservedKind.put("green",Kind.RES_green);
		reservedKind.put("blue",Kind.RES_blue);
	}

	@Override
	public IToken next() throws LexicalException {
		State state = State.START;
		while(true){
			if(pos>=input.length()){return new Token(EOF, 0, 0, null, new SourceLocation(1, 1));}
			char ch = input.charAt(pos);
			switch(state){
				case START->{
					startPos = pos;
					startCol = col;
					switch(ch){
						case ' ', '\t', '\n', '\r' ->{
							pos++;
							col++;
							if(ch == '\n'){
								col = 1;
								line++;
							}
						}
						case '+',',',';',']','%','/','?','!','0','(',')','^'->{
							pos++;
							col++;
							return new Token(m.get(ch),startPos,1,arr,new SourceLocation(line,startCol));
						}
						case '[','#','&','<','=','-','>','"','|','*',':' ->{
							state = s.get(ch);
							pos++;
							col++;
							length = 1;
							if(pos>=input.length()){
								switch(state) {
									case HAVE_LSQUARE->{return new Token(Kind.LSQUARE, startPos, 1, arr,new SourceLocation(line,startCol));}
									case HAVE_HASH -> throw new LexicalException("Invalid Hash");
									case HAVE_AND-> {return new Token(Kind.BITAND, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_LT -> {return new Token(Kind.LT, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_EQ -> {return new Token(Kind.ASSIGN, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_MINUS->{return new Token(Kind.MINUS, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_GT->{return new Token(Kind.GT, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_QUOTE->throw new LexicalException("New Line error in string");
									case HAVE_OR->{return new Token(Kind.BITOR, startPos, 1, arr, new SourceLocation(line,startCol));}
									case HAVE_STAR->{return new Token(Kind.TIMES, startPos, 1, arr, new SourceLocation(line, startCol));}
									case HAVE_COLON->{return new Token(Kind.COLON, startPos, 1, arr, new SourceLocation(line, startCol));}
								}
							}
						}
						case 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
								'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','$','_'->{
							state = State.IN_IDENT;
							pos++;
							col++;
							length = 1;
							if(pos>=input.length()){
								if(constants.get(input.substring(startPos,startPos+length))!=null)
									return new Token(Kind.CONST, startPos, length, arr, new SourceLocation(line, startCol));
								if(reserved.get(input.substring(startPos,startPos+length))!=null)
									return new Token(reservedKind.get(input.substring(startPos,startPos+length)), startPos, length, arr, new SourceLocation(line, startCol));
								if(input.substring(startPos, startPos + length).equals("TRUE")||input.substring(startPos, startPos + length).equals("FALSE"))
									return new Token(Kind.BOOLEAN_LIT, startPos, length, arr, new SourceLocation(line, startCol));
								return new Token(Kind.IDENT, startPos, length, arr, new SourceLocation(line, startCol));
							}
						}
						case '1', '2', '3', '4', '5', '6', '7', '8', '9'-> {
							pos++;
							col++;
							length = 1;
							state = State.IN_NUM;
						}
						default->throw new LexicalException("Invalid Char");
					}
				}
				case IN_IDENT->{
					switch(ch){
						case 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
								'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','$','_',
								'0','1','2','3','4','5','6','7','8','9'->{
							pos++;
							col++;
							length++;
							if(pos>=input.length()){
								if(constants.get(input.substring(startPos,startPos+length))!=null)
									return new Token(Kind.CONST, startPos, length, arr, new SourceLocation(line, startCol));
								if(reserved.get(input.substring(startPos,startPos+length))!=null)
									return new Token(reservedKind.get(input.substring(startPos,startPos+length)), startPos, length, arr, new SourceLocation(line, startCol));
								if(input.substring(startPos, startPos + length).equals("TRUE")||input.substring(startPos, startPos + length).equals("FALSE"))
									return new Token(Kind.BOOLEAN_LIT, startPos, length, arr, new SourceLocation(line, startCol));
								return new Token(Kind.IDENT, startPos, length, arr, new SourceLocation(line, startCol));
							}
						}
						default->{
							if(constants.get(input.substring(startPos,startPos+length))!=null)
								return new Token(Kind.CONST, startPos, length, arr, new SourceLocation(line, startCol));
							if(reserved.get(input.substring(startPos,startPos+length))!=null)
								return new Token(reservedKind.get(input.substring(startPos,startPos+length)), startPos, length, arr, new SourceLocation(line, startCol));
							if(input.substring(startPos, startPos + length).equals("TRUE")||input.substring(startPos, startPos + length).equals("FALSE"))
								return new Token(Kind.BOOLEAN_LIT, startPos, length, arr, new SourceLocation(line, startCol));
							return new Token(Kind.IDENT, startPos, length, arr, new SourceLocation(line, startCol));
						}
					}
				}
//           case HAVE_ZERO->{
//
//           }
//           case HAVE_DOT->{
//
//           }
//           case IN_FLOAT->{
//
//           }
				case IN_NUM->{
					if (ch >= '0' && ch <= '9') {
						col++;
						pos++;
						length++;
						if(pos>=input.length()){
							String intParse = input.substring(startPos, startPos+length);
							try {
								Integer.parseInt(intParse);
							}
							catch (Exception e){
								throw new LexicalException("Num too large");
							}
							return new Token(Kind.NUM_LIT, startPos, length, arr, new SourceLocation(line, startCol));
						}
					}
					else {
						String intParse = input.substring(startPos, startPos+length);
						try {
							Integer.parseInt(intParse);
						}
						catch (Exception e){
							throw new LexicalException("Num too large");
						}
						return new Token(Kind.NUM_LIT, startPos, length, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_EQ->{
					if (ch == '=') {
						col++;
						pos++;
						return new Token(Kind.EQ, startPos, 2, arr, new SourceLocation(line, startCol));
					}
					else {
						return new Token(Kind.ASSIGN, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_MINUS->{
					if (ch == '>') {
						col++;
						pos++;
						return new Token(Kind.RARROW, startPos, 2, arr, new SourceLocation(line, startCol));
					} else {
						return new Token(Kind.MINUS, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_LSQUARE->{
					if (input.charAt(pos) == ']') {
						pos++;
						col++;
						return new Token(Kind.BOX, startPos, 2, arr, new SourceLocation(line, startCol));
					}
					return new Token(Kind.LSQUARE, startPos, 1, arr,new SourceLocation(line,startCol));
				}
				case HAVE_HASH->{
					if(input.charAt(pos) == '#'){
						while(input.charAt(pos) != '\n'){
							pos++;
							col++;
							if ((int)input.charAt(pos-1) > 126 || (int)input.charAt(pos-1) < 32) throw new LexicalException("Invalid Character");
						}
						col = 0;
						state = State.START;
					} else {
						throw new LexicalException("Invalid Token");
					}
				}
				case HAVE_AND->{
					if (input.charAt(pos) == '&') {
						pos++;
						col++;
						return new Token(Kind.AND, startPos, 2, arr, new SourceLocation(line, startCol));
					} else {
						return new Token(Kind.BITAND, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_OR->{
					if(input.charAt(pos) == '|'){
						pos++;
						col++;
						return new Token(Kind.OR, startPos, 2, arr, new SourceLocation(line,startCol));
					} else {
						return new Token(Kind.BITOR, startPos, 1, arr, new SourceLocation(line,startCol));
					}
				}
				case HAVE_LT->{
					if (input.charAt(pos) == '=') {
						pos++;
						col++;
						return new Token(Kind.LE, startPos, 2, arr, new SourceLocation(line, startCol));
					} else if (input.charAt(pos) == ':') {
						pos++;
						col++;
						return new Token(Kind.BLOCK_OPEN, startPos, 2, arr, new SourceLocation(line, startCol));
					} else {
						return new Token(Kind.LT, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_GT->{
					if (input.charAt(pos) == '=') {
						pos++;
						col++;
						return new Token(Kind.GE, startPos, 2, arr, new SourceLocation(line, startCol));
					} else {
						return new Token(Kind.GT, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_QUOTE->{
					while(input.charAt(pos) != '"') {
						col++;
						pos++;
						length++;
						if(pos==input.length()) throw new LexicalException("No closing quote");
						if (input.charAt(pos)== '\n') throw new LexicalException("String Error");
						if ((int)input.charAt(pos-1) > 126 || (int)input.charAt(pos-1) < 32) throw new LexicalException("Invalid Character");
					}
					col++;
					pos++;
					length++;
					return new Token(Kind.STRING_LIT, startPos, length, arr, new SourceLocation(line, startCol));
				}
				case HAVE_STAR->{
					if (ch == '*') {
						col++;
						pos++;
						return new Token(Kind.EXP, startPos, 2, arr, new SourceLocation(line, startCol));
					}
					else {
						return new Token(Kind.TIMES, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				case HAVE_COLON -> {
					if (input.charAt(pos) == '>'){
						pos++;
						col++;
						return new Token(Kind.BLOCK_CLOSE, startPos, 2, arr, new SourceLocation(line, startCol));
					}
					else {
						return new Token(Kind.COLON, startPos, 1, arr, new SourceLocation(line, startCol));
					}
				}
				default->throw new IllegalStateException("lexer bug");
			}
		}
//     Original return statement
//     return new Token(EOF, 0, 0, null, new SourceLocation(1, 1));
	}
}