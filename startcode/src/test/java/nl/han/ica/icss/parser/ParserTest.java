package nl.han.ica.icss.parser;

import nl.han.ica.icss.checker.Checker;
import nl.han.ica.icss.generator.Generator;
import nl.han.ica.icss.transforms.Evaluator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import nl.han.ica.icss.ast.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;

class ParserTest
{

    AST parseTestFile(String resource) throws IOException
    {
        //Open test file to parse
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream(resource);
        CharStream charStream = CharStreams.fromStream(inputStream);
        ICSSLexer lexer = new ICSSLexer(charStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        ICSSParser parser = new ICSSParser(tokens);
        parser.setErrorHandler(new BailErrorStrategy());

        //Setup collection of the parse error messages
        BaseErrorListener errorListener = new BaseErrorListener()
        {
            private String message;

            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
            {
                message = msg;
            }

            public String toString()
            {
                return message;
            }
        };
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        //Parse & extract AST
        ASTListener listener = new ASTListener();
        try
        {
            ParseTree parseTree = parser.stylesheet();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, parseTree);
        }
        catch (ParseCancellationException e)
        {
            fail(errorListener.toString());
        }

        return listener.getAST();
    }

    @Test
    void testParseLevel0() throws IOException
    {
        AST sut = parseTestFile("level0.icss");
        AST exp = Fixtures.uncheckedLevel0();
        assertEquals(exp, sut);
    }

    @Test
    void testParseLevel1() throws IOException
    {
        AST sut = parseTestFile("level1.icss");
        AST exp = Fixtures.uncheckedLevel1();
        assertEquals(exp, sut);
    }

    @Test
    void testParseLevel2() throws IOException
    {
        AST sut = parseTestFile("level2.icss");
        AST exp = Fixtures.uncheckedLevel2();
        assertEquals(exp, sut);
    }

    @Test
    void testParseLevel3() throws IOException
    {
        AST sut = parseTestFile("level3.icss");
        AST exp = Fixtures.uncheckedLevel3();
        assertEquals(exp, sut);
    }

    @Test
    void Checker_UnknownVariable_shouldReturnErrorMessage() throws IOException
    {
        AST sut = parseTestFile("level4_unknown_variable.icss");
        (new Checker()).check(sut);

        assertEquals(1, sut.getErrors().size());
        assertEquals("ERROR: Variable 'SomeUnknownVariable' is not defined in current scope.", sut.getErrors().get(0).toString());
    }

    @Test
    void Checker_MultiplyPixels_shouldReturnErrorMessage() throws IOException
    {
        AST sut = parseTestFile("level5_CH02_calculate_with_pixels.icss");
        (new Checker()).check(sut);

        assertEquals(1, sut.getErrors().size());
        assertEquals("ERROR: Multiply operation can only be used with a scalar and a non-scalar expression or scalar and scalar", sut.getErrors().get(0).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"level6_CH03_multiply_color.icss", "level6_CH03_subtract_color.icss", "level6_CH03_add_color.icss"})
    void Checker_CalculateWithColors_ShouldReturnErrorMessage(String icssFile) throws IOException
    {
        AST sut = parseTestFile(icssFile);
        (new Checker()).check(sut);

        assertEquals(1, sut.getErrors().size());
        assertEquals("ERROR: Color literals are not allowed in operations", sut.getErrors().get(0).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"level7_CH04_color_with_percentage_value.icss", "level7_CH04_color_with_pixel_value.icss"})
    void Checker_ColorPropertyWithIncorrectLiteral_ShouldReturnTwoErrorMessages(String icssFile) throws IOException
    {
        AST sut = parseTestFile(icssFile);
        (new Checker()).check(sut);

        assertEquals(2, sut.getErrors().size());
        assertEquals("ERROR: Color literals are not allowed in operations", sut.getErrors().get(0).toString());
        assertEquals("ERROR: Only color expressions are allowed for color", sut.getErrors().get(1).toString());
    }

    @Test
    void Checker_WidthWithHexColorValue_ShouldReturnTwoErrorMessages() throws IOException
    {
        AST sut = parseTestFile("level7_CH04_width_with_color_value.icss");
        (new Checker()).check(sut);

        assertEquals(2, sut.getErrors().size());
        assertEquals("ERROR: Color literals are not allowed in operations", sut.getErrors().get(0).toString());
        assertEquals("ERROR: Only pixel and percentage expressions are allowed for width", sut.getErrors().get(1).toString());
    }

    @Test
    void Checker_IfStatementWithNonBooleanClause_ShouldReturnErrorMessage() throws IOException
    {
        AST sut = parseTestFile("level8_ifstatement_boolean_variable.icss");
        (new Checker()).check(sut);

        assertEquals(1, sut.getErrors().size());
        assertEquals("ERROR: The if clause can only be of type boolean", sut.getErrors().get(0).toString());
    }

    @Test
    void Checker_UseVariableThatIsNotInScope_ShouldReturnErrorMessage() throws IOException
    {
        AST sut = parseTestFile("level9_variable_outside_scope.icss");
        (new Checker()).check(sut);

        assertEquals(2, sut.getErrors().size());
        assertEquals("ERROR: Variable 'WidthVar' is not defined in current scope.", sut.getErrors().get(0).toString());
        assertEquals("ERROR: Variable 'WidthVar' is not defined in current scope.", sut.getErrors().get(1).toString());
    }

    @Test
    void Evaluator_Apply_ASTShouldNotContainIfElseOrExpressionClasses() throws IOException
    {
        AST sut = parseTestFile("level3.icss");
        (new Evaluator()).apply(sut);

        walkthroughAstNode(sut.root);
    }

    @Test
    void Generator_Generate_ShouldGenerateSameAsExpectedCss() throws IOException
    {
        AST sut = parseTestFile("level0.icss");
        String css = (new Generator()).generate(sut);

        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = classLoader.getResourceAsStream("level0.icss");
        CharStream charStream = CharStreams.fromStream(inputStream);
        String original = charStream.toString();

        assertEquals(cleanString(original), cleanString(css));
    }

    private void walkthroughAstNode(ASTNode parentNode)
    {
        for (ASTNode node : parentNode.getChildren())
        {
            assertNotEquals(Expression.class, node.getClass());
            assertNotEquals(IfClause.class, node.getClass());
            assertNotEquals(ElseClause.class, node.getClass());

            walkthroughAstNode(node);
        }
    }

    private String cleanString(String css)
    {
        return css.replace("\n", "").replace("\r", "").replace("\t", "").replace(" ", "");
    }
}
