package wtf.meier.tariff.interpreter

interface IVisitable {
    fun accept(visitor: IVisitor)
}