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

import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.LexicalException;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.SyntaxException;

import static edu.ufl.cise.cop4020fa23.Kind.*;

/**
Expr::=  ConditionalExpr | LogicalOrExpr    
ConditionalExpr ::=  ?  Expr  :  Expr  :  Expr 
LogicalOrExpr ::= LogicalAndExpr (    (   |   |   ||   ) LogicalAndExpr)*
LogicalAndExpr ::=  ComparisonExpr ( (   &   |  &&   )  ComparisonExpr)*
ComparisonExpr ::= PowExpr ( (< | > | == | <= | >=) PowExpr)*
PowExpr ::= AdditiveExpr ** PowExpr |   AdditiveExpr
AdditiveExpr ::= MultiplicativeExpr ( ( + | -  ) MultiplicativeExpr )*
MultiplicativeExpr ::= UnaryExpr (( * |  /  |  % ) UnaryExpr)*
UnaryExpr ::=  ( ! | - | length | width) UnaryExpr  |  UnaryExprPostfix
UnaryExprPostfix::= PrimaryExpr (PixelSelector | ε ) (ChannelSelector | ε )
PrimaryExpr ::=STRING_LIT | NUM_LIT |  IDENT | ( Expr ) | Z 
    ExpandedPixel  
ChannelSelector ::= : red | : green | : blue
PixelSelector  ::= [ Expr , Expr ]
ExpandedPixel ::= [ Expr , Expr , Expr ]
Dimension  ::=  [ Expr , Expr ]                         

 */

public class ExpressionParser implements IParser {
	final ILexer lexer;
	private IToken t;

	/**
	 * @param lexer
	 * @throws LexicalException
	 */
	public ExpressionParser(ILexer lexer) throws LexicalException {
		super();
		this.lexer = lexer;
		t = lexer.next();
	}


	@Override
	public AST parse() throws PLCCompilerException {
		Expr e = expr();
		return e;
	}

	//Parsing 4 lecture, match, kind codes
	private void match(Kind token) throws SyntaxException, LexicalException {
		if (token == t.kind()){
			consume();
		}
		else {
			throw new SyntaxException("Expected token: " + t.kind() + "Actual token: " + token);
		}
	}
	private void consume() throws LexicalException {
		t = lexer.next();
	}
	protected boolean isKind(Kind kind) {
		return t.kind() == kind;
	}
	protected boolean isKind(Kind... kinds) {
		for (Kind k: kinds) {
			if (k == t.kind()) {
				return true;
			}
		}
		return false;
	}
	private Expr expr() throws PLCCompilerException {
		IToken firstToken = t;
//		return PrimaryExpr();
		return PostFixExpr();
//		if (firstToken.kind() == ) {
//			//Conditional expr()
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
	}
//	private Expr ConditionalExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr LogicalOrExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr LogicalAndExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr ComparisonExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr PowExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr AdditiveExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr MultiplicativeExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
//	private Expr UnaryExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}
	private Expr PostFixExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr primary = PrimaryExpr();
		PixelSelector pixel = null;
		ChannelSelector channel = null;
		if (isKind(LSQUARE)) {
			pixel = PixelSelector();
		}
		if (isKind(COLON)) {
			channel = ChannelSelector();
		}
		return new PostfixExpr(firstToken,primary,pixel,channel);
	}
	private Expr PrimaryExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr e = null;
		if (isKind(STRING_LIT, NUM_LIT, BOOLEAN_LIT, IDENT, CONST)) {
			if (t.kind() == STRING_LIT) {
				e = new StringLitExpr(t);
				match(STRING_LIT);
			}
			else if (t.kind() == NUM_LIT) {
				e = new NumLitExpr(t);
				match(NUM_LIT);
			}
			else if (t.kind() == BOOLEAN_LIT) {
				e = new BooleanLitExpr(t);
				match(BOOLEAN_LIT);
			}
			else if(t.kind() == IDENT) {
				e = new IdentExpr(t);
				match(IDENT);
			}
			else if (t.kind() == CONST) {
				e = new ConstExpr(t);
				match(CONST);
			}
		}

		else if (isKind(LPAREN)) {
			match(LPAREN);
			expr();
			e = expr();
			match(RPAREN);
		}
		else if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expr red = expr();
			match(COMMA);
			Expr grn = expr();
			match(COMMA);
//			Expr Blue();
			match(RSQUARE);
//			e = new ExpandedPixelExpr();
		}
		else {
			throw new UnsupportedOperationException("Expected kind: " + firstToken.kind() + "Actual Kind: " +t.kind());
		}
		return e;
    }
	private ChannelSelector ChannelSelector() throws PLCCompilerException {
		IToken firstToken = t;
		IToken color = null;
		match(COLON);
		if(isKind(RES_red)){
			color = t;
			match(RES_red);
		}else if(isKind(RES_blue)){
			color = t;
			match(RES_blue);
		}else if(isKind(RES_green)){
			color = t;
			match(RES_green);
		}
		return new ChannelSelector(firstToken,color);
	}
	private PixelSelector PixelSelector() throws PLCCompilerException {
		IToken firstToken = t;
		Expr x = null;
		Expr y = null;

		return null;
	}
//	private Expr ExpandedPixelExpr() throws PLCCompilerException {
//		IToken firstToken = t;
//		if (firstToken.kind() == ) {
//			//Conditional Statement
//
//		}
//		else if (firstToken.kind() == ) {
//			//LogicalOrExpr
//
//
//		}
//		else {
//			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
//		}
//	}

}