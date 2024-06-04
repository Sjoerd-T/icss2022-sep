package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform
{
    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator()
    {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast)
    {
        variableValues = new HANLinkedList<>();
        evaluateStylesheet(ast.root);
    }

    private void evaluateStylesheet(ASTNode node)
    {
        // Add new scope
        variableValues.addFirst(new HashMap<>());

        ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
        for (ASTNode child : node.getChildren())
        {
            if (child instanceof Stylerule)
            {
                evaluateStylerule((Stylerule) child);
            }
            else if (child instanceof VariableAssignment)
            {
                evaluateVariableAssignment((VariableAssignment) child);
                nodesToRemove.add(child);
            }
        }

        // Remove all children from nodesToRemove, they are repl
        for (ASTNode child : nodesToRemove)
        {
            node.removeChild(child);
        }

        // Remove scope
        variableValues.removeFirst();
    }

    private void evaluateStylerule(Stylerule stylerule)
    {
        // Add new scope
        variableValues.addFirst(new HashMap<>());
        evaluateStyleruleBody(stylerule.body);

        // Remove scope
        variableValues.removeFirst();
    }

    private void evaluateStyleruleBody(ArrayList<ASTNode> nodes)
    {
        ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
        ArrayList<ASTNode> nodesToAdd = new ArrayList<>();
        for (ASTNode child : nodes)
        {
            if (child instanceof Declaration)
            {
                evaluateDeclaration((Declaration) child);
            }
            else if (child instanceof IfClause)
            {
                nodesToAdd.addAll(evaluateIfClause((IfClause) child));
                nodesToRemove.add(child);
            }
            else if (child instanceof VariableAssignment)
            {
                evaluateVariableAssignment((VariableAssignment) child);
                nodesToRemove.add(child);
            }
        }

        // Remove all the nodes that are not needed anymore
        for (ASTNode node : nodesToRemove)
        {
            nodes.remove(node);
        }

        // Add if-clause bodies to stylerule body
        nodes.addAll(nodesToAdd);
    }

    private void evaluateDeclaration(Declaration declaration)
    {
        declaration.expression = evaluateExpression(declaration.expression);
    }

    // TR02: Evaluate if clauses
    private ArrayList<ASTNode> evaluateIfClause(IfClause ifClause)
    {
        if (ifClause == null || ifClause.conditionalExpression == null)
        {
            // Preferable throw an exception because the outcome can be different from what someone expect.
            return new ArrayList<>();
        }

        // Evaluate condition
        boolean ifClauseIsTrue = ((BoolLiteral) evaluateExpression(ifClause.conditionalExpression)).value;

        if (ifClauseIsTrue)
        {
            // Evaluate body of if-clause
            evaluateStyleruleBody(ifClause.body);
            return ifClause.body;
        }

        if (ifClause.elseClause == null)
        {
            return new ArrayList<>();
        }

        // Evaluate body of else clause
        evaluateStyleruleBody(ifClause.elseClause.body);
        return ifClause.elseClause.body;
    }

    private void evaluateVariableAssignment(VariableAssignment variableAssignment)
    {
        // Make value of variable available in current scope
        variableValues.getFirst().put(variableAssignment.name.name, evaluateExpression(variableAssignment.expression));
    }

    // TR01: Change expressions to literals
    private Literal evaluateExpression(ASTNode node)
    {
        if (node instanceof VariableReference)
        {
            return evaluateVariableReference((VariableReference) node);
        }
        else if (node instanceof Operation)
        {
            return evaluateOperation((Operation) node);
        }
        else if (node instanceof Literal)
        {
            return (Literal) node;
        }

        return null;
    }

    private Literal evaluateVariableReference(VariableReference variableReference)
    {
        // Find variable value
        for (HashMap<String, Literal> scope : variableValues)
        {
            if (scope.containsKey(variableReference.name))
            {
                return scope.get(variableReference.name);
            }
        }

        return null;
    }

    private Literal evaluateOperation(Operation operation)
    {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        if (operation instanceof AddOperation)
        {
            return evaluateAddOperation(left, right);
        }
        else if (operation instanceof SubtractOperation)
        {
            return evaluateSubtractOperation(left, right);
        }
        else if (operation instanceof MultiplyOperation)
        {
            return evaluateMultiplyOperation(left, right);
        }

        return null;
    }

    private Literal evaluateAddOperation(Literal left, Literal right)
    {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral)
        {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        }
        else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral)
        {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
        {
            return new ScalarLiteral(((ScalarLiteral) left).value + ((ScalarLiteral) right).value);
        }

        return null;
    }

    private Literal evaluateSubtractOperation(Literal left, Literal right)
    {
        if (left instanceof PixelLiteral && right instanceof PixelLiteral)
        {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        }
        else if (left instanceof PercentageLiteral && right instanceof PercentageLiteral)
        {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
        {
            return new ScalarLiteral(((ScalarLiteral) left).value - ((ScalarLiteral) right).value);
        }

        return null;
    }

    private Literal evaluateMultiplyOperation(Literal left, Literal right)
    {
        if (left instanceof PixelLiteral && right instanceof ScalarLiteral)
        {
            return new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
        }
        else if (left instanceof PercentageLiteral && right instanceof ScalarLiteral)
        {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
        }
        else if (left instanceof ScalarLiteral && right instanceof ScalarLiteral)
        {
            return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
        }

        return null;
    }
}
