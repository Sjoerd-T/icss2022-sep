package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator
{

    // GE01: Generate CSS from AST
    public String generate(AST ast)
    {
        return generateStylesheet(ast.root);
    }

    private String generateStylesheet(ASTNode node)
    {
        StringBuilder output = new StringBuilder();

        for (ASTNode child : node.getChildren())
        {
            if (child instanceof Stylerule)
            {
                output.append(generateStylerule(child));
            }
        }

        return output.toString();
    }

    private String generateStylerule(ASTNode node)
    {
        Stylerule stylerule = (Stylerule) node;
        StringBuilder output = new StringBuilder();

        // Add all selectors, seperated by a comma and an enter
        for (Selector selector : stylerule.selectors)
        {
            if (output.length() > 0)
            {
                output.append(",\n");
            }
            output.append(selector.toString());
        }

        output.append(" {\n");

        for (ASTNode child : stylerule.body)
        {
            if (child instanceof Declaration)
            {
                output.append(generateDeclaration(child));
            }
        }

        output.append("}\n\n");
        return output.toString();
    }

    private String generateDeclaration(ASTNode node)
    {
        Declaration declaration = (Declaration) node;
        StringBuilder output = new StringBuilder();

        // GE02: Add 2 spaces per scope level.
        output.append(String.format("  %s: ", declaration.property.name));

        if (declaration.expression instanceof ColorLiteral)
        {
            output.append(((ColorLiteral) declaration.expression).value + ";\n");
        }
        else if (declaration.expression instanceof PixelLiteral)
        {
            output.append(((PixelLiteral) declaration.expression).value + "px;\n");
        }
        else if (declaration.expression instanceof PercentageLiteral)
        {
            output.append(((PercentageLiteral) declaration.expression).value + "%;\n");
        }

        return output.toString();
    }

}
