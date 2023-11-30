package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.*;
import edu.ufl.cise.cop4020fa23.ast.Dimension;
import edu.ufl.cise.cop4020fa23.exceptions.CodeGenException;
import edu.ufl.cise.cop4020fa23.exceptions.PLCCompilerException;
import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;
import edu.ufl.cise.cop4020fa23.runtime.FileURLIO;
import edu.ufl.cise.cop4020fa23.runtime.ImageOps;
import edu.ufl.cise.cop4020fa23.runtime.PixelOps;
import jdk.swing.interop.LightweightContentWrapper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import static edu.ufl.cise.cop4020fa23.Kind.*;

public class CodeGenVisitor implements ASTVisitor{
    String packageName;
    int n=0;
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        LValue lvalue = assignmentStatement.getlValue();
        Expr expr = assignmentStatement.getE();
        // !! IP (???)
        if (lvalue.getVarType() == Type.IMAGE) {
            if (lvalue.getPixelSelector() == null && lvalue.getChannelSelector() == null) {
                if (expr.getType() == Type.IMAGE) {
                    sb.append("ImageOps.copyInto(");
                    sb.append(expr.visit(this,arg));
                    sb.append(",");
                    sb.append(lvalue.visit(this,arg));
                    sb.append(")");
//                    ImageOps.copyInto(source,lvalue); //need to get bufferedImages as input
                }
                else if (lvalue.getType() == Type.PIXEL ) {
//                    ImageOps.setAllPixels();
                }
                else if (lvalue.getType() == Type.STRING) {
                    sb.append(lvalue.visit(this,arg));
                    sb.append("FileURLIO.readImage(");
                    sb.append(expr.visit(this,arg));
                    sb.append(")");
//                    FileURLIO.readImage();
//                    ImageOps.copyInto(loadedImage, lvalue);
                }
            }


        }
        //Someone said something in slack about this
        else if (lvalue.getVarType() == Type.PIXEL && lvalue.getChannelSelector() == null && expr.getType() == Type.INT) {
//            lvalue = PixelOps.pack(expr,expr,expr);
            sb.append(lvalue.visit(this,arg).toString());
            sb.append(" = PixelOps.pack(");
            sb.append(expr.visit(this,arg).toString());
            sb.append(",");
            sb.append(expr.visit(this,arg).toString());
            sb.append(",");
            sb.append(expr.visit(this,arg).toString());
            sb.append(")");
        }
        else if (lvalue.getVarType() == Type.PIXEL && lvalue.getChannelSelector()!= null) {
            sb.append(assignmentStatement.getlValue().visit(this,arg).toString());
            sb.append(expr.visit(this,arg).toString());
            sb.append(")");
//            //RED
//            if (lvalue.getChannelSelector().visit(this,arg).toString().equals("RES_red")) {
//                sb.append("PixelOps.setRed(");
//                sb.append(lvalue.visit(this,arg).toString());
//                sb.append(",");
//                sb.append(expr.visit(this,arg).toString());
//                sb.append(")");
//            }
//            //GREEN
//            else if (lvalue.getChannelSelector().color() == Kind.RES_green) {
//                sb.append("PixelOps.setGreen(");
//                sb.append(lvalue.visit(this,arg).toString());
//                sb.append(",");
//                sb.append(expr.visit(this,arg).toString());
//                sb.append(")");
//            }
//            //BLUE
//            else if (assignmentStatement.getlValue().getChannelSelector().color() == Kind.RES_blue) {
//                sb.append("PixelOps.setBlue(");
//                sb.append(lvalue.visit(this,arg).toString());
//                sb.append(",");
//                sb.append(expr.visit(this,arg).toString());
//                sb.append(")");
//            }
        }
        else {
            // !! CHECK: Added .toString() to these, not sure if correct
            sb.append(lvalue.visit(this, arg).toString());
            sb.append(" = ");
            sb.append(expr.visit(this, arg).toString());
        }
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
//            sb.append("(");
//            sb.append(left.visit(this, arg).toString());
//            sb.append(" ");
//            sb.append(binaryExpr.getOp().text());
//            sb.append(" ");
//            sb.append(right.visit(this, arg).toString());
//            sb.append(")");
            if (op == Kind.BOOLEAN_LIT) {

            }
            else if (left.getType() == Type.PIXEL && right.getType() == Type.PIXEL) {
                sb.append("(ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.");
                sb.append(ImageOps.OP.valueOf(op.toString()));
                sb.append(",");
                sb.append(left.visit(this,arg).toString());
                sb.append(",");
                sb.append(right.visit(this,arg).toString());
                sb.append("))");
//                ImageOps.binaryPackedPixelPixelOp(op,left,right);
            }
            else if (left.getType() == Type.PIXEL && right.getType() == Type.INT) {
                sb.append("ImageOps.binaryPackedPixelIntOp(");
            }
            else if (left.getType() == Type.IMAGE || right.getType() == Type.IMAGE) {

            }
            else {
                sb.append("(");
                sb.append(left.visit(this, arg).toString());
                sb.append(" ");
                sb.append(binaryExpr.getOp().text());
                sb.append(" ");
                sb.append(right.visit(this, arg).toString());
                sb.append(")");
            }
        }
