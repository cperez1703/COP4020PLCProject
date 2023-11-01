package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

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
        if (binaryExpr != null) {
            Expr left = binaryExpr.getLeftExpr();
            IToken op = binaryExpr.getOp();
            Expr right = binaryExpr.getRightExpr();
            Type inferBinaryType = null;
            if (op.kind() == Kind.EQ) {
                if (right.getType() == left.getType())
                    inferBinaryType = Type.BOOLEAN;
            } else if (op.kind() == Kind.PLUS) {
                if (right.getType() == left.getType()) {
                    inferBinaryType = left.getType();
                }

            } else if (op.kind() == Kind.BITOR || op.kind() == Kind.BITAND) {
                if (left.getType() == Type.PIXEL && right.getType() == Type.PIXEL) {
                    inferBinaryType = Type.PIXEL;
                }
            } else if (op.kind() == Kind.OR || op.kind() == Kind.AND) {
                if (left.getType() == Type.BOOLEAN && right.getType() == Type.BOOLEAN) {
                    inferBinaryType = Type.BOOLEAN;
                }
            } else if (op.kind() == Kind.LT || op.kind() == Kind.GT || op.kind() == Kind.LE || op.kind() == Kind.GE) {
                if (right.getType() == Type.INT && left.getType() == Type.INT) {
                    inferBinaryType = Type.BOOLEAN;
                }
            } else if (op.kind() == Kind.EXP) {
                if (left.getType() == Type.INT && right.getType() == Type.INT) {
                    inferBinaryType = Type.INT;
                } else if (left.getType() == Type.PIXEL && right.getType() == Type.INT) {
                    inferBinaryType = Type.PIXEL;
                }
            } else if (op.kind() == Kind.MINUS || op.kind() == Kind.TIMES || op.kind() == Kind.DIV || op.kind() == Kind.MOD) {
                if (op.kind() != Kind.MINUS && (left.getType() == Type.PIXEL || left.getType() == Type.IMAGE) && right.getType() == Type.INT) {
                    inferBinaryType = left.getType();
                } else if ((left.getType() == Type.INT || left.getType() == Type.PIXEL || left.getType() == Type.IMAGE) && right.getType() == left.getType()) {
                    inferBinaryType = left.getType();
                }
            }
            binaryExpr.setType(inferBinaryType);
            return inferBinaryType;
        } else {
            throw new PLCCompilerException();
        }
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
        return channelSelector;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCCompilerException {
        Expr guardExpr = conditionalExpr.getGuardExpr();
        Expr trueExpr = conditionalExpr.getTrueExpr();
        Expr falseExpr = conditionalExpr.getFalseExpr();
        guardExpr.setType(Type.BOOLEAN);
        trueExpr.setType(falseExpr.getType());
        conditionalExpr.setType(trueExpr.getType());
        return conditionalExpr;
        //Maybe??
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
        Expr expr = declaration.getInitializer();
        NameDef nameDef = declaration.getNameDef();
        Type type = null;
        if (expr == null || expr.getType() == nameDef.getType() || (expr.getType() == Type.STRING && nameDef.getType() == Type.IMAGE)) {
            type = nameDef.getType();
        }
        return type;
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
        check(expandedPixelExpr.getRed().getType()==Type.INT, expandedPixelExpr, "Invalid Red");
        check(expandedPixelExpr.getGreen().getType()==Type.INT, expandedPixelExpr, "Invalid Green");
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
        Type varType = lValue.getNameDef().getType();
        Type type = null;
        if(lValue.getPixelSelector()!=null){
            if(varType!=Type.IMAGE)throw new TypeCheckException("Invalid LValue");
        }
        if(lValue.getChannelSelector()!=null){
            if(varType==Type.IMAGE || varType==Type.PIXEL){
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
        Type type = nameDef.getType();
        if (nameDef.getType() !=null) {
            if (nameDef.getDimension() != null) {
                type = Type.IMAGE;
            } else if (type == Type.INT || type == Type.BOOLEAN || type == Type.STRING || type == Type.PIXEL || type == Type.IMAGE) {
                type = nameDef.getType();
            }
            st.insertName(nameDef);
            return type;
            //IP (??)
        }
        else {
            throw new TypeCheckException();
        }
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
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws TypeCheckException {
        if (postfixExpr != null) {
            Type inferPostFixExprType = null;
            Expr exprType = postfixExpr.primary();
            PixelSelector pixelSelector = postfixExpr.pixel();
            ChannelSelector channelSelector = postfixExpr.channel();
            if (pixelSelector == null && channelSelector == null) {
                inferPostFixExprType = exprType.getType();
            }
            else if (exprType.getType() == Type.IMAGE) {
                if (pixelSelector != null && channelSelector == null) {
                    inferPostFixExprType = Type.PIXEL;
                }
                else if (pixelSelector != null && channelSelector != null) {
                    inferPostFixExprType = Type.INT;
                }
                else if (pixelSelector == null && channelSelector != null) {
                    inferPostFixExprType = Type.IMAGE;
                }
            }
            else if (exprType.getType() == Type.PIXEL) {
                if (pixelSelector == null && channelSelector != null) {
                    inferPostFixExprType = Type.INT;
                }
            }
            return inferPostFixExprType;

        }
        else {
            throw new TypeCheckException();
        }
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
        if (unaryExpr != null) {
            if (ExprType == Type.BOOLEAN && op == Kind.BANG) {
                inferUnaryExprType = Type.BOOLEAN;
            } else if (ExprType == Type.INT && op == Kind.MINUS) {
                inferUnaryExprType = Type.INT;
            } else if (ExprType == Type.IMAGE && (op == Kind.RES_width || op == Kind.RES_height)) {
                inferUnaryExprType = Type.INT;
            }
            return inferUnaryExprType;
        } else {
            return new PLCCompilerException();
        }
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
        if(Objects.equals(constExpr.getName(), "Z"))type = Type.INT;
        else type = Type.PIXEL;
        return type;
    }
}

