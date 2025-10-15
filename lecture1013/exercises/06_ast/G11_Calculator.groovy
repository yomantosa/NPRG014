import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformationClass
import static org.codehaus.groovy.control.CompilePhase.SEMANTIC_ANALYSIS
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.control.messages.SyntaxErrorMessage

import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.ClassHelper




@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass("UnsupportedTransformation")
public @interface Unsupported {}


@GroovyASTTransformation(phase = SEMANTIC_ANALYSIS)
public class UnsupportedTransformation implements ASTTransformation {

    public void visit(ASTNode[] astNodes, SourceUnit source) {
        //...
        // TASK The "Unsupported" transformation should make the annotated methods throw the UnsupportedOperationException
        // Fill in the missing AST generation code to make the script pass
        // You can take inspiration from exercises
        // TASK Report an error when the method declared as @Unsupported has nonnempty body - e.g. multiply()
        // Documentation and hints:
        // http://docs.groovy-lang.org/docs/next/html/documentation/
        // http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/package-summary.html
        // http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/expr/package-summary.html
        // http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/stmt/package-summary.html
        // http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/tools/package-summary.html        
        // http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/tools/GeneralUtils.html        

        if (!astNodes || astNodes.length < 2) return

        ASTNode annotationNode = astNodes[0]
        ASTNode methodNode = astNodes[1]
        if (!(methodNode instanceof MethodNode)) return
        MethodNode method = (MethodNode) methodNode

        // --- Check for non-empty body ---
        if (method.code instanceof BlockStatement && !((BlockStatement) method.code).statements.isEmpty()) {
            addError("Method '${method.name}' annotated with @Unsupported must not have a body", method, source)
            return
        }

        // --- Replace body with 'throw new UnsupportedOperationException("The <name> operation is not supported")' ---
        def msgExpr = new ConstantExpression("The ${method.name} operation is not supported".toString())
        def newException = new ConstructorCallExpression(
                ClassHelper.make(UnsupportedOperationException),
                new ArgumentListExpression(msgExpr)
        )
        def throwStmt = new ThrowStatement(newException)

        def newBody = new BlockStatement()
        newBody.addStatement(throwStmt)
        method.setCode(newBody)
    }

    def createStatements(String clause) {
        def statements = """
            if(!($clause)) {
                throw new Exception('Precondition violated: {$clause}')
            }
        """

        AstBuilder ab = new AstBuilder()
        List<ASTNode> res = ab.buildFromString(CompilePhase.SEMANTIC_ANALYSIS, statements)
        BlockStatement bs = res[0]
        return bs
    }

    public void addError(String msg, ASTNode expr, SourceUnit source) {
        int line = expr.lineNumber
        int col = expr.columnNumber
        SyntaxException se = new SyntaxException(msg + '\n', line, col)
        SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
        source.errorCollector.addErrorAndContinue(sem)
    }
}

final calculator = new GroovyShell(this.class.getClassLoader()).evaluate('''
class Calculator {
    def plus(a, b) {
        a + b
    }

    @Unsupported
    def minus(a, b) {}

    @Unsupported
    def divide(a, b) {}

    @Unsupported
    def multiply(a, b) {
        a * b
    }
}

new Calculator()
''')

assert 5 == calculator.plus(2, 3)
try {
    calculator.minus(10, 5)
    assert false, "Exception should have been thrown since the minus method is not supported"
} catch (RuntimeException e) {
    assert e instanceof UnsupportedOperationException
    assert e.message == 'The minus operation is not supported'
}
try {
    calculator.divide(10, 5)
    assert false, "Exception should have been thrown since the divide method is not supported"
} catch (RuntimeException e) {
    assert e instanceof UnsupportedOperationException
    assert e.message == 'The divide operation is not supported'
}

println 'well done'