//        if (left.getType() == Type.PIXEL && right.getType() == Type.PIXEL) {
//            ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.valueOf(op.toString()), PixelOps.pack(PixelOps.SELECT_RED, PixelOps.SELECT_GREEN, PixelOps.SELECT_BLUE), PixelOps.pack(PixelOps.SELECT_RED, PixelOps.SELECT_GREEN, PixelOps.SELECT_BLUE));
//
//        }
//
//        if (left.getType() == Type.PIXEL && right.getType() == Type.INT) {
//
//        }

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
        return channelSelector.color();
//        StringBuilder sb = new StringBuilder();
//        if (arg instanceof Expr) {
//            if (channelSelector.color() == Kind.RES_red) {
//                sb.append("ImageOps.extractRed(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(")");
//
//            }
//            //GREEN
//            else if (channelSelector.color() == Kind.RES_green) {
//                sb.append("ImageOps.extractGreen(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(")");
//            }
//            //BLUE
//            else if (channelSelector.color() == Kind.RES_blue) {
//                sb.append("ImageOps.extractBlue(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(")");
//            }
//        }
//        else if (arg instanceof LValue) {
//            //RED
//            if (channelSelector.color() == Kind.RES_red) {
//                sb.append("PixelOps.setRed(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(",");
//            }
//            //GREEN
//            else if (channelSelector.color() == Kind.RES_green) {
//                sb.append("PixelOps.setGreen(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(",");
//            }
//            //BLUE
//            else if (channelSelector.color() == Kind.RES_blue) {
//                sb.append("PixelOps.setBlue(");
//                sb.append(channelSelector.visit(this,arg).toString());
//                sb.append(",");
//            }
//        }
//        return sb;
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
        Expr expr = declaration.getInitializer();
        if(expr==null){
            if(declaration.getNameDef().getType()!=Type.IMAGE){
                sb.append(declaration.getNameDef().visit(this,arg));
            }else if (declaration.getNameDef().getType()==Type.IMAGE) {
                if (declaration.getNameDef().getDimension() == null) {
                    System.out.println(declaration.getNameDef());
                    throw new CodeGenException("Dimension null");
                }
                String width = declaration.getNameDef().getDimension().getWidth().toString();
                String height = declaration.getNameDef().getDimension().getHeight().toString();
                int w = Integer.parseInt(width);
                int h = Integer.parseInt(height);
                BufferedImage bufferedImage = ImageOps.makeImage(w, h);
                sb.append("final BufferedImage ");
                sb.append(declaration.getNameDef().visit(this, arg).toString());
                sb.append(" = ");
                sb.append("ImageOps.makeImage(");
                sb.append(declaration.getNameDef().getDimension().toString());
                sb.append(")");
            }
        }
        else if (declaration.getNameDef().getType() != Type.IMAGE) {
            sb.append(declaration.getNameDef().visit(this, arg));
            if (declaration.getInitializer() != null) {
                sb.append(" = ");
                sb.append(declaration.getInitializer().visit(this, arg));
            }
        }
        else if (declaration.getNameDef().getType() == Type.IMAGE) {
            if(declaration.getInitializer().getType() == Type.STRING){
                sb.append(declaration.getInitializer().visit(this,arg).toString());
                if(declaration.getNameDef().getDimension()!=null){

                }else{
                    sb.append("FileURLIO.readImage(");
                    sb.append(declaration.getInitializer().visit(this,arg).toString());
                    sb.append(")");
                }
            }
            else if(declaration.getInitializer().getType()==Type.IMAGE&&declaration.getNameDef()==null){
                sb.append(declaration.getInitializer().visit(this,arg).toString());
            }
            else if(declaration.getInitializer().getType()==Type.IMAGE&&declaration.getNameDef()!=null){
                sb.append(declaration.getInitializer().visit(this,arg).toString());
            }
        }
        return sb;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Expr w = dimension.getWidth();
        Expr h = dimension.getHeight();
        sb.append(w.visit(this,arg).toString());
        sb.append(", ");
        sb.append(h.visit(this,arg).toString());
        return sb;
