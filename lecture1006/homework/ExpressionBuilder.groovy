// 2025/2026
// TASK The MarkupBuilder in Groovy can transform a hierarchy of method calls and nested closures into a valid XML document.
// Create a NumericExpressionBuilder builder that will read a user-specified hierarchy of simple math expressions and build a tree representation of it.
// The basic arithmetics operations as well as the power (aka '^') operation must be supported.
// It will feature a toString() method that will pretty-print the expression tree into a string with the same semantics, as verified by the assert on the last line.
// This means that parentheses must be placed where necessary with respect to the mathematical operator priorities.
// Change or add to the code in the script. Reuse the infrastructure code at the bottom of the script.
class NumericExpressionBuilder extends BuilderSupport {
    Item root

    @Override
    protected void setParent(Object parent, Object child) {
        parent.children << child
    }

    @Override
    protected Object createNode(Object name) {
        return new Item(name.toString())
    }

    @Override
    protected Object createNode(Object name, Object val) {
        return new Item(name.toString(), val)
    }

    @Override
    protected Object createNode(Object name, Map attributes) {
        def item = new Item(name.toString())
        if (attributes.containsKey('value')) {
            item.value = attributes.value
        }
        return item
    }

    @Override
    protected Object createNode(Object name, Map attributes, Object value) {
        def item = new Item(name.toString())
        item.value = attributes?.value ?: value
        return item
    }

    @Override
    protected void nodeCompleted(Object parent, Object node) {
        if (parent == null) {
            root = node
        }
    }

    Item rootItem() {
        return root
    }
}

class Item {
    String op
    def value
    List<Item> children = []

    Item(String op) {
        this.op = op
    }

    Item(String op, def value) {
        this.op = op
        this.value = value
    }

    private static final Map<String, Integer> PRIORITY = [
        'number': 4,
        'variable': 4,
        'power': 3,
        '^': 3,
        '*': 2,
        '/': 2,
        '+': 1,
        '-': 1
    ]

    private int priority() {
        PRIORITY.get(op, 0)
    }

    @Override
    String toString() {
        if (op == 'number') return value.toString()
        if (op == 'variable') return value.toString()

        if (children.size() == 1) {
            return "${op}${children[0]}"
        }

        if (children.size() == 2) {
            def left = children[0]
            def right = children[1]
            def leftStr = left.toString()
            def rightStr = right.toString()

            if (left.priority() < this.priority()) {
                leftStr = "(${leftStr})"
            }

            if (right.priority() < this.priority()) {
                rightStr = "(${rightStr})"
            }

            def symbol = (op == 'power') ? '^' : op
            return "${leftStr} ${symbol} ${rightStr}"
        }
        return op
    }
}
//------------------------- Do not modify beyond this point!

def build(builder, String specification) {
    def binding = new Binding()
    binding['builder'] = builder
    new GroovyShell(binding).evaluate(specification)
}

//Custom expression to display. It should be eventually pretty-printed as 10 + x * (2 - 3) / 8 ^ (9 - 5)
String description = '''
builder.'+' {
    number(value: 10)
    '*' {
        variable(value: 'x')
        '/' {
            '-' {
                number(value: 2)
                number(value: 3)
            }
            power {
                number(value: 8)
                '-' {
                    number(value: 9)
                    number(value: 5)
                }
            }
        }
    }
}
'''

//XML builder building an XML document
build(new groovy.xml.MarkupBuilder(), description)

//NumericExpressionBuilder building a hierarchy of Items to represent the expression
def expressionBuilder = new NumericExpressionBuilder()
build(expressionBuilder, description)
def expression = expressionBuilder.rootItem()
println (expression.toString())
assert '10 + x * (2 - 3) / 8 ^ (9 - 5)' == expression.toString()