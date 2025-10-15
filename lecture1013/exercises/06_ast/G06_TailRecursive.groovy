@groovy.transform.TailRecursive
def fact(BigDecimal n, BigDecimal acc = 1) {
    // if (n < 2) 1
    // else n * fact(n - 1)
    if (n < 2) acc
    else fact(n - 1, n * acc)

}

println fact(5)

//TASK Make the function tail recursive so that it can pass the following line
println fact(10000)