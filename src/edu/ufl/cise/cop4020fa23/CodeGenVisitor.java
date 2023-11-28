package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;
import jdk.swing.interop.LightweightContentWrapper;

import java.util.List;

public class CodeGenVisitor implements ASTVisitor{
    String packageName;
    int n=0;
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append(assignmentStatement.getlValue().visit(this,arg));
        sb.append("=");
        sb.append(assignmentStatement.getE().visit(this,arg));
        return sb;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Expr left = binaryExpr.getLeftExpr();
        Expr right = binaryExpr.getRightExpr();
        Kind op = binaryExpr.getOpKind();
        if(left.getType()==Type.STRING &&op == Kind.EQ){
            sb.append(left.visit(this,arg).toString());
            sb.append(".equals(");
            sb.append(right.visit(this,arg).toString());
            sb.append(")");
        }else if (op==Kind.EXP){
            sb.append("((int)Math.round(Math.pow(");
            sb.append(left.visit(this,arg).toString());
            sb.append(",");
            sb.append(right.visit(this,arg).toString());
            sb.append(")))");
        }else{
            sb.append("(");
            sb.append(left.visit(this,arg).toString());
            sb.append(" ");
            sb.append(binaryExpr.getOp().text());
            sb.append(" ");
            sb.append(right.visit(this,arg).toString());
            sb.append(")");
        }
        return sb;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCCompilerException {
        StringBuilder sb;
        sb = new StringBuilder();
        List<Block.BlockElem> elems = block.getElems();
        sb.append("{\n");
        for(int i = 0; i < elems.size();i++){
            Block.BlockElem elem = elems.get(i);
            sb.append(elem.visit(this,arg));
            sb.append(";");
            sb.append("\n");
        }
        sb.append("}\n");
        return (arg != null ? sb.toString() : null);
    }

    @Override
    public Object visitBlockStatement(StatementBlock statementBlock, Object arg) throws PLCCompilerException {
        return new StringBuilder(statementBlock.getBlock().visit(this,arg).toString());
    }

    @Override
    public Object visitChannelSelector(ChannelSelector channelSelector, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(conditionalExpr.getGuardExpr().visit(this,arg));
        sb.append("?");
        sb.append(conditionalExpr.getTrueExpr().visit(this,arg));
        sb.append(":");
        sb.append(conditionalExpr.getFalseExpr().visit(this,arg));
        sb.append(")");
        return sb;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append(declaration.getNameDef().visit(this,arg));
        if(declaration.getInitializer()!=null){
            sb.append("=");
            sb.append(declaration.getInitializer().visit(this,arg));
        }
        return sb;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitGuardedBlock(GuardedBlock guardedBlock, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCCompilerException {
        return new StringBuilder(identExpr.getNameDef().getJavaName());
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCCompilerException {
        return new StringBuilder(lValue.getNameDef().getJavaName());//change
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCCompilerException {
        StringBuilder sb;
        sb = new StringBuilder();
        String type = nameDef.getType().toString().toLowerCase();
        if(type.equals("string"))type="String";
        sb.append(type);
        sb.append(" ");
        sb.append(nameDef.getName());//need to change to account for multiples(n is number)
        sb.append("$");
        sb.append(n);
        StringBuilder javaName = new StringBuilder();
        javaName.append(nameDef.getName());
        javaName.append("$");
        javaName.append(n);
        nameDef.setJavaName(javaName.toString());
        n++;
        return (arg != null ? sb.toString() : null);
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCCompilerException {
        return new StringBuilder(numLitExpr.getText());
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws PLCCompilerException {
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder sb;
//        if(arg!=null)sb= (StringBuilder) arg;
//        else sb = new StringBuilder();
        sb = new StringBuilder();
        sb.append("package edu.ufl.cise.cop4020fa23;\n");
        sb.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n");
        sb.append("public class ");
        sb.append(program.getName());
        sb.append("{\n");
        sb.append("public static ");
        String type = program.getType().toString().toLowerCase();
        if(type.equals("string"))type="String";
        sb.append(type);
        sb.append(" apply(");
        List<NameDef> params = program.getParams();
        for(int i = 0; i < params.size(); i++){
            NameDef param = params.get(i);
            sb.append(param.visit(this,arg));
            if(i<params.size()-1)sb.append(", ");
        }
        sb.append(")\n");
        sb.append(visitBlock(program.getBlock(), arg));
        sb.append("}\n");
        return (arg != null ? sb.toString() : null);
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCCompilerException {
        StringBuilder stringBuilder;
//        if (arg != null) {
//            stringBuilder = (StringBuilder) arg;
//        }
//        else {
//            stringBuilder = new StringBuilder();
//        }
        stringBuilder = new StringBuilder();
        Expr expr = returnStatement.getE();
        stringBuilder.append("return ");
        stringBuilder.append(expr.visit(this,arg));
        return (arg != null ? stringBuilder.toString() : null);
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCCompilerException {
        return new StringBuilder(stringLitExpr.getText());
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        Kind op = unaryExpr.getOp();
        if(op==Kind.BANG)sb.append("!");
        if(op==Kind.MINUS)sb.append("-");
        sb.append(unaryExpr.getExpr().visit(this,arg));
        sb.append(")");
        return sb;
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append("ConsoleIO.write(");
        sb.append(writeStatement.getExpr().visit(this,arg));
        sb.append(")");
        return sb;
    }

    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws PLCCompilerException {
        return new StringBuilder(booleanLitExpr.getText().toLowerCase());
    }

    @Override
    public Object visitConstExpr(ConstExpr constExpr, Object arg) throws PLCCompilerException {
        return null;
    }

}
