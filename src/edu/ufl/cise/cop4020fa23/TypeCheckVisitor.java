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
        lvalue.visit(this,arg);
        Expr expr = assignmentStatement.getE();
        expr.visit(this,arg);
        boolean assignmentCompatible = false;
        if(expr.getType() == lvalue.getType()){
            assignmentCompatible = true;
        }
        else if(lvalue.getType() == Type.PIXEL && expr.getType() == Type.INT){
            assignmentCompatible = true;
        }
        else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.PIXEL){
            assignmentCompatible = true;
        }
        else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.INT){
            assignmentCompatible = true;
        }
        else if(lvalue.getType() == Type.IMAGE && expr.getType()==Type.STRING){
            assignmentCompatible = true;
        }
        check(assignmentCompatible == true, assignmentStatement, "not assignment compatible");
        st.exitScope();
        return assignmentStatement;
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
//        Type type = (Type) guardedBlock.getBlock().visit(this,arg);
        Expr expr = guardedBlock.getGuard();
        expr.visit(this,arg);
        Block gBlock = guardedBlock.getBlock();
        gBlock.visit(this,arg);
        Type type = expr.getType();
        check(type == Type.BOOLEAN, guardedBlock, "guarded block must be bool");
        return type;
        //Done
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
        check(st.lookup(identExpr.getName())!=null, identExpr, "Ident Expr doesn't exist");
        identExpr.setNameDef(st.lookup(identExpr.getName()));
        identExpr.setType(identExpr.getNameDef().getType());
        Type type = identExpr.getType();
        return type;
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
        NameDef lvalNameDef = st.lookup(lValue.getName());
        Type varType = lvalNameDef.getType();
        Type inferLValueType = null;
        if(lValue.getPixelSelector()!=null){
            lValue.getPixelSelector().visit(this,true);
//            check(lValue.getVarType() == Type.IMAGE, lValue,"lvalue not image when pixelselector not null");
            varType = Type.IMAGE;
        }
        if(lValue.getChannelSelector()!=null){
//            lValue.getChannelSelector().visit(this,arg);
//            check(lValue.getVarType() == Type.PIXEL || lValue.getVarType()==Type.IMAGE, lValue, "invalid lvalue when channel selector not null");
            if(varType==Type.IMAGE){
                varType = Type.IMAGE;
            }
            else if (varType == Type.PIXEL) {
                varType = Type.PIXEL;
            }else{
                throw new TypeCheckException("Invalid LValue");
            }
        }
        if(lValue.getPixelSelector()==null && lValue.getChannelSelector()==null){
            inferLValueType = varType;
        }
        else if(varType == Type.IMAGE){
            if(lValue.getPixelSelector()!=null && lValue.getChannelSelector()==null){
                lValue.getPixelSelector().visit(this,true);
                inferLValueType = Type.PIXEL;
            }
            else if(lValue.getPixelSelector()!=null && lValue.getChannelSelector()!=null){
                lValue.getPixelSelector().visit(this,true);
                lValue.getChannelSelector().visit(this,arg);
                inferLValueType = Type.INT;
            }
            else if(lValue.getPixelSelector()==null && lValue.getChannelSelector()!=null){
                lValue.getChannelSelector().visit(this,arg);
                inferLValueType = Type.IMAGE;
            }
        }
        else if (varType == Type.PIXEL && lValue.getPixelSelector()==null && lValue.getChannelSelector()!=null){
            lValue.getChannelSelector().visit(this,arg);
            inferLValueType = Type.INT;
        }
        check(inferLValueType != null, lValue, "inferLValue not defined");
        lValue.setType(inferLValueType);
        lValue.setNameDef(lvalNameDef);
        return inferLValueType;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCCompilerException {
        Type type = null;
        Type nameDefType = nameDef.getType();
        Dimension nameDefDimension = nameDef.getDimension();
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
        if(arg.equals(true)) {
            check(xExpr instanceof IdentExpr || xExpr instanceof NumLitExpr, pixelSelector, "not ident or numlit");
            check(yExpr instanceof IdentExpr || yExpr instanceof NumLitExpr, pixelSelector, "not ident or numlit");
            if (xExpr instanceof IdentExpr && st.lookup(((IdentExpr)xExpr).getName()) == null) {
                st.insertName(new SyntheticNameDef(((IdentExpr)xExpr).getName()));
            }
            if (yExpr instanceof IdentExpr && st.lookup(((IdentExpr)yExpr).getName()) == null) {
                st.insertName(new SyntheticNameDef(((IdentExpr)yExpr).getName()));
            }
        }
        xExpr.visit(this,arg);
        yExpr.visit(this,arg);
        Type xType = xExpr.getType();
        Type yType = yExpr.getType();
        xExpr.setType(xType);
        yExpr.setType(yType);
        check(xType == Type.INT, pixelSelector, "Pixel X not an int");
        check(yType == Type.INT, pixelSelector, "Pixel Y not an int");
        return pixelSelector;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws PLCCompilerException {
        Type inferPostFixExprType = null;
        Expr expr = postfixExpr.primary();
        expr.visit(this,arg);
        PixelSelector pixelSelector = postfixExpr.pixel();
        ChannelSelector channelSelector = postfixExpr.channel();

        check(true, postfixExpr, "postfix expr undefined");
        if (pixelSelector == null && channelSelector == null) {
            inferPostFixExprType = expr.getType();
        }
        else if (expr.getType() == Type.IMAGE && (pixelSelector != null && channelSelector == null)) {
            pixelSelector.visit(this,true);
            inferPostFixExprType = Type.PIXEL;
        }
        else if (expr.getType() == Type.IMAGE && (pixelSelector != null && channelSelector != null)) {
            pixelSelector.visit(this, true);
            channelSelector.visit(this,arg);
            inferPostFixExprType = Type.INT;
        }
        else if (expr.getType() == Type.IMAGE && (pixelSelector == null && channelSelector != null)) {
            channelSelector.visit(this,arg);
            inferPostFixExprType = Type.IMAGE;
        }
        else if (expr.getType() == Type.PIXEL && (pixelSelector == null && channelSelector != null)){
            channelSelector.visit(this,arg);
            inferPostFixExprType = Type.INT;
        }
        postfixExpr.setType(inferPostFixExprType);
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
        Expr returnExpr = returnStatement.getE();
        returnExpr.visit(this, arg);
        Type exprType = returnExpr.getType();
        check(exprType==root.getType(),returnStatement,"Invalid Return Statement Type");
        return exprType;
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
        Kind op = unaryExpr.getOp();
        unaryExpr.getExpr().visit(this,arg);
        Type ExprType = unaryExpr.getExpr().getType();
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

