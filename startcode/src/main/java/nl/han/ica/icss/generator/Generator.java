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
        if (ast.root instanceof Stylesheet)
        {
            return generateStylesheet(ast.root);
        }
        else
        {
            throw new RuntimeException("First object in AST should be of type Stylesheet");
        }
    }

    private String generateStylesheet(Stylesheet node)
    {
        StringBuilder output = new StringBuilder();

        for (ASTNode child : node.getChildren())
        {
            if (child instanceof Stylerule)
            {
                output.append(generateStylerule((Stylerule)child));
            }
        }

        return output.toString();
    }

    private String generateStylerule(Stylerule stylerule)
    {
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
                output.append(generateDeclaration((Declaration)child));
            }
        }

        output.append("}\n\n");
        return output.toString();
    }

    private String generateDeclaration(Declaration declaration)
    {
        StringBuilder output = new StringBuilder();

        // GE02: Add 2 spaces per scope level.
        output.append(String.format("  %s: ", declaration.property.name));

        if (declaration.expression instanceof ColorLiteral)
        {
            output.append(String.format("%s;\n", ((ColorLiteral) declaration.expression).value));
        }
        else if (declaration.expression instanceof PixelLiteral)
        {
            output.append(String.format("%spx;\n", ((PixelLiteral) declaration.expression).value));
        }
        else if (declaration.expression instanceof PercentageLiteral)
        {
            output.append(String.format("%s%%;\n", ((PercentageLiteral) declaration.expression).value));
        }

        return output.toString();
    }

}
