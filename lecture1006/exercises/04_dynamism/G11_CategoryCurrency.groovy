class Money {
    Integer amount
    String currency

    Money plus(Money other) {
        if (this.currency != other.currency) throw new IllegalArgumentException('Cannot add different currencies');
        new Money(amount: this.amount + other.amount, currency: this.currency)
    }

    public String toString() {
        "${amount} ${currency}"
    }
}

class MoneyCategory {
//TASK Define methods of the MoneyCategory class so that the code below passes

    // "geteur" is the "get;"
    static Money geteur(Integer self) {
        new Money(amount: self, currency: 'EUR')
    }

    // "getusd" is the "get;"
    static Money getusd(Integer self) {
        new Money(amount: self, currency: 'USD')
    }
}

use(MoneyCategory) {
    // "10.eur" is the same as "seteur" in "set;"
    println 10.eur
    println 10.eur + 20.eur
    println 10.usd + 20.usd

    // Uncomment this to see theh error
    // println 10.eur + 10.usd
}