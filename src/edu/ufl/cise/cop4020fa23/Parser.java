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

import java.util.ArrayList;
import java.util.List;

public class Parser implements IParser {
	
	final ILexer lexer;
	private IToken t;

	public Parser(ILexer lexer) throws LexicalException {
		super();
		this.lexer = lexer;
		t = lexer.next();
	}


	@Override
	public AST parse() throws PLCCompilerException {
		AST e = program();
		return e;
	}

	private AST program() throws PLCCompilerException {
		IToken firstToken = t;
		IToken type = null;
		IToken name = null;
		List<NameDef> params = new ArrayList<NameDef>();
		Block block = null;
		if(isKind(RES_image,RES_pixel,RES_int,RES_string,RES_void,RES_boolean)){
			type = Type();
			name = t;
			match(IDENT);
			match(LPAREN);
			params = ParamList();
			match(RPAREN);
			block = Block();
		}else{
			throw new UnsupportedOperationException();
		}
		if(!isKind(EOF)){
			throw new SyntaxException("Trailing Elements");
		}
		return new Program(firstToken, type, name, params, block);
	}

	private void match(Kind token) throws SyntaxException, LexicalException {
		if (token == t.kind()){
			consume();
		}
		else {
			throw new SyntaxException("Expected token: " + t.kind() + " Actual token: " + token);
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

	private Block Block() throws PLCCompilerException{
		IToken firstToken = t;
		match(BLOCK_OPEN);
		List<Block.BlockElem> elems = new ArrayList<>();
		while(!isKind(BLOCK_CLOSE)){
			if(isKind(RES_image,RES_pixel,RES_int,RES_string,RES_void,RES_boolean)){
				elems.add(Declaration());
			}else if(isKind(IDENT,RES_write,RES_do,RES_if,RETURN,BLOCK_OPEN)){
				elems.add(Statement());
			}
			match(SEMI);
		}
		match(BLOCK_CLOSE);
		return new Block(firstToken, elems);
	}

	private List<NameDef> ParamList() throws PLCCompilerException{
		List<NameDef> params = new ArrayList<>();
		while(isKind(RES_image,RES_pixel,RES_int,RES_string,RES_void,RES_boolean)){
			params.add(NameDef());
			if(isKind(COMMA)) match(COMMA);
		}
		return params;
	}

	private NameDef NameDef() throws PLCCompilerException{
		IToken firstToken = t;
		Dimension dimension = null;
		IToken typeToken = Type();
		if(isKind(LSQUARE)){
			dimension = Dimension();
		}
		IToken identToken = t;
		match(IDENT);
		return new NameDef(firstToken,typeToken,dimension,identToken);
	}

	private IToken Type() throws PLCCompilerException{
		if(isKind(RES_image,RES_pixel,RES_int,RES_string,RES_void,RES_boolean)){
			IToken token = t;
			consume();
			return token;
		}else{
			throw new SyntaxException("Invalid Type");
		}
	}

	private Declaration Declaration() throws PLCCompilerException{
		IToken firstToken = t;
		NameDef nameDef = null;
		Expr initializer = null;
		if(isKind(RES_image,RES_pixel,RES_int,RES_string,RES_void,RES_boolean)){
			nameDef = NameDef();
			if(isKind(ASSIGN)){
				match(ASSIGN);
				initializer = expr();
			}
			return new Declaration(firstToken,nameDef,initializer);
		}else{
			throw new SyntaxException("Invalid Declaration");
		}
	}
	private Expr expr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr e = null;
		if (isKind(QUESTION)) {
			e = ConditionalExpr();
		}
		else if (isKind(STRING_LIT, NUM_LIT, BOOLEAN_LIT, IDENT, CONST,LPAREN,LSQUARE,BANG,MINUS,RES_width,RES_height)) {
			e = LogicalOrExpr();
		}
		else {
			throw new SyntaxException("Invalid Parser Expression");
		}
		return e;
	}
	private Expr ConditionalExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr guard = null;
		Expr trueExpr = null;
		Expr falseExpr = null;
		match(Kind.QUESTION);
		guard = expr();
		match(Kind.RARROW);
		trueExpr = expr();
		match(Kind.COMMA);
		falseExpr = expr();
		return new ConditionalExpr(firstToken, guard, trueExpr, falseExpr);
	}
	private Expr LogicalOrExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = LogicalAndExpr();
		while(isKind(OR,BITOR)){
			IToken op = t;
			consume();
			right = LogicalAndExpr();
			left = new BinaryExpr(firstToken,left,op,right);
		}
		return left;
	}
	private Expr LogicalAndExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = ComparisonExpr();
		while(isKind(AND,BITAND)){
			IToken op = t;
			consume();
			right = ComparisonExpr();
			left = new BinaryExpr(firstToken,left,op,right);
		}
		return left;
	}
	private Expr ComparisonExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = PowExpr();
		while(isKind(LT,GT,EQ,LE,GE)){
			IToken op = t;
			consume();
			right = PowExpr();
			left = new BinaryExpr(firstToken,left,op,right);
		}
		return left;
	}
	private Expr PowExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = AdditiveExpr();
		while(isKind(EXP)){
			IToken op = t;
			consume();
			right = PowExpr();
			left = new BinaryExpr(firstToken,left,op,right);
		}
		return left;
	}
	private Expr AdditiveExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = MultiplicativeExpr();
		while(isKind(PLUS) || isKind(MINUS)) {
			IToken op = t;
			consume();
			right = MultiplicativeExpr();
			left = new BinaryExpr(firstToken, left, op, right);
		}
		return left;
	}
	private Expr MultiplicativeExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr left = null;
		Expr right = null;
		left = UnaryExpr();
		while(isKind(TIMES)||isKind(DIV)||isKind(MOD)){
			IToken op = t;
			consume();
			right = UnaryExpr();
			left = new BinaryExpr(firstToken, left ,op ,right);
		}
		return left;//
	}
	private Expr UnaryExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr e = null;
		IToken op = null;
		if(isKind(BANG,MINUS,RES_width,RES_height)){
			op = firstToken;
			consume();
			e = UnaryExpr();//
			return new UnaryExpr(firstToken,op,e);
		}else{
			e = PostFixExpr();
		}
		return e;
	}
	private Expr PostFixExpr() throws PLCCompilerException {
		IToken firstToken = t;
		Expr primary = PrimaryExpr();
		PixelSelector pixel = null;
		ChannelSelector channel = null;
		if (isKind(LSQUARE)) {
			pixel = PixelSelector();
			if(isKind(COLON)){
				channel = ChannelSelector();
			}
			return new PostfixExpr(firstToken,primary,pixel,channel);
		}
		else if (isKind(COLON)) {
			channel = ChannelSelector();
			return new PostfixExpr(firstToken,primary,pixel,channel);
		}else{
			return primary;
		}
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
			e = expr();
			match(RPAREN);
		}
		else if (isKind(LSQUARE)) {
			match(LSQUARE);
			Expr red = expr();
			match(COMMA);
			Expr grn = expr();
			match(COMMA);
			Expr blu = expr();
			match(RSQUARE);
			e = new ExpandedPixelExpr(firstToken,red,grn,blu);
		}
		else {
			throw new SyntaxException("Expected kind: " + firstToken.kind() + " Actual Kind: " +t.kind());
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
		}else{
			throw new SyntaxException("No given color for ChannelSelector");
		}
		return new ChannelSelector(firstToken,color);
	}
	private PixelSelector PixelSelector() throws PLCCompilerException {
		IToken firstToken = t;
		match(LSQUARE);
		Expr x = null;
		Expr y = null;
		x = expr();
		match(COMMA);
		y = expr();
		match(RSQUARE);
		return new PixelSelector(firstToken,x,y);
	}

	private Dimension Dimension() throws PLCCompilerException{
		IToken firstToken = t;
		match(LSQUARE);
		Expr x = null;
		x = expr();
		match(COMMA);
		Expr y = null;
		y = expr();
		match(RSQUARE);
		return new Dimension(firstToken, x, y);
	}

	private LValue LValue() throws PLCCompilerException{
		IToken firstToken = t;
		IToken name = null;
		PixelSelector pixelSelector = null;
		ChannelSelector channelSelector = null;
		if (isKind(IDENT)) {
			name = t;
			match(IDENT);
			if (isKind(LSQUARE)) {
				pixelSelector = PixelSelector();
				if (isKind(COLON)) {
					channelSelector = ChannelSelector();
				}
				return new LValue(firstToken, name, pixelSelector, channelSelector);
			}
			else if (isKind(COLON)) {
				channelSelector = ChannelSelector();
				return new LValue(firstToken, name, pixelSelector, channelSelector);
			}
			return new LValue(firstToken, name, pixelSelector, channelSelector);
		}
		else {
			throw new SyntaxException("Some LValue implementation/input error!");
		}
	}

	private Statement Statement() throws PLCCompilerException{
		IToken firstToken = t;
		LValue LValue = null;
		Expr Expr = null;
		if (isKind(IDENT)) {
			LValue = LValue();
			match(ASSIGN);
			Expr = expr();
			return new AssignmentStatement(firstToken, LValue, Expr);
		}
		else if (isKind(RES_write)) {
			match(RES_write);
			Expr = expr();
			return new WriteStatement(firstToken, Expr);
		}
		else if (isKind(RES_do)) {
			List<GuardedBlock> guardedBlocks = new ArrayList<GuardedBlock>();
			match(RES_do);
			GuardedBlock guardedBlock = GuardedBlock();
			guardedBlocks.add(guardedBlock);
			while(isKind(BOX)) {
				match(BOX);
				GuardedBlock GuardedBlock = GuardedBlock();
				guardedBlocks.add(GuardedBlock);
			}
			match(RES_od);
			return new DoStatement(firstToken, guardedBlocks);
		}
		else if (isKind(RES_if)){
			List<GuardedBlock> guardedBlocks = new ArrayList<GuardedBlock>();
			match(RES_if);
			GuardedBlock guardedBlock = GuardedBlock();
			guardedBlocks.add(guardedBlock);
			while(isKind(BOX)) {
				match(BOX);
				GuardedBlock GuardedBlock = GuardedBlock();
				guardedBlocks.add(GuardedBlock);
			}
			match(RES_fi);
			return new IfStatement(firstToken, guardedBlocks);
		}
		else if (isKind(RETURN)){
			match(RETURN);
			Expr = expr();
			return new ReturnStatement(firstToken, Expr);
		}
		else if (isKind(BLOCK_OPEN)) {
			Block block = BlockStatement();
			return new StatementBlock(firstToken, block);
		}
		throw new SyntaxException("Statement not implemented correctly!");
	}

	private GuardedBlock GuardedBlock() throws PLCCompilerException{
		IToken firstToken = t;
		Expr guard = null;
		guard = expr();
		match(RARROW);
		Block Block = null;
		Block = Block();
		return new GuardedBlock(firstToken, guard, Block);
	}

	private Block BlockStatement() throws PLCCompilerException {
		return Block();
	}
}
//hello