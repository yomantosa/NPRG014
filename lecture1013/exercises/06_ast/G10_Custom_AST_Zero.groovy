import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformationClass
import groovyjarjarasm.asm.Opcodes
import static org.codehaus.groovy.control.CompilePhase.SEMANTIC_ANALYSIS

import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.FieldNode



@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass("ZeroTransformation")
public @interface Zero {}

//TASK Complete the transformation code using direct API manipulation at the indicated position so the the test passes
// Documentation and hints:
// http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/package-summary.html
// http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/expr/package-summary.html
// http://docs.groovy-lang.org/docs/groovy-latest/html/api/org/codehaus/groovy/ast/stmt/package-summary.html

@GroovyASTTransformation(phase = SEMANTIC_ANALYSIS)
public class ZeroTransformation implements ASTTransformation {

    public void visit(ASTNode[] astNodes, SourceUnit source) {
        if (!astNodes) return
        def annotationNode = astNodes[0]
        def annotatedNode = astNodes[1]

        if (!(annotatedNode instanceof ClassNode)) return

        ClassNode classNode = (ClassNode) annotatedNode

        // Add a public int field named 'zero' initialized to 0
        def zeroField = new FieldNode(
            "zero",                             // name
            Opcodes.ACC_PUBLIC,                 // modifiers
            ClassHelper.int_TYPE,               // type
            classNode,                          // owner
            new ConstantExpression(0)           // initial value
        )

        // Only add if not already defined
        if (!classNode.hasProperty("zero")) {
            classNode.addField(zeroField)
        }
    }
}

final calculator = new GroovyShell(this.class.getClassLoader()).evaluate('''
@Zero
class Calculator {}

new Calculator()
''')

assert 0 == calculator.zero

println 'done'