//        return new StringBuilder (dimension.toString()); //is this a temp>
    }

    @Override
    public Object visitDoStatement(DoStatement doStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        List<GuardedBlock> guardedBlocks = doStatement.getGuardedBlocks();
        sb.append("do{\n");
        sb.append("if(");
        for(int i = 0; i < guardedBlocks.size();i++){
            if(i>0)sb.append("else if(");
            sb.append(guardedBlocks.get(i).getGuard().visit(this,arg));
            sb.append("){\n");
            sb.append(guardedBlocks.get(i).getBlock().visit(this,arg));
            sb.append("}\n");
        }
        sb.append("}");
        sb.append("while(");
        for(int i = 0; i < guardedBlocks.size();i++){
            if(i>0)sb.append("||");
            sb.append("(");
            sb.append(guardedBlocks.get(i).getGuard().visit(this,arg));
            sb.append(")");
        }
        sb.append(")\n");
        return sb;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        sb.append("PixelOps.pack(");
        Expr r = expandedPixelExpr.getRed();
        Expr g = expandedPixelExpr.getGreen();
        Expr b = expandedPixelExpr.getBlue();
        sb.append(r.visit(this,arg).toString());
        sb.append(", ");
        sb.append(g.visit(this,arg).toString());
        sb.append(", ");
        sb.append(b.visit(this,arg).toString());
        sb.append(")");
        return sb;
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
        StringBuilder sb = new StringBuilder();
        List<GuardedBlock> guardedBlocks = ifStatement.getGuardedBlocks();
        for (int i = 0; i < guardedBlocks.size(); i++) {
            Expr g = guardedBlocks.get(i).getGuard();
            Block b = guardedBlocks.get(i).getBlock();
            if (i == 0) {
                sb.append("if");
                sb.append(g.visit(this,arg).toString());
                sb.append(b.visit(this,arg).toString());
            }
            else if (i != 0){
                sb.append("else if(");
                sb.append(g.visit(this,arg).toString());
                sb.append(")");
                sb.append(b.visit(this,arg).toString());
            }
        }
        return sb;
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
        else if (type.equals("image"))type="BufferedImage"; //this down to boolean are questionable, unsure if this is where to implement
        else if(type.equals("pixel"))type="int";
        else if (type.equals("int"))type="int";
        else if (type.equals("void"))type="void";
        else if (type.equals("boolean")) type="Boolean";
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
        StringBuilder sb = new StringBuilder();
        Expr x = pixelSelector.xExpr();
        Expr y = pixelSelector.yExpr();
        sb.append(x.visit(this,arg).toString());
        sb.append(", ");
        sb.append(y.visit(this,arg).toString());
        return sb;
    }

    @Override
    public Object visitPostfixExpr(PostfixExpr postfixExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        if (postfixExpr.primary().getType() == Type.PIXEL) {
            sb.append("PixelOps.");
            if(postfixExpr.channel().visit(this,arg)==RES_red)sb.append("red");
            else if(postfixExpr.channel().visit(this,arg)==RES_green)sb.append("green");
            else if(postfixExpr.channel().visit(this,arg)==RES_blue)sb.append("blue");
            sb.append("(");
            sb.append(postfixExpr.primary().visit(this,arg).toString());
            sb.append(")");
        }
        else if (postfixExpr.primary().getType() == Type.IMAGE) {
            if (postfixExpr.pixel() != null && postfixExpr.channel() == null) {
                sb.append("ImageOps.getRGB(");
                sb.append(postfixExpr.primary().visit(this,arg).toString());
                sb.append(",");
                sb.append(postfixExpr.channel().visit(this,arg).toString());
                sb.append(")");
            }
            else if (postfixExpr.pixel() != null && postfixExpr.channel() != null) {
                sb.append(postfixExpr.channel().visit(this,arg));
                sb.append("(ImageOps.getRGB(");
                sb.append(postfixExpr.primary().visit(this,arg).toString());
                sb.append(",");
                sb.append(postfixExpr.pixel().visit(this,arg).toString());
                sb.append("))");
            }
            else if (postfixExpr.pixel() == null && postfixExpr.channel() != null) {
                postfixExpr.channel().visit(this,arg);
                //RED
                if (postfixExpr.channel().color() == Kind.RES_red) {
                    sb.append("ImageOps.extractRed(");
                    sb.append(postfixExpr.primary().visit(this,arg).toString());
                    sb.append(")");

                }
                //GREEN
                else if (postfixExpr.channel().color() == Kind.RES_green) {
                    sb.append("ImageOps.extractGreen(");
                    sb.append(postfixExpr.primary().visit(this,arg).toString());
                    sb.append(")");
                }
                //BLUE
                else if (postfixExpr.channel().color() == Kind.RES_blue) {
                    sb.append("ImageOps.extractBlue(");
                    sb.append(postfixExpr.primary().visit(this,arg).toString());
                    sb.append(")");
                }
            }
        }
        return sb;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCCompilerException {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append("package edu.ufl.cise.cop4020fa23;\n");
        sb.append("import edu.ufl.cise.cop4020fa23.runtime.ConsoleIO;\n");
        sb.append("import edu.ufl.cise.cop4020fa23.runtime.ImageOps;\n");
        sb.append("import edu.ufl.cise.cop4020fa23.runtime.PixelOps;\n");
        sb.append("public class ");
        sb.append(program.getName());
        sb.append("{\n");
        sb.append("public static ");
        String type = program.getType().toString().toLowerCase();
        if(type.equals("string"))type="String";
        else if (type.equals("image"))type="BufferedImage"; //this down to boolean are questionable, unsure if this is where to implement
        else if(type.equals("pixel"))type="int";
        else if (type.equals("int"))type="int";
        else if (type.equals("void"))type="void";
        else if (type.equals("boolean")) type="Boolean";
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
        if(op == Kind.RES_height) {
            sb.append(".getHeight()");
        }
        if(op == Kind.RES_width) {
            sb.append(".getWidth()");
        }
        sb.append(")");
        return sb;
    }

    @Override
    public Object visitWriteStatement(WriteStatement writeStatement, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        Type exprType = writeStatement.getExpr().getType();
        if (exprType == Type.PIXEL) {
            sb.append("ConsoleIO.writePixel(");
            sb.append(writeStatement.getExpr().visit(this,arg).toString());
            sb.append(")");
        }
        else {
            sb.append("ConsoleIO.write(");
            sb.append(writeStatement.getExpr().visit(this, arg).toString());
            sb.append(")");
        }
        return sb;
    }

    @Override
    public Object visitBooleanLitExpr(BooleanLitExpr booleanLitExpr, Object arg) throws PLCCompilerException {
        return new StringBuilder(booleanLitExpr.getText().toLowerCase());
    }

    @Override
    public Object visitConstExpr(ConstExpr constExpr, Object arg) throws PLCCompilerException {
        StringBuilder sb = new StringBuilder();
        if (constExpr.getName().equals("Z")) {
            sb.append("255");
        }
        else {
            sb.append("0x");
            String color = constExpr.getName();
            switch (color){
                case "RED"-> {
                    String hexstring = Integer.toHexString(Color.RED.getRGB());
                    sb.append(hexstring);
                }
                case "BLUE"-> {
                    String hexstring = Integer.toHexString(Color.BLUE.getRGB());
                    sb.append(hexstring);
                }
                case "GREEN"-> {
                    String hexstring = Integer.toHexString(Color.GREEN.getRGB());
                    sb.append(hexstring);
                }
                case "GRAY"-> {
                    String hexstring = Integer.toHexString(Color.GRAY.getRGB());
                    sb.append(hexstring);
                }
                case "DARK_GRAY"-> {
                    String hexstring = Integer.toHexString(Color.DARK_GRAY.getRGB());
                    sb.append(hexstring);
                }
                case "LIGHT_GRAY"-> {
                    String hexstring = Integer.toHexString(Color.LIGHT_GRAY.getRGB());
                    sb.append(hexstring);
                }
                case "CYAN"-> {
                    String hexstring = Integer.toHexString(Color.CYAN.getRGB());
                    sb.append(hexstring);
                }
                case "PINK"-> {
                    String hexstring = Integer.toHexString(Color.PINK.getRGB());
                    sb.append(hexstring);
                }
                case "YELLOW"-> {
                    String hexstring = Integer.toHexString(Color.YELLOW.getRGB());
                    sb.append(hexstring);
                }
                case "MAGENTA"-> {
                    String hexstring = Integer.toHexString(Color.MAGENTA.getRGB());
                    sb.append(hexstring);
                }
                case "ORANGE"-> {
                    String hexstring = Integer.toHexString(Color.ORANGE.getRGB());
                    sb.append(hexstring);
                }
                case "BLACK"-> {
                    String hexstring = Integer.toHexString(Color.BLACK.getRGB());
                    sb.append(hexstring);
                }
                case "WHITE"-> {
                    String hexstring = Integer.toHexString(Color.WHITE.getRGB());
                    sb.append(hexstring);
                }
            }
        }
        return sb;
    }

}
