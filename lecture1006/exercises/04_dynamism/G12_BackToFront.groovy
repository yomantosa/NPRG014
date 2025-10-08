String.metaClass.backToFront = {->
    delegate[-1..0]
}

println 'cimanyd si yvoorG'.backToFront()



//TASK define a starTrim() method to surround the original trimmed string with '*' 

def s = '   core   '
// Strign.metaClass
s.metaClass.starTrim = {-> "*${delegate.trim()}*"}
println s.respondsTo("starTrim")

assert '*core*' == s.starTrim()

println 'done'


















