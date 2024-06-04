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
        // Create the StringBuilder here once, so we only have to do a toString once.
        StringBuilder output = new StringBuilder();

        for (ASTNode child : node.getChildren())
        {
            if (child instanceof Stylerule)
            {
                generateStylerule(output, (Stylerule)child);
            }
        }

        return output.toString();
    }

    private void generateStylerule(StringBuilder output, Stylerule stylerule)
    {
        // Add all selectors, separated by a comma and a newline
        boolean addComma = false;
        for (Selector selector : stylerule.selectors)
        {
            if (addComma)
            {
                output.append(",\n");
            }

            output.append(selector.toString());

            // Only add commas on the second run
            addComma = true;
        }

        output.append(" {\n");

        for (ASTNode child : stylerule.body)
        {
            if (child instanceof Declaration)
            {
                generateDeclaration(output, (Declaration)child);
            }
        }

        // Add two newlines so the next expression will not be directly beneath the current one
        output.append("}\n\n");
        // No need to return because the StringBuilder is reference type and the values are already added to the StringBuilder.
    }

    private void generateDeclaration(StringBuilder output, Declaration declaration)
    {
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
    }
}
