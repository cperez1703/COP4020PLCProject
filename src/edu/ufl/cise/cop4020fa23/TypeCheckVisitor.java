package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;
import edu.ufl.cise.cop4020fa23.Token;

import java.util.List;
import java.util.Objects;

public class TypeCheckVisitor implements ASTVisitor {
    SymbolTable st = new SymbolTable();
    Program root;

    public boolean check(boolean bool, AST node, String message) throws TypeCheckException {
        if (bool == false) {
            throw new TypeCheckException(node.firstToken.sourceLocation(), message);
        } else {
            return true;
        }
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        st.enterScope();
        LValue lvalue = assignmentStatement.getlValue();
        Expr expr = assignmentStatement.getE();
        lvalue.visit(this,arg);
        expr.visit(this,arg);
        if(expr.getType() == lvalue.getType()){
            st.exitScope();
            return assignmentStatement;
        }else if(lvalue.getType() == Type.PIXEL && expr.getType() == Type.INT){
            st.exitScope();
            return assignmentStatement;
        }else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.PIXEL){
            st.exitScope();
            return assignmentStatement;
        }else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.INT){
            st.exitScope();
            return assignmentStatement;
        }else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.STRING){
            st.exitScope();
            return assignmentStatement;
        }
        throw new TypeCheckException("Invalid Assignment Statement");
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
        Type inferBinaryType = null;
        binaryExpr.getLeftExpr().visit(this,arg);
        binaryExpr.getRightExpr().visit(this,arg);
        Type left = binaryExpr.getLeftExpr().getType();
        Kind op = binaryExpr.getOp().kind();
        Type right = binaryExpr.getRightExpr().getType();

        if (op == Kind.BITOR || op == Kind.BITAND) {
            if (left == Type.PIXEL && right == Type.PIXEL) {
                inferBinaryType = Type.PIXEL;
            }
        }
        else if (op == Kind.AND || op == Kind.OR) {
            if (left == Type.BOOLEAN && right == Type.BOOLEAN) {
                inferBinaryType = Type.BOOLEAN;
            }
        }
        else if (op == Kind.LT || op == Kind.GT || op == Kind.LE || op == Kind.GE) {
            if (right == Type.INT && left == Type.INT) {
                inferBinaryType = Type.BOOLEAN;
            }
        }
        else if (op == Kind.EQ && right == left) {
            inferBinaryType = Type.BOOLEAN;
        }
        else if (op == Kind.EXP) {
            if (left == Type.INT && right == Type.INT) {
                inferBinaryType = Type.INT;
            }
            else if (left == Type.PIXEL && right == Type.INT) {
                inferBinaryType = Type.PIXEL;
            }
        }
        else if (op == Kind.PLUS) {
            if (right == left) {
                inferBinaryType = left;
            }
        }
        else if (op == Kind.MINUS || op == Kind.TIMES || op == Kind.DIV || op == Kind.MOD) {
            if ((left == Type.INT || left == Type.PIXEL || left == Type.IMAGE) && right == left) {
                inferBinaryType = left;
            }
            else if (op != Kind.MINUS && (left == Type.PIXEL || left == Type.IMAGE) && right == Type.INT) {
                inferBinaryType = left;
            }
        }
        else {
            check(false, binaryExpr, "invalid binary expr");
        }
        binaryExpr.setType(inferBinaryType);
        return inferBinaryType;
        //Done
    }
    @Override
    public Object visitBlock(Block block, Object arg) throws PLCCompilerException {
        st.enterScope();
        List<Block.BlockElem> blockElems = block.getElems();
        for (Block.BlockElem elem : blockElems) {
            elem.visit(this, arg);
        }
        st.exitScope();
        return block;
        //Done
    }

    @Override
    public Object visitBlockStatement(StatementBlock statementBlock, Object arg) throws PLCCompilerException {
        statementBlock.getBlock().visit(this,arg);
        return statementBlock;
        //Done
    }

    @Override
    public Object visitChannelSelector(ChannelSelector channelSelector, Object arg) throws PLCCompilerException {
        channelSelector.visit(this, arg);
        return channelSelector.color();
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCCompilerException {
        Expr guardExpr = conditionalExpr.getGuardExpr();
        Expr trueExpr = conditionalExpr.getTrueExpr();
        Expr falseExpr = conditionalExpr.getFalseExpr();

        guardExpr.visit(this, arg);
        Type guardType = guardExpr.getType();
        check(guardType == Type.BOOLEAN, conditionalExpr, "guard expression is not a boolean");

        trueExpr.visit(this, arg);
        Type trueType = trueExpr.getType();
        falseExpr.visit(this, arg);
        Type falseType = falseExpr.getType();
        check(trueType == falseType, conditionalExpr, "true expr must match false expr");

        conditionalExpr.setType(trueType);
        return trueType;
        //Done
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
        Expr expr = declaration.getInitializer();
        NameDef nameDef = declaration.getNameDef();
        Type nameDefType = nameDef.getType();
        if (expr != null) {
            expr.visit(this, arg);
        }
        nameDef.visit(this, arg);
        check(expr == null ||
                expr.getType() == nameDef.getType() ||
                (expr.getType() == Type.STRING && nameDef.getType() == Type.IMAGE), declaration, "expression issue TypeCheck");
        return nameDefType;
        //IP??
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCCompilerException {
        Type typeW = (Type) dimension.getWidth().visit(this, arg);
        check(typeW == Type.INT, dimension, "image width must be int");
        Type typeH = (Type) dimension.getHeight().visit(this, arg);
        check(typeH == Type.INT, dimension, "image height must be int");
        return dimension;
        //Done
    }

    @Override
    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
        List<GuardedBlock> gBlockElems = doStatement.getGuardedBlocks();
        for(GuardedBlock elem:gBlockElems){
            elem.visit(this,arg);
        }
        return doStatement;
        //Done
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCCompilerException {
        expandedPixelExpr.getRed().visit(this,arg);
        check(expandedPixelExpr.getRed().getType()==Type.INT, expandedPixelExpr, "Invalid Red");
        expandedPixelExpr.getGreen().visit(this,arg);
        check(expandedPixelExpr.getGreen().getType()==Type.INT, expandedPixelExpr, "Invalid Green");
        expandedPixelExpr.getBlue().visit(this,arg);
        check(expandedPixelExpr.getBlue().getType()==Type.INT, expandedPixelExpr, "Invalid Blue");
        expandedPixelExpr.setType(Type.PIXEL);
        return expandedPixelExpr;
    }

    @Override
    public Object visitGuardedBlock(GuardedBlock guardedBlock, Object arg) throws PLCCompilerException {
        Type type = (Type) guardedBlock.getBlock().visit(this,arg);
        check(type == Type.BOOLEAN, guardedBlock, "guarded block must be bool");
        return type;
        //Done
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
        check(st.lookup(identExpr.getName())!=null, identExpr, "Ident Expr doesn't exist");
        identExpr.setNameDef(st.lookup(identExpr.getName()));
        identExpr.setType(identExpr.getNameDef().getType());
        return identExpr;
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws PLCCompilerException {
        List<GuardedBlock> gBlockElems = ifStatement.getGuardedBlocks();
        for(GuardedBlock elem:gBlockElems){
            elem.visit(this,arg);
        }
        return ifStatement;
        //Done
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCCompilerException {
        NameDef nameDef = st.lookup(lValue.getName());
        lValue.getNameDef().visit(this,arg);
        Type varType = lValue.getNameDef().getType();
        Type type = null;
        lValue.getPixelSelector().visit(this,arg);
        if(lValue.getPixelSelector()!=null){
            if(varType!=Type.IMAGE)throw new TypeCheckException("Invalid LValue");
        }
        if(lValue.getChannelSelector()!=null){
            if(varType==Type.IMAGE || varType==Type.PIXEL){
                varType = lValue.getVarType();
            }else{
                throw new TypeCheckException("Invalid LValue");
            }
        }
        if(lValue.getPixelSelector()==null && lValue.getChannelSelector()==null){
            type = varType;
        }else if(varType == Type.IMAGE){
            if(lValue.getPixelSelector()!=null && lValue.getChannelSelector()==null){
                type = Type.PIXEL;
            }else if(lValue.getPixelSelector()!=null && lValue.getChannelSelector()!=null){
                type = Type.INT;
            }else if(lValue.getPixelSelector()==null && lValue.getChannelSelector()!=null){
                type = Type.IMAGE;
            }
        }else if (varType == Type.PIXEL && lValue.getPixelSelector()==null && lValue.getChannelSelector()!=null){
            type = Type.INT;
        }
        lValue.setType(type);
        return lValue;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCCompilerException {
        Type type = null;
        Type nameDefType = nameDef.getType();
        Dimension nameDefDimension = nameDef.getDimension();
        String nameDefName = nameDef.getName();
        check(nameDefType != Type.VOID, nameDef, "Invalid namedef definition");
        if (nameDefDimension != null) {
            nameDefDimension.visit(this, arg);
            type = Type.IMAGE;
        }
        else {
            type = nameDefType;
        }
        st.insertName(nameDef);
        return type;
        //IP (??)
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCCompilerException {
        Type type = Type.INT;
        numLitExpr.setType(type);
        return type;
        //Done
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCCompilerException {
        Expr xExpr = pixelSelector.xExpr();
        Expr yExpr = pixelSelector.yExpr();
        Type xType = xExpr.getType();
        Type yType = yExpr.getType();

        if(arg instanceof LValue) {
            check(xExpr instanceof IdentExpr || xExpr instanceof NumLitExpr, pixelSelector, "not ident or numlit");
            check(yExpr instanceof IdentExpr || yExpr instanceof NumLitExpr, pixelSelector, "not ident or numlit");
            if (xExpr instanceof IdentExpr && st.lookup(((IdentExpr)xExpr).getName()) == null) {
                st.insertName(new SyntheticNameDef(((IdentExpr)xExpr).getName()));
            }
            if (yExpr instanceof IdentExpr && st.lookup(((IdentExpr)yExpr).getName()) == null) {
                st.insertName(new SyntheticNameDef(((IdentExpr) yExpr).getName()));
            }
        }
        check(xType == Type.INT, pixelSelector, "Pixel X not an int");
        check(yType == Type.INT, pixelSelector, "Pixel Y not an int");
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws TypeCheckException {
        Type inferPostFixExprType = null;
        Expr exprType = postfixExpr.primary();
        PixelSelector pixelSelector = postfixExpr.pixel();
        ChannelSelector channelSelector = postfixExpr.channel();

        check(false, postfixExpr, "postfix expr undefined");
        if (pixelSelector == null && channelSelector == null) {
            inferPostFixExprType = exprType.getType();
        }
        else if (exprType.getType() == Type.IMAGE && (pixelSelector != null && channelSelector == null)) {
            inferPostFixExprType = Type.PIXEL;
        }
        else if (exprType.getType() == Type.IMAGE && (pixelSelector != null && channelSelector != null)) {
            inferPostFixExprType = Type.INT;
        }
        else if (exprType.getType() == Type.IMAGE && (pixelSelector == null && channelSelector != null)) {
            inferPostFixExprType = Type.IMAGE;
        }
        else if (exprType.getType() == Type.PIXEL && (pixelSelector == null && channelSelector != null)){
            inferPostFixExprType = Type.INT;
        }
        exprType.setType(inferPostFixExprType);
        return inferPostFixExprType;
    }

    @Override
    public Object visitProgram (Program program, Object arg) throws PLCCompilerException {
        root = program;
        Type type = Type.kind2type(program.getTypeToken().kind());
        program.setType(type);
        st.enterScope();
        List<NameDef> params = program.getParams();
        for (NameDef param : params) {
            param.visit(this, arg);
        }
        program.getBlock().visit(this, arg);
        st.exitScope();
        return type;
        //Done
    }

    @Override
    public Object visitReturnStatement (ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
        Type type = (Type) returnStatement.getE().visit(this,arg);
        check(type==root.getType(),returnStatement,"Invalid Return Statement Type");
        return type;
        //Done?
    }

    @Override
    public Object visitStringLitExpr (StringLitExpr stringLitExpr, Object arg) throws PLCCompilerException {
        Type type = Type.STRING;
        stringLitExpr.setType(type);
        return type;
        //Done
    }

    @Override
    public Object visitUnaryExpr (UnaryExpr unaryExpr, Object arg) throws PLCCompilerException {
        Type inferUnaryExprType = null;
        Type ExprType = unaryExpr.getType();
        Kind op = unaryExpr.getOp();
        unaryExpr.getExpr().visit(this,arg);
        if (ExprType == Type.BOOLEAN && op == Kind.BANG) {
            inferUnaryExprType = Type.BOOLEAN;
        } else if (ExprType == Type.INT && op == Kind.MINUS) {
            inferUnaryExprType = Type.INT;
        } else if (ExprType == Type.IMAGE && (op == Kind.RES_width || op == Kind.RES_height)) {
            inferUnaryExprType = Type.INT;
        }
        else {
            check(false, unaryExpr, "infer unary expr undefined");
        }
        unaryExpr.setType(inferUnaryExprType);
        return inferUnaryExprType;
    }

    @Override
    public Object visitWriteStatement (WriteStatement writeStatement, Object arg) throws PLCCompilerException {
        writeStatement.getExpr().visit(this, arg);
        return writeStatement;
        //Done
    }

    @Override
    public Object visitBooleanLitExpr (BooleanLitExpr booleanLitExpr, Object arg) throws PLCCompilerException {
        Type type = Type.BOOLEAN;
        booleanLitExpr.setType(type);
        return type;
        //Done
    }

    @Override
    public Object visitConstExpr (ConstExpr constExpr, Object arg) throws PLCCompilerException {
        Type type = null;
        if(Objects.equals(constExpr.getName(), "Z")) {
            type = Type.INT;
        }
        else {
            type = Type.PIXEL;
        }
        constExpr.setType(type);
        return type;
    }
}

