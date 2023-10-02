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
	private void match(IToken token) throws SyntaxException, LexicalException {
		if (token == t){
			t = lexer.next();
		}
		else {
			throw new SyntaxException("Expected token: " + t.kind() + "Actual token: " + token.kind());
		}
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
		if (firstToken.kind() == ) {
			//Conditional Statement

		}
		else if (firstToken.kind() == ) {
			//LogicalOrExpr


		}
		else {
			throw new UnsupportedOperationException("THE PARSER HAS NOT BEEN IMPLEMENTED YET");
		}
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
//	private Expr PostFixExpr() throws PLCCompilerException {
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
	private Expr PrimaryExpr() throws PLCCompilerException {
		IToken firstToken = t;
		if (isKind(STRING_LIT, NUM_LIT, BOOLEAN_LIT, IDENT, CONST)) {
			if (t.kind() == STRING_LIT) {
				return new StringLitExpr(t);
			}
			else if (t.kind() == NUM_LIT) {
				return new NumLitExpr(t);
			}
			else if (t.kind() == BOOLEAN_LIT) {
				return new BooleanLitExpr(t);
			}
			else if(t.kind() == IDENT) {
				return new IdentExpr(t);
			}
			else if (t.kind() == CONST) {
				return new ConstExpr(t);
			}
			match(t);
		}

		else if (isKind(LPAREN)) {
			match(t);
			expr();
			match(t);
		}
		else if (isKind(LSQUARE)) {
			match(t);
			expr();
			match(t);
			expr();
			match(t);
			expr();
			match(t);
		}
		else {
			throw new UnsupportedOperationException("Expected kind: " + firstToken.kind() + "Actual Kind: " +t.kind());
		}
        return null;
    }
//	private Expr ChannelSelector() throws PLCCompilerException {
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
//	private Expr PixelSelector() throws PLCCompilerException {
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