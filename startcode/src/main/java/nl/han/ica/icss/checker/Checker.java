package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;


public class Checker
{

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast)
    {
        // Root should always be stylesheet
        variableTypes = new HANLinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(ASTNode node)
    {
        Stylesheet stylesheet = (Stylesheet) node;

        // Add new scope
        variableTypes.addFirst(new HashMap<>());

        // Check all children
        for (ASTNode child : stylesheet.getChildren())
        {
            if (child instanceof VariableAssignment)
            {
                // Cast to VariableAssignment
                VariableAssignment variableAssignment = (VariableAssignment) child;
                checkVariableAssignment(variableAssignment);
            } else if (child instanceof Stylerule)
            {
                // Cast to Stylerule
                Stylerule stylerule = (Stylerule) child;
                checkStyleRule(stylerule);
            } else
            {
                child.setError("Stylesheet can only contain variable assignments and style rules on root level");
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment)
    {
        variableTypes.getFirst().put(variableAssignment.name.name, getExpressionType(variableAssignment.expression));
    }

    private ExpressionType getExpressionType(Expression expression)
    {
        if (expression instanceof ColorLiteral)
        {
            return ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral)
        {
            return ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral)
        {
            return ExpressionType.PERCENTAGE;
        } else if (expression instanceof ScalarLiteral)
        {
            return ExpressionType.SCALAR;
        } else if (expression instanceof BoolLiteral)
        {
            return ExpressionType.BOOL;
        } else if (expression instanceof VariableReference)
        {
            return checkVariableReference((VariableReference) expression);
        }
        return ExpressionType.UNDEFINED;
    }

    private ExpressionType checkVariableReference(VariableReference variableReference)
    {
        for (HashMap<String, ExpressionType> variable : variableTypes)
        {
            if (variable.containsKey(variableReference.name))
            {
                return variable.get(variableReference.name);
            }
        }

        variableReference.setError(String.format("Variable %s does not exist.", variableReference.name));
        return ExpressionType.UNDEFINED;
    }

    private void checkStyleRule(Stylerule stylerule)
    {

    }
